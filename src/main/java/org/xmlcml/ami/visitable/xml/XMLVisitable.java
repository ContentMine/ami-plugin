package org.xmlcml.ami.visitable.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.AbstractVisitable;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.xml.XMLUtil;

public class XMLVisitable extends AbstractVisitable  {

	private final static Logger LOG = Logger.getLogger(XMLVisitable.class);

	private static final String XML = "xml";
	public static final String ITALIC= ".//*[local-name()='italic' or local-name()='i' or local-name()='it']";

	private List<XMLContainer> xmlContainerList;
	private Element xmlElement;
	private File xmlFile;

	public XMLVisitable() {
		
	}

	@Override
	// FIXME
	public void downloadParseAndAddURL(URL url) throws Exception {
		super.addURL(url);
		ensureXMLContainerList();
		try {
			Document document = new Builder().build(url.openStream());
			XMLContainer xmlContainer = new XMLContainer(url, document.getRootElement()); 
			xmlContainerList.add(xmlContainer);
		} catch (Throwable t) {
			LOG.error("Cannot parse document: "+url+" ("+t+") ");
		}
	}
	
	@Override
	public void addFile(File xmlFile) throws Exception {
		this.xmlFile = xmlFile;
		checkFile(xmlFile);
		parseContentToContainerAndAddToList(xmlFile);
	}

	/** parse content.
	 * 
	 * messy signature as we pass both URLs and HTML files.
	 * 
	 * Therefore contains unpleasant if instanceof clauses
	 * 
	 * @param object
	 * @throws Exception
	 */
	private void parseContentToContainerAndAddToList(Object object) throws Exception {
		ensureXMLContainerList();
		xmlElement = parseToElement(object);
//		addTags(xmlElement);
		List<Element> xmlElements = splitByXPath(xmlElement);
		for (Element subXmlElement : xmlElements) {
			XMLContainer xmlContainer = createContainer(object, subXmlElement);
			xmlContainerList.add(xmlContainer);
		}
		LOG.debug(this.hashCode()+" XML containerList size now "+xmlContainerList.size());
	}
	
	// FIXME possibly move up
	private Element parseToElement(Object object) throws Exception {
		xmlElement = null;
		if (object instanceof File) {
			File xmlFile = (File) object;
			LOG.debug("MUST STRIP DTD");
			xmlElement = new Builder().build(new FileInputStream(xmlFile)).getRootElement();
			if (xmlElement == null) {
				throw new RuntimeException("cannot parse HTML file: "+xmlFile.getAbsolutePath());
			}
		} else if (object instanceof URL) {
			url = (URL) object;
			xmlElement = new Builder().build(url.openStream()).getRootElement();
			if (xmlElement == null) {
				throw new RuntimeException("cannot parse URL: "+url);
			}
		}
		return xmlElement;
	}

	// FIXME possibly move up?
	private List<Element> splitByXPath(Element xmlElement) {
		List<Element> xmlElementList = new ArrayList<Element>();
		String xPath = (xPathProcessor == null) ? null : xPathProcessor.getXPath();
		if (xPath == null) {
			xmlElementList.add(xmlElement);
		} else {
			xmlElementList = XMLUtil.getQueryElements(xmlElement, xPath);
//			for (Element element : elementList) {
//				xmlElementList.add(element);
//			}
		}
		LOG.debug("after xpath ("+xPath+") split: "+xmlElementList.size());
		for (Element xmlElem : xmlElementList) {
			LOG.trace("X "+xmlElem.toXML());
		}
		
		return xmlElementList;
	}


	private void ensureXMLContainerList() {
		if (xmlContainerList == null) {
			xmlContainerList = new ArrayList<XMLContainer>();
		}
	}

	private XMLContainer createContainer(Object object, Element xmlElement) {
		XMLContainer xmlContainer = null;
		if (object instanceof File) {
			xmlContainer = new XMLContainer((File) object, xmlElement);
		} else if (object instanceof URL) {
			xmlContainer = new XMLContainer((URL) object, xmlElement);
		} else {
			throw new RuntimeException("Cannot create XMLContainer for "+object.getClass());
		}
		return xmlContainer;
	}


	public List<XMLContainer> getXMLContainerList() {
		ensureXMLContainerList();
		if (url != null) {
			xmlContainerList = createContainerFromUrl();
		} else if (xmlFile != null) {
//			xmlContainerList = createContainerFromFile();
		} else if (fileList != null) {
			xmlContainerList = createContainersFromFiles();
			throw new RuntimeException("Need to deal with splitting files");
		} else {
			throw new RuntimeException("No XML input");
		}
		return xmlContainerList;
	}

	private List<XMLContainer> createContainerFromUrl() {
		xmlContainerList = new ArrayList<XMLContainer>();
		if (url != null) {
			try {
				Element xmlElement = stripDTDAndParse(url);
				xmlContainerList.add(new XMLContainer(url, xmlElement));
			} catch (Exception e) {
				LOG.error("not an XML file: "+url+ ": " + e);
			}
			
		}
		return xmlContainerList;
	}

	private Element stripDTDAndParse(File file) throws IOException {
		String xmlString = FileUtils.readFileToString(file);
		Element xmlElement = XMLUtil.stripDTDAndParse(xmlString);
		return xmlElement;
	}

	private Element stripDTDAndParse(URL url) throws IOException {
		String xmlString = IOUtils.toString(url);
		Element xmlElement = XMLUtil.stripDTDAndParse(xmlString);
		return xmlElement;
	}

	public List<XMLContainer> createContainersFromFiles() {
		xmlContainerList = new ArrayList<XMLContainer>();
		if (fileList != null) {
			for (File file : fileList) {
				try {
					XMLContainer container = createContainerFromFile0(file);
					xmlContainerList.add(container);
				} catch (Exception e) {
					LOG.error("file: "+file+"; "+file.exists());
					LOG.error("not an XML file: "+file+ ": " + e);
				}
			}
		}
		return xmlContainerList;
		
	}

	private List<XMLContainer> createContainerFromFile() {
		xmlContainerList = new ArrayList<XMLContainer>();
		try {
			XMLContainer container = createContainerFromFile0(xmlFile);
			xmlContainerList.add(container);
		} catch (Exception e) {
			LOG.error("not an XML file: "+url+ ": " + e);
		}
		return xmlContainerList;
	}

	private XMLContainer createContainerFromFile0(File file) throws IOException {
		Element xmlElement = XMLUtil.stripDTDAndParse(FileUtils.readFileToString(file));
		XMLContainer container = new XMLContainer(file, xmlElement);
		return container;
	}

	@Override
	public String[] getExtensions() {
		return new String[] {"xml"};
	}

	@Override
	public void getMetadata() {
		for (XMLContainer xmlContainer : xmlContainerList) {
			getMetadata(xmlContainer.getElement());
		}
	}

	private void getMetadata(Element xml) {
		//FIXME
	}
	
	public static boolean hasSuffix(String suffix) {
		return XML.equalsIgnoreCase(suffix);
	}
	
}
