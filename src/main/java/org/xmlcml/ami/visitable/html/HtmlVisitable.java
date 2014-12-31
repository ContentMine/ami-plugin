package org.xmlcml.ami.visitable.html;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.ami.util.AMIUtil;
import org.xmlcml.ami.visitable.AbstractVisitable;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlFactory;
import org.xmlcml.xml.XMLUtil;

public class HtmlVisitable extends AbstractVisitable  {

	public final static Logger LOG = Logger.getLogger(HtmlVisitable.class);

	private static final String[] extensions = {AMIUtil.HTM, AMIUtil.HTML};
	public static final String ITALIC_XPATH = ".//*[local-name()='i']";
	
	private List<HtmlContainer> htmlContainerList;
	private HtmlFactory htmlFactory;
	private HtmlElement htmlElement;
	
	public HtmlVisitable() {
		super();
	}
	
	@Override
	public void addFile(File htmlFile) throws Exception {
		checkFile(htmlFile);
		parseContentToContainerAndAddToList(htmlFile);
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
		ensureHtmlContainerList();
		ensureHtmlFactory();
		HtmlElement htmlElement = parseToElement(object);
//		addTags(htmlElement);
		List<HtmlElement> htmlElements = splitByXPath(htmlElement);
		for (HtmlElement subHtmlElement : htmlElements) {
			HtmlContainer htmlContainer = createContainer(object, subHtmlElement);
			htmlContainerList.add(htmlContainer);
		}
	}

	private List<HtmlElement> splitByXPath(HtmlElement htmlElement) {
		List<HtmlElement> htmlElementList = new ArrayList<HtmlElement>();
		String xPath = (xPathProcessor == null) ? null : xPathProcessor.getXPath();
		if (xPath == null) {
			htmlElementList.add(htmlElement);
		} else {
			List<Element> elementList = XMLUtil.getQueryElements(htmlElement, xPath);
			for (Element element : elementList) {
				htmlElementList.add((HtmlElement) element);
			}
		}
		LOG.trace("after xpath split: "+htmlElementList.size());
		return htmlElementList;
	}

	private HtmlContainer createContainer(Object object, HtmlElement htmlElement) {
		HtmlContainer htmlContainer = null;
		if (object instanceof File) {
			htmlContainer = new HtmlContainer((File) object, htmlElement);
		} else if (object instanceof URL) {
			htmlContainer = new HtmlContainer((URL) object, htmlElement);
		} else {
			throw new RuntimeException("Cannot create HtmlContainer for "+object.getClass());
		}
		return htmlContainer;
	}

	private HtmlElement parseToElement(Object object) throws Exception {
		htmlElement = null;
		if (object instanceof File) {
			File htmlFile = (File) object;
			htmlElement = htmlFactory.parse(htmlFile);
			if (htmlElement == null) {
				throw new RuntimeException("cannot parse HTML file: "+htmlFile.getAbsolutePath());
			}
		} else if (object instanceof URL) {
			URL url = (URL) object;
			htmlElement = htmlFactory.parse(url);
			if (htmlElement == null) {
				throw new RuntimeException("cannot parse URL: "+url);
			}
		}
		return htmlElement;
	}

	@Override
	public void downloadParseAndAddURL(URL url) throws Exception {
		LOG.debug("downloading "+url);
		super.addURL(url);
		parseContentToContainerAndAddToList(url);
	}

	private void ensureHtmlContainerList() {
		if (htmlContainerList == null) {
			htmlContainerList = new ArrayList<HtmlContainer>();
		}
	}
	
	public List<HtmlContainer> getHtmlContainerList() {
		ensureHtmlContainerList();
		if (htmlContainerList.size() > 0) {
			LOG.trace("htmlContainers: "+htmlContainerList.size());
			// explicit containers already
		} else if (super.findFilesInDirectories() != null) {
			htmlContainerList = createContainersFromFiles();
		} 
		return htmlContainerList;
	}

	public List<HtmlContainer> createContainersFromFiles() {
		htmlContainerList = new ArrayList<HtmlContainer>();
		if (fileList != null) {
			ensureHtmlFactory();
			for (File file : fileList) {
				try {
					addFile(file);
				} catch (Exception e) {
					LOG.error("Cannot parse file: "+file+"; "+e);
				}
			}
		}
		return htmlContainerList;
	}

	private void ensureHtmlFactory() {
		if (htmlFactory == null) {
			htmlFactory = new HtmlFactory();
			// this should be per publisher...
			htmlFactory.addTagToDelete("script");
			htmlFactory.addTagToDelete("button");
			htmlFactory.addAttributeToDelete("onclick");
			htmlFactory.addAttributeToDelete("alt"); // often filled with rubbish
			htmlFactory.addMissingNamespacePrefix("g"); // from biomed central
		}
	}

	public String[] getExtensions() {
		return new String[] {"htm", "html"};
	}

	@Override
	public void getMetadata() {
		this.searchContainers();
	}

	private void searchContainers() {
		for (HtmlContainer htmlContainer : this.getHtmlContainerList()) {
			getMetadata(htmlContainer.getHtmlElement());
		}
	}

	private void getMetadata(HtmlElement html) {
		// FIXME
		
	}
	
	/*public void retrieveContainers() {
		List<VisitableContainer> crawler.retrieveContainers();
	}

	public static boolean hasSuffix(String suffix) {
		return HTM.equalsIgnoreCase(suffix) ||
			HTML.equalsIgnoreCase(suffix);
	}
	
	private List<Text> makeTextList(HtmlElement htmlElement) {
		Nodes nodes = htmlElement.query(".//text()");
		List<Text> textList = new ArrayList<Text>();
		for (int i = 0; i < nodes.size(); i++) {
			textList.add((Text)nodes.get(i));
		}
		return textList;
	}

	private List<Text> normalizeItalic(List<Text> texts) {
		for (int i = texts.size() - 1; i > 0; i--) {
			Text texti = texts.get(i);
			if (texti.getParent() instanceof HtmlI) {
				if (i >= 0) {
					i = mergePrecedingWhiteSpaceAndDecrement(texts.get(i - 1), i, texti);
				}
				if (i > 0) {
					i = mergeWithPrecedingTextAndDecrement(texts.get(i - 1), i, texti);
				}
			}
			
		}
		return texts;
	}

	private int mergePrecedingWhiteSpaceAndDecrement(Text preceding, int textPointer, Text texti) {
		String value = preceding.getValue();
		// whitespace? merge with this and delete
		if (value.trim().length() == 0) {
			String newValue = (new StringBuilder(value).append(texti.getValue())).toString();
			LOG.trace(newValue);
			texti.setValue(newValue);
			preceding.detach();
			textPointer--;
		}
		return textPointer;
	}

	private int mergeWithPrecedingTextAndDecrement(Text preceding, int textPointer, Text texti) {
		if (preceding.getParent() instanceof HtmlI) {
			String newValue = preceding.getValue() + texti.getValue();
			preceding.setValue(newValue);
			texti.detach();
			textPointer--;
		}
		return textPointer;
	}


	private List<HtmlElement> dehyphenate(List<HtmlElement> htmlElements) {
		for (int i = htmlElements.size() - 1; i > 0; i--) {
			
		}
		return  null;
	}

	private List<HtmlElement> mergeOrContractSpaces(List<HtmlElement> htmlElements) {
		// TODO Auto-generated method stub
		return null;
	}*/

}
