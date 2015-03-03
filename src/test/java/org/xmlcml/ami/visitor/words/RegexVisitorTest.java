package org.xmlcml.ami.visitor.words;

import java.io.File;
import java.util.List;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.VisitorOutput;
import org.xmlcml.ami.visitor.regex.CompoundRegex;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.euclid.Util;
import org.xmlcml.files.EuclidSource;
import org.xmlcml.xml.XMLUtil;

public class RegexVisitorTest {
	
	private final Logger LOG = Logger.getLogger(RegexVisitorTest.class);


	@Test
	public void testSearchXMLDOI() throws Exception {
		String[] args = new String[] {
				"-i", Fixtures.MULTIPLE_SPECIES_312_XML.toString(),
				"-o", new File(Fixtures.AMI_OUT, "multiple1regexh.xml").toString(),
				"-r.r", "regex/metadata.xml",
		};
		new RegexVisitor().processArgs(args);
	}

	@Test
	public void testSearchHtmlFilesPhyloTreePDB() throws Exception {
//		regexVisitor.readRegexFiles();

		visitFile(Fixtures.BMC_SAMPLE);
	}

	@Test
	@Ignore // bad HTML
	/** null output as no hits?
	 * 
	 * @throws Exception
	 */
	public void testCommandRegexHTML() throws Exception {

		String[] args = new String[] {
				"-i", Fixtures.MULTIPLE_312_HTML.toString(),
				"-o", new File(Fixtures.AMI_OUT, "multiple1regexx.xml").toString(),
				"-r.r", "regex/pdb.xml", "regex/phylotree.xml", "regex/metadata.xml",
		};
		RegexVisitor.main(args);
	}

	@Test
	/** null output as no hits?
	 * 
	 * @throws Exception
	 */
	public void testCommandRegex() throws Exception {

		String[] args = new String[] {
				"-i", Fixtures.MULTIPLE_SPECIES_312_XML.toString(),
				"-o", new File(Fixtures.AMI_OUT, "multiple1regexh.xml").toString(),
				"-r.r", "regex/pdb.xml", /*"regex/phylotree.xml", "regex/metadata.xml",*/
		};
		RegexVisitor.main(args);
	}

	@Test
	/** runs DOI regex over single paper.
	 * 
	 * WORKS 2014-10-03
	 * 
	 * Gives:
	 * 490  [main] DEBUG org.xmlcml.ami.visitable.AbstractVisitable  - 
	 * creating output file target/journal.pone.0077058.xml/results.xml 
	 * <results xmlns="http://www.xml-cml.org/ami">
	 *   <result xmlns="" doi="0077058" count="11" />
	 * </results>
	 * @throws Exception
	 */
	public void testCommandRegexMetadataDOI() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/species/journal.pone.0077058.xml",
				"-o", new File("target/junk/").toString(),
				"-r.r", "regex/metadata.xml",
		};
		RegexVisitor.main(args);
	}
	
	@Test
	/** runs Genbank regex over single paper.
	 * 
	 * @throws Exception
	 */
	public void testCommandRegexGenbank0() throws Exception {
		File output = new File("target/regex/genbank0/results.xml");
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/regex/genbank0.xml",
				"-o", output.toString(),
				"-r.r", "regex/genbank.xml",
		};
		RegexVisitor.main(args);
//		Assert.assertTrue(output.exists());
	}
	
	@Test
	/** runs Genbank regex over single paper.
	 * 
	 * SHOWCASE example
	 * 
	 * // FIXME output filename is wrong
	 * @throws Exception
	 */
	public void testCommandRegexGeneral() throws Exception {
		File outputDir = new File("target/junk.xml");
		File outputFile = new File(new File(outputDir.getParentFile(), "1471-2148-14-70.xml"), AbstractVisitor.RESULTS_XML);
		FileUtils.deleteQuietly(outputFile);
		Assert.assertFalse("should have deleted "+outputFile, outputFile.exists());
		String[] args = new String[] {
				"-i", "./docs/regex/1471-2148-14-70.xml",
				"-o", outputDir.toString(),
				"-r.r", "regex/genbank.xml",
		};
		RegexVisitor.main(args);
		Assert.assertTrue("should have created: "+outputFile, outputFile.exists());
		Element element = XMLUtil.parseQuietlyToDocument(outputFile).getRootElement();
		LOG.debug(outputFile);
		List<Element> resultList = XMLUtil.getQueryElements(element, 
				"/*[local-name()='results']/*[local-name()='results']/*[local-name()='result']");
		Assert.assertEquals(0, resultList.size());
	}
	
	@Test
	/** runs Phylo regex over single paper.
	 * 
	 * @throws Exception
	 */
	@Ignore // output file name needs editing
	public void testCommandRegexPhylo() throws Exception {
		File outputDir = new File("target/");
		File outputFile = new File(new File(outputDir.getParentFile(), "1471-2148-14-70.xml"), AbstractVisitor.RESULTS_XML);
		FileUtils.deleteQuietly(outputFile);
		Assert.assertFalse("should have deleted "+outputFile, outputFile.exists());
		String[] args = new String[] {
				"-i", "./docs/regex/1471-2148-14-70.xml",
				"-o", outputDir.toString(),
				"-r.r", "regex/phylotree.xml",
		};
		RegexVisitor.main(args);
		Assert.assertTrue("should have created: "+outputFile, outputFile.exists());
		Element element = XMLUtil.parseQuietlyToDocument(outputFile).getRootElement();
		LOG.debug(outputFile);
	}
	
	@Test
	/** runs Ebola regex over single paper.
	 * 
	 * @throws Exception
	 */
	public void testCommandRegexEbola() throws Exception {
		File outputDir = new File("target/ebola");
		String[] args = new String[] {
				"-i", "./docs/ebola/xml/",
				"-o", outputDir.toString(),
				"-r.r", "regex/ebola.xml",
				"-e", "xml"
		};
		RegexVisitor.main(args);
	}
	
	@Test
	/** runs Ebola regex over single text document.
	 * 
	 * SHOWCASE example
	 * 
	 * @throws Exception
	 */
	public void testEbolaRegex() throws Exception {
		File outputDir = new File("target/junk.xml");
		File outputFile = new File(new File(outputDir.getParentFile(), "14Nov.xml"), AbstractVisitor.RESULTS_XML);
		FileUtils.deleteQuietly(outputFile);
		Assert.assertFalse("should have deleted "+outputFile, outputFile.exists());
		String[] args = new String[] {
				"-i", "./docs/ebola/text/14Nov.txt",
				"-o", outputDir.toString(),
				"-r.r", "regex/ebola.xml",
		};
		RegexVisitor.main(args);
		// asserts
//		Assert.assertTrue("should have created: "+outputFile, outputFile.exists());
//		Element element = XMLUtil.parseQuietlyToDocument(outputFile).getRootElement();
//		LOG.debug(outputFile);
//		List<Element> resultList = XMLUtil.getQueryElements(element, 
//				"/*[local-name()='results']/*[local-name()='results']/*[local-name()='result']");
//		Assert.assertEquals(0, resultList.size());
	}
	
	@Test
	/** runs Ebola regex over single HTML document.
	 *  
	 * @throws Exception
	 */
	public void testEbolaSpringerRegexXML() throws Exception {
		
		String p1678 = new File("./docs/ebola/xml/1678-9199-20-44.xml").toString();
		String[] args = new String[] {
				"-i", p1678,
				"-o", "target/1678.xml",
				"-r.r", "regex/ebola.xml",
		};
		RegexVisitor.main(args);
	}
	
	@Test
	/** runs Ebola regex over single HTML document.
	 *  
	 * @throws Exception
	 */
	public void testEbolaLiberiaRegexHTML() throws Exception {
		
		String nov14 = new File("./docs/ebola/html/14nov.html").toString();
		String[] args = new String[] {
				"-i", nov14,
				"-o", "target/liberiahtml.xml",
				"-r.r", "regex/ebola.xml",
		};
		RegexVisitor.main(args);
	}
	
	@Test
	/** runs Ebola regex over single text document.
	 * 
	 * 
	 * @throws Exception
	 */
	public void testWHOPDFRegex() throws Exception {
		
		// fails, no extractable text... don't know why
		/**
		String nov14 = new File("./docs/ebola/SITRep190Nov21th2014.pdf").toString();
		String[] args = new String[] {
				"-i", nov14,
				"-o", "target/liberia.xml",
				"-r.r", "regex/ebola.xml",
		};
		RegexVisitor.main(args);
		*/
		
		String nov14 = new File("./docs/ebola/pdf/roadmapsitrep_14Nov2014_eng.pdf").toString();
		String[] args = new String[] {
				"-i", nov14,
				"-o", "target/14nov.xml",
				"-r.r", "regex/ebola.xml",
		};
		RegexVisitor.main(args);
	}

	
	/** test phylo Regex on ca 10 PLOS papers locally.
	 * 
	 * @param file
	 */
	@Test
	@Ignore // too large
	public void testPhyloRegex() throws Exception {
		String plosMany = new File("./src/test/resources/org/xmlcml/ami/species/many/").toString();
		String[] args = new String[] {
				"-i", plosMany,
				"-o", "target/plosMany.xml",
				"-r.r", "regex/phylotree.xml",
				"-e", "xml",
		};
		RegexVisitor.main(args);
	}
	
	/** test agriculture Regex on ca 30 PLOS papers locally.
	 * 
	 * @param file
	 */
	@Test
	@Ignore // too many
	public void testAgriculture() throws Exception {
		String plosMany = new File("./src/test/resources/org/xmlcml/ami/species/many/").toString();
		String[] args = new String[] {
				"-i", plosMany,
				"-o", "target/agriculture.xml",
				"-r.r", "regex/agriculture.xml",
				"-e", "xml",
		};
		RegexVisitor.main(args);
	}
	
	/** test agriculture Regex on ca 30 PLOS papers locally.
	 * 
	 * @param file
	 */
	@Test
	// URL
	@Ignore // uses web
	public void testAgricultureURLs() throws Exception {
		if (!Util.checkPMR()) return;
		String[] args = new String[] {
				"-i", "http://www.biomedcentral.com/1471-2229/14/{1,3}",
				"-o", "target/agriculture.xml",
				"-r.r", "regex/agriculture.xml",
				"-e", "html",
		};
		RegexVisitor.main(args);
	}
	
	
	
	/** test agriculture Regex on ca 30 PLOS papers locally.
	 * 
	 * @param file
	 */
	@Test
	// URL
	@Ignore // too large
	public void testAgriculturePhylogeny() throws Exception {
		if (!Util.checkPMR()) return;
		String[] args = new String[] {
				"-i", "http://www.biomedcentral.com/1471-2229/14/{1,20}",
				"-o", "target/agriculture.xml",
				"-r.r", "regex/agriculture.xml", "regex/phylotree.xml",
				"-e", "html",
		};
		RegexVisitor.main(args);
	}
	
	/** test astrophysics Regex 
	 * 
	 * 3 sample astrophysics files suggested by Stray Toaster
	 * 
	 * this may take some time as the files download DTDs, etc. (wish they didn't)
	 * 
	 * the PDF takes ca 1 min to process
	 * 
	 * s2.0-S2212686414000272 fails due to awful HTML
	 * 
	 * @param file
	 */
	@Test
	// URL
	//@Ignore // unless you are an astrophysicist
	public void testAstrophysics() throws Exception {
		String astroDir = new File("./src/test/resources/org/xmlcml/ami/astrophys/").toString();
		String[] args = new String[] {
				"-i",
				"http://www.sciencedirect.com/science/article/pii/S2212686414000272",
				"-o", "target/astrophys.xml",
				"-r.r", "regex/astrophys.xml",
		};
		RegexVisitor.main(args);
	}
	
	@Test
	// fails as XML is not properly read
	public void testExtractedPLOSXML() throws Exception {
		String[] args = new String[] {
				"-i", new File("src/test/resources/org/xmlcml/ami/plosone/journal.pone.0113556.xml").toString(),
				"-t", "plosone",
				"-o", "target/plosone/species/",
				"-x", "//*[@tag='figure']",
				"-r.r", "regex/figure.xml",
				"-e", EuclidSource.XML
		};
		RegexVisitor.main(args);
	}
	
	@Test
	public void testExtractedPLOSHTML() throws Exception {
		String[] args = new String[] {
				"-i", new File("src/test/resources/org/xmlcml/ami/plosone/journal.pone.0113556.html").toString(),
				"-t", "plosone",
				"-o", "target/plosone/species/",
				"-x", "//*[@tag='abstract']",
				"-r.r", "regex/common.xml",
				"-e", EuclidSource.XML
		};
		RegexVisitor.main(args);
	}
	
	@Test
	// URL
	@Ignore
	public void testBMCPhyloURLs() throws Exception {
		String[] args = new String[] {
				"-i", "http://www.biomedcentral.com/content/download/xml/1471-2148-13-191.xml",
				"-t", "bmc",
				"-x", "//*[@tag='abstract']",
				"-r.r", "regex/phylotree.xml",
				"-e", EuclidSource.XML
		};
		RegexVisitor.main(args);
	}
	
	@Test
	// URL
	@Ignore
	public void testBMCCommonURLHTMLs() throws Exception {
		String[] args = new String[] {
//				"-i", "http://www.biomedcentral.com/content/download/xml/1471-2148-13-191.xml",
				"-i", "http://www.biomedcentral.com/1471-2148/13/191",
				"--regex", "https://raw.githubusercontent.com/ContentMine/ami/master/regex/phylotree.xml",
				"-e", EuclidSource.HTML
		};

		RegexVisitor.main(args);
	}
	
	@Test
	public void testRegex() throws Exception {
		String[] args = new String[] {
				"--r.regex", "regex/phylotree.xml",
				"--context", "100"
		};
		RegexVisitor regexVisitor = new RegexVisitor();
		regexVisitor.processArgs(args);
		WordArgProcessor argProcessor = (WordArgProcessor) regexVisitor.getArgProcessor();
		List<ArgumentOption> chosenOptions = argProcessor.getChosenArgumentList();
		for (ArgumentOption option : chosenOptions) {
			LOG.debug("OPT> "+option);
		}
		
	}
	
	
	

	// ============================
	
	private static void visitFile(File file) {
		visitFile(new RegexVisitor(), file);
	}
		
	private static void visitFile(RegexVisitor regexVisitor, File file) {
		if (file.exists()) {
			HtmlVisitable htmlVisitable = new HtmlVisitable();
			htmlVisitable.setTopDirectory(file);
			regexVisitor.visit(htmlVisitable);
//			regexVisitor.debug();
		}
	}


	
}
