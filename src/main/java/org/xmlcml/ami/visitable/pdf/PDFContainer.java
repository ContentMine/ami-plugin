package org.xmlcml.ami.visitable.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.VisitableContainer;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.ami.visitable.svg.SVGContainer;
import org.xmlcml.html.HtmlElement;

public class PDFContainer extends VisitableContainer {

	private static final Logger LOG = Logger.getLogger(PDFContainer.class);
	
	private HtmlContainer htmlContainer;
	private List<SVGContainer> svgContainerList;
	
	public PDFContainer(File file) {
		this.file = file;
	}

	public PDFContainer(URL url) {
		this.url = url;
	}

	public PDFContainer(File file, HtmlElement htmlElement) {
		this(file);
		htmlContainer = new HtmlContainer(file, htmlElement);
		
	}

	@Override
	public Element getElement() {
		return htmlContainer == null ? null : htmlContainer.getElement();
	}

	/**
	 * <article-id pub-id-type="doi">10.1371/journal.pone.0080753</article-id>
	 */
	@Override
	public String getDoi() {
		if (doi == null) {
			doi = (htmlContainer == null) ? null : htmlContainer.getDoi();
		}
		return doi;
	}
	
	@Override
	public String getName() {
		return file == null ? null : file.getAbsolutePath();
	}

	public HtmlContainer getHtmlContainer() {
		return htmlContainer;
	}

	public List<SVGContainer> getSVGListContainer() {
		return svgContainerList;
	}

	public void processFile() {
		LOG.debug("Processing PDF file: "+file);
		try {
			convertToXHTMLAndSVG(new FileInputStream(file));
		} catch (Exception e) {
			LOG.error("cannot read/processPDF file: "+file+" : ", e);
		}
	}

	private void convertToXHTMLAndSVG(InputStream is) throws Exception {
		PDF2XHTMLConverter converter = new PDF2XHTMLConverter();
		HtmlElement htmlElement = converter.readAndConvertToXHTML(is);
		htmlContainer = new HtmlContainer(file, htmlElement);
		LOG.debug("Processed PDF file: "+file);
		//Might also add SVG here
		//svgContainerList = new ArrayList<SVGContainer>();
		//pdfContainer.addSVGList(svgList);
	}

	public void processURL() {
		try {
			PDF2XHTMLConverter converter = new PDF2XHTMLConverter();
			HtmlElement htmlElement = converter.readAndConvertToXHTML(url);
			htmlContainer = new HtmlContainer(file, htmlElement);
			//Might also add SVG here
			//svgContainerList = new ArrayList<SVGContainer>();
			//pdfContainer.addSVGList(svgList);
		} catch (Exception e) {
			LOG.error("not a PDF file: "+file);
		}
	}

}
