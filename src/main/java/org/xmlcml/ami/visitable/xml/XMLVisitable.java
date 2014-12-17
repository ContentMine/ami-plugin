package org.xmlcml.ami.visitable.xml;

import java.io.File;
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
import org.xmlcml.xml.XMLUtil;

public class XMLVisitable extends AbstractVisitable  {

	private final static Logger LOG = Logger.getLogger(XMLVisitable.class);

	private static final String XML = "xml";
	public static final String ITALIC= ".//*[local-name()='italic' or local-name()='i' or local-name()='it']";

	private List<XMLContainer> xmlContainerList;

	public XMLVisitable() {
		
	}

	@Override
	public void addFile(File file) throws Exception {
		checkFile(file);
		ensureXMLContainerList();
		ensureFileList();
		try {
			this.fileList.add(file);
			Element root = XMLUtil.stripDTDAndParse(FileUtils.readFileToString(file));
			XMLContainer xmlContainer = new XMLContainer(file, root); 
			xmlContainerList.add(xmlContainer);
		} catch (Throwable t) {
			LOG.error("Cannot parse document: "+file+" ("+t+") ");
		}
	}

	@Override
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

	private void ensureXMLContainerList() {
		if (xmlContainerList == null) {
			xmlContainerList = new ArrayList<XMLContainer>();
		}
	}

	public List<XMLContainer> getXMLContainerList() {
		ensureXMLContainerList();
		if (url != null) {
			xmlContainerList = createContainerFromUrl();
		} else if (super.findFilesInDirectories() != null) {
			xmlContainerList = createContainersFromFiles();
		} 
		return xmlContainerList;
	}

	private List<XMLContainer> createContainerFromUrl() {
		xmlContainerList = new ArrayList<XMLContainer>();
		if (url != null) {
			try {
				String xmlString = IOUtils.toString(url);
				Element xmlElement = XMLUtil.stripDTDAndParse(xmlString);
				xmlContainerList.add(new XMLContainer(url, xmlElement));
			} catch (Exception e) {
				LOG.error("not an XML file: "+url+ ": " + e);
			}
			
		}
		return xmlContainerList;
	}

	public List<XMLContainer> createContainersFromFiles() {
		xmlContainerList = new ArrayList<XMLContainer>();
		if (fileList != null) {
			for (File file : fileList) {
				try {
//					Element xmlElement = XMLUtil.parseQuietlyToDocument(file).getRootElement();
					Element xmlElement = XMLUtil.stripDTDAndParse(FileUtils.readFileToString(file));
					xmlContainerList.add(new XMLContainer(file, xmlElement));
				} catch (Exception e) {
					LOG.error("file: "+file+"; "+file.exists());
					LOG.error("not an XML file: "+file+ ": " + e);
				}
			}
		}
		return xmlContainerList;
		
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
