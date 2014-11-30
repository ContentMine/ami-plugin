package org.xmlcml.ami.visitable.pdf;

import java.io.FileOutputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitable.pdf.PDF2XHTMLConverter;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.pdf2svg.PDF2SVGConverter;
import org.xmlcml.xml.XMLUtil;

public class PDFVisitableTest {

	@Test
	public void testPDFConverter2SVGList() throws Exception {
		PDF2SVGConverter converter = new PDF2SVGConverter();
		converter.setWriteFile(false);
		converter.setStoreSVG(true);
		converter.openPDFFile(Fixtures.MULTIPLE_312_PDF);
		List<SVGSVG> svgList = converter.getPageList();
		Assert.assertEquals("svg list", 14, svgList.size());
	}
	
	@Test
	@Ignore("Gives 101, but has not been counted so would only act as a regression test; waiting for decent chunking algorithm")
	public void testPDFConverter2XHTML() throws Exception {
		PDF2XHTMLConverter converter = new PDF2XHTMLConverter();
		HtmlElement htmlElement = converter.readAndConvertToXHTML(Fixtures._329_PDF);
		XMLUtil.debug(htmlElement, new FileOutputStream("target/pdf2xhtml.html"), 1);
		Assert.assertEquals("italic", 113, XMLUtil.getQueryElements(htmlElement, ".//*[local-name()='i']").size());
	}
}
