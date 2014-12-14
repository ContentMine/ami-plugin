package org.xmlcml.ami.visitable.pdf;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.AbstractVisitable;
import org.xmlcml.ami.visitable.VisitableContainer;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.svg2xml.pdf.PDFAnalyzer;

public class PDFVisitable extends AbstractVisitable  {

	private final static Logger LOG = Logger.getLogger(PDFVisitable.class);

	private static final String PDF = "pdf";

	private List<PDFContainer> pdfContainerList;

	public PDFVisitable() {
	}

	@Override
	public void addFile(File file) throws Exception {
		checkFile(file);
		ensurePDFContainerList();
		PDFContainer pdfContainer = new PDFContainer(file); 
		pdfContainer.processFile();
		pdfContainerList.add(pdfContainer);
	}

	@Override
	public void downloadParseAndAddURL(URL url) throws Exception {
		super.addURL(url);
		ensurePDFContainerList();
		PDFContainer pdfContainer = new PDFContainer(url); 
		pdfContainer.processURL();
		pdfContainerList.add(pdfContainer);
	}

	private void ensurePDFContainerList() {
		if (pdfContainerList == null) {
			pdfContainerList = new ArrayList<PDFContainer>();
		}
	}

	/*public List<PDFContainer> getPDFContainerList() {
		ensurePDFContainerList();
		if (super.findFilesInDirectories() != null) {
			pdfContainerList = createContainersFromFiles();
		} 
		return pdfContainerList;
	}*/


	@Override
	public String[] getExtensions() {
		return new String[] {PDF};
	}

	@Override
	public void getMetadata() {
		/*for (PDFContainer pdfContainer : pdfContainerList) {
			getMetadata(pdfContainer.getElement());
		}*/
	}

	private void getMetadata(Element pdf) {
		//FIXME
	}


	/*public List<HtmlContainer> createHtmlElementList() {
		List<List<SVGSVG>> svgListList = new ArrayList<List<SVGSVG>>();
		for (PDFContainer pdfContainer : pdfContainerList) {
			File file = pdfContainer.getFile();
			PDF2SVGConverter converter = new PDF2SVGConverter();
			try {
				converter.openPDFFile(file);
				List<SVGSVG> svgList = converter.getPageList();
				svgListList.add(svgList);
			} catch (Exception e) {
				LOG.error("Cannot read/parse: "+file);
			}
		}
		return svgListList;
	}*/

	public List<List<SVGSVG>> createSVGListList() {
		List<List<SVGSVG>> svgListList = new ArrayList<List<SVGSVG>>();
		for (PDFContainer pdfContainer : pdfContainerList) {
			File file = pdfContainer.getFile();
			PDF2SVGConverter converter = new PDF2SVGConverter();
			try {
				converter.openPDFFile(file);
				List<SVGSVG> svgList = converter.getPageList();
				svgListList.add(svgList);
			} catch (Exception e) {
				LOG.error("Cannot read/parse: "+file);
			}
		}
		return svgListList;
	}

	public void convertSVG(List<SVGSVG> svgList) {
		PDFAnalyzer analyzer = new PDFAnalyzer();
		analyzer.createAndFillPageAnalyzers(svgList);
	}

	public VisitableContainer getHtmlContainer() {
		//TODO Auto-generated method stub
		return null;
	}

	public List<PDFContainer> getPDFContainerList() {
		return pdfContainerList;
	}
	
}
