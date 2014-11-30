package org.xmlcml.ami.visitor.regex;

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
import org.xmlcml.ami.visitable.pdf.PDF2XHTMLConverter;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.VisitorOutput;
import org.xmlcml.svg2xml.pdf.PDFAnalyzer;
import org.xmlcml.xml.XMLUtil;

public class RegexVisitorTest {
	
	private final Logger LOG = Logger.getLogger(RegexVisitorTest.class);

	@Test
	@Ignore // FIXME fails
	public void testConstructor() {
		RegexVisitor regexVisitor = new RegexVisitor();
		List<CompoundRegex> compoundRegexList = regexVisitor.getCompoundRegexList();
		Assert.assertNotNull("size may vary", compoundRegexList);
		Assert.assertTrue("should be larger than 0", compoundRegexList.size() > 0);
		CompoundRegex phyloRegex = regexVisitor.getCompoundRegex("phylotree");
		Assert.assertNotNull(phyloRegex);
		Assert.assertTrue("phylo", 30 < phyloRegex.getRegexComponentList()
				.size());
	}

	@Test
	@Ignore // FIXME fails commandline
	public void testSearch() throws Exception {
		AbstractVisitor regexVisitor = new RegexVisitor();
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.addFile(Fixtures.ARMBRUSTER_HTML);
		regexVisitor.visit(htmlVisitable);
	}

	@Test
	@Ignore
	public void testSearchFiles() throws Exception {
		RegexVisitor regexVisitor = new RegexVisitor();
		regexVisitor.addRegexFile("phylotree");
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.setTopDirectory(Fixtures.TEST_DIRECTORIES_DIR);
		regexVisitor.visit(htmlVisitable);
//		regexVisitor.debug();
	}

	@Test
	@Ignore
	//FIXME directory not in repository
	public void testSearchHtmlSample() throws Exception {
		RegexVisitor regexVisitor = new RegexVisitor();
		regexVisitor.addRegexFile("phylotree");
		visitFile(regexVisitor, Fixtures.BMC_SAMPLE);
	}

	@Test
	@Ignore
	public void testSearchHtmlFilesPDB() throws Exception {
		RegexVisitor regexVisitor = new RegexVisitor();
		VisitorOutput visitorOutput = new VisitorOutput("target/regex/pdb.xml");
		regexVisitor.setVisitorOutput(visitorOutput);
		regexVisitor.addRegexFile("regex/pdb.xml");
		visitFile(regexVisitor, Fixtures.BMC_SAMPLE);
	}

	@Test
	public void testSearchXMLDOI() throws Exception {
		String[] args = new String[] {
				"-i", Fixtures.MULTIPLE_SPECIES_312_XML.toString(),
				"-o", new File(Fixtures.AMI_OUT, "multiple1regexh.xml").toString(),
				"-g", "regex/metadata.xml",
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
				"-g", "regex/pdb.xml", "regex/phylotree.xml", "regex/metadata.xml",
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
				"-g", "regex/pdb.xml", /*"regex/phylotree.xml", "regex/metadata.xml",*/
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
				"-g", "regex/metadata.xml",
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
				"-g", "regex/genbank.xml",
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
				"-g", "regex/genbank.xml",
		};
		RegexVisitor.main(args);
		Assert.assertTrue("should have created: "+outputFile, outputFile.exists());
		Element element = XMLUtil.parseQuietlyToDocument(outputFile).getRootElement();
		XMLUtil.debug(element);
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
	public void testCommandRegexPhylo() throws Exception {
		File outputDir = new File("target/");
		File outputFile = new File(new File(outputDir.getParentFile(), "1471-2148-14-70.xml"), AbstractVisitor.RESULTS_XML);
		FileUtils.deleteQuietly(outputFile);
		Assert.assertFalse("should have deleted "+outputFile, outputFile.exists());
		String[] args = new String[] {
				"-i", "./docs/regex/1471-2148-14-70.xml",
				"-o", outputDir.toString(),
				"-g", "regex/phylotree.xml",
		};
		RegexVisitor.main(args);
		Assert.assertTrue("should have created: "+outputFile, outputFile.exists());
		Element element = XMLUtil.parseQuietlyToDocument(outputFile).getRootElement();
		XMLUtil.debug(element);
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
				"-g", "regex/ebola.xml",
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
				"-g", "regex/ebola.xml",
		};
		RegexVisitor.main(args);
		// asserts
		Assert.assertTrue("should have created: "+outputFile, outputFile.exists());
		Element element = XMLUtil.parseQuietlyToDocument(outputFile).getRootElement();
		XMLUtil.debug(element);
		LOG.debug(outputFile);
		List<Element> resultList = XMLUtil.getQueryElements(element, 
				"/*[local-name()='results']/*[local-name()='results']/*[local-name()='result']");
		Assert.assertEquals(0, resultList.size());
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
				"-g", "regex/ebola.xml",
		};
		RegexVisitor.main(args);
	}
	
	@Test
	@Ignore // fails on commandline (with reading files) 
	/** runs Ebola regex over single HTML document.
	 *  
	 * @throws Exception
	 */
	public void testEbolaLiberiaRegexHTML() throws Exception {
		
		String nov14 = new File("./docs/ebola/html/14nov.html").toString();
		String[] args = new String[] {
				"-i", nov14,
				"-o", "target/liberiahtml.xml",
				"-g", "regex/ebola.xml",
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
				"-g", "regex/ebola.xml",
		};
		RegexVisitor.main(args);
		*/
		
		String nov14 = new File("./docs/ebola/pdf/roadmapsitrep_14Nov2014_eng.pdf").toString();
		String[] args = new String[] {
				"-i", nov14,
				"-o", "target/14nov.xml",
				"-g", "regex/ebola.xml",
		};
		RegexVisitor.main(args);
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
