package org.xmlcml.xhtml2stm.visitable.html;

import java.io.File;
import java.net.URL;
import java.util.List;

import nu.xom.Element;

import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.util.HtmlUtil;
import org.xmlcml.xhtml2stm.visitable.VisitableContainer;

public class HtmlContainer extends VisitableContainer {

	private HtmlElement htmlElement;
	private String citation;
	public HtmlContainer(File file, HtmlElement htmlElement) {
		this.file = file;
		this.htmlElement = htmlElement;
	}

	public HtmlContainer(URL url, HtmlElement htmlElement) {
		this.url = url;
		this.htmlElement = htmlElement;
	}

	public HtmlElement getHtmlElement() {
		return htmlElement;
	}
	
	@Override
	public Element getElement() {
		return getHtmlElement();
	}
	
	public String getLocation() {
		return file == null ? null : file.getAbsolutePath();
	}

	/** current only does BMC.
	 * 
	 * Needs changing for other journals.
	 * 
	 * @return
	 */
	@Override
	public String getDoi() {
		if (doi == null) {
			if (htmlElement != null) {
				List<HtmlElement> nodeList = HtmlUtil.getQueryHtmlElements(htmlElement, ".//h:*[starts-with(.,'doi') and contains(.,'Cite')]");
				if (nodeList.size() > 0 ) {
					doi = String.valueOf(nodeList.get(0).getValue());
					// trim trailing
					doi = doi.split("\\s+")[0];
				}
			}
		}
		return doi;
	}
	
	@Override
	public String getName() {
		return file == null ? null : file.getAbsolutePath();
	}
}
