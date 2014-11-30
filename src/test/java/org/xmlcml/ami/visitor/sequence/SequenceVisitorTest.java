package org.xmlcml.ami.visitor.sequence;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitable.xml.XMLVisitable;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.sequence.SequenceVisitor;
import org.xmlcml.ami.result.ResultsListElement;
import org.xmlcml.xml.XMLUtil;

public class SequenceVisitorTest {


	private static final Logger LOG = Logger.getLogger(SequenceVisitorTest.class);

	@Test
	@Ignore // FIXME QUICK
	public void testHtmlVisitable() throws Exception {
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.addFile(Fixtures.METABOLITE_00039_HTML);
		htmlVisitable.addFile(Fixtures.MULTIPLE_SPECIES_312_HTML);
		htmlVisitable.addFile(Fixtures.PASSERINES_313_HTML);
		
		AbstractVisitor sequenceVisitor = new SequenceVisitor();
		sequenceVisitor.visit(htmlVisitable);
	}

	@Test
	public void testSVGVisitable() throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		AbstractVisitor sequenceVisitor = new SequenceVisitor();
		try {
			sequenceVisitor.visit(svgVisitable);
			Assert.fail("should fail)");
		} catch (Exception e) {
			LOG.error("failed as expected "+e);
		}
		
		
	}

	@Test
	public void testSVGVisitableDir() throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		File[] files = Fixtures.MOLECULES_DIR.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.toString().endsWith(".svg")) {
					svgVisitable.addSVGFile(file);
				}
			}
		}
		AbstractVisitor sequenceVisitor = new SequenceVisitor();
		try {
			sequenceVisitor.visit(svgVisitable);
			Assert.fail("should fail)");
		} catch (Exception e) {
			LOG.error("failed as expected "+e);
		}

	}

	@Test
	@Ignore // too many files , needs mending
	public void testSearchHtmlFiles() throws Exception {
		AbstractVisitor sequenceVisitor = new SequenceVisitor();
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.setTopDirectory(Fixtures.TEST_DIRECTORIES_DIR);
		sequenceVisitor.visit(htmlVisitable);
	}

	@Test
	public void testSearchHtmlFilesSample() throws Exception {
		AbstractVisitor sequenceVisitor = new SequenceVisitor();
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		File file = Fixtures.BMC_SAMPLE;
		if (file.exists()) {
			htmlVisitable.setTopDirectory(file);
			sequenceVisitor.visit(htmlVisitable);
			ResultsListElement results = sequenceVisitor.getResultsElement();
			XMLUtil.debug(results, new FileOutputStream("target/htmlSequenceSample.xml"), 1);
		}
	}

	@Test
	@Ignore
	public void testSearchHtmlFilesAll() throws Exception {
		AbstractVisitor sequenceVisitor = new SequenceVisitor();
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		File file = Fixtures.BMC_ALL;
		if (file.exists()) {
			htmlVisitable.setTopDirectory(file);
			sequenceVisitor.visit(htmlVisitable);
			ResultsListElement results = sequenceVisitor.getResultsElement();
			XMLUtil.debug(results, new FileOutputStream("target/htmlSequenceAll.xml"), 1);
		}
	}

	@Test
	@Ignore
	public void testSearchXmlFiles() throws Exception {
		AbstractVisitor sequenceVisitor = new SequenceVisitor();
		XMLVisitable xmlVisitable = new XMLVisitable();
		xmlVisitable.addFile(Fixtures.HADROSAUR_XML);
		sequenceVisitor.visit(xmlVisitable);
		ResultsListElement results = sequenceVisitor.getResultsElement();
		XMLUtil.debug(results, new FileOutputStream("target/hadrosaurResults.xml"), 1);
	}

	// fails because of HTML file
	@Test
	@Ignore // FIXME QUICK
	public void testCommandlineHtmlFile() throws Exception {
		String userHome = System.getProperties().get("user.home").toString();
		LOG.trace("user.home: "+userHome);
		File outputFile = new File(Fixtures.AMI_OUT,"/sequences.xml");
		String[] args = new String[] {
				"-i", Fixtures.MULTIPLE_SPECIES_312_HTML.toString(),
				"-o", outputFile.toString(),
		};
		SequenceVisitor.main(args);
	}

	@Test
	public void testCommandlineXMLFile() throws Exception {
		String userHome = System.getProperties().get("user.home").toString();
		LOG.trace("user.home: "+userHome);
		File outputFile = new File(Fixtures.AMI_OUT,"/sequences.xml");
		String[] args = new String[] {
				"-i", Fixtures.MULTIPLE_SPECIES_312_XML.toString(),
				"-o", outputFile.toString(),
		};
		SequenceVisitor.main(args);
	}

}
