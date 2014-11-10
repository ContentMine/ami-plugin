package org.xmlcml.xhtml2stm.visitable.html;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.util.HtmlUtil;
import org.xmlcml.xhtml2stm.util.Util;
import org.xmlcml.xhtml2stm.visitable.AbstractVisitable;

public class HtmlVisitable extends AbstractVisitable  {

	private final static Logger LOG = Logger.getLogger(HtmlVisitable.class);

	private static final String[] extensions = {Util.HTM, Util.HTML};
	public static final String ITALIC_XPATH = ".//*[local-name()='i']";
	
	private List<HtmlContainer> htmlContainerList;
	
	public HtmlVisitable() {
		
	}
	
	@Override
	public void addFile(File htmlFile) throws Exception {
		ensureHtmlContainerList();
		HtmlContainer htmlContainer = new HtmlContainer(htmlFile, HtmlUtil.readAndCreateElement(htmlFile));
		htmlContainerList.add(htmlContainer);
	}

	@Override
	public void addURL(URL url) throws Exception {
		ensureHtmlContainerList();
		HtmlContainer htmlContainer = new HtmlContainer(url, HtmlUtil.readAndCreateElement(url));
		htmlContainerList.add(htmlContainer);
	}

	private void ensureHtmlContainerList() {
		if (htmlContainerList == null) {
			htmlContainerList = new ArrayList<HtmlContainer>();
		}
	}
	
	public List<HtmlContainer> getHtmlContainerList() {
		ensureHtmlContainerList();
		if (htmlContainerList.size() > 0) {
			LOG.debug("htmlContainers: "+htmlContainerList.size());
			// explicit containers already
		} else if (super.findFilesInDirectories() != null) {
			htmlContainerList = createContainersFromFiles();
		} 
		return htmlContainerList;
	}

	public List<HtmlContainer> createContainersFromFiles() {
		htmlContainerList = new ArrayList<HtmlContainer>();
		if (fileList != null) {
			for (File file : fileList) {
				try {
					HtmlElement htmlElement = HtmlUtil.readAndCreateElement(file);
					htmlContainerList.add(new HtmlContainer(file, htmlElement));
				} catch (Exception e) {
					LOG.error("Not an HTML file: "+file);
				}
			}
		}
		return htmlContainerList;
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
