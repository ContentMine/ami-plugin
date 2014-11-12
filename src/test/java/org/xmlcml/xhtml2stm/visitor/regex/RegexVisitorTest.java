package org.xmlcml.xhtml2stm.visitor.regex;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.xhtml2stm.Fixtures;
import org.xmlcml.xhtml2stm.visitable.html.HtmlVisitable;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;
import org.xmlcml.xhtml2stm.visitor.VisitorOutput;

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
	 * 490  [main] DEBUG org.xmlcml.xhtml2stm.visitable.AbstractVisitable  - 
	 * creating output file target/journal.pone.0077058.xml/results.xml 
	 * <results xmlns="http://www.xml-cml.org/xhtml2stm">
	 *   <result xmlns="" doi="0077058" count="11" />
	 * </results>
	 * @throws Exception
	 */
	public void testCommandRegexMetadataDOI() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/xhtml2stm/species/journal.pone.0077058.xml",
				"-o", new File("target/junk/").toString(),
				"-g", "regex/metadata.xml",
		};
		RegexVisitor.main(args);
	}
	
	@Test
	@Ignore // FIXME QUICK
	/** runs Genbank regex over single paper.
	 * 
	 * @throws Exception
	 */
	public void testCommandRegexGenbank0() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/xhtml2stm/regex/genbank0.xml",
				"-o", new File("target/junk/").toString(),
				"-g", "regex/genbank.xml",
		};
		RegexVisitor.main(args);
	}
	
	@Test
	@Ignore // FIXME QUICK
	/** runs Genbank regex over single paper.
	 * 
	 * @throws Exception
	 */
	public void testCommandRegexGenbank() throws Exception {
		String[] args = new String[] {
				"-i", "./docs/regex/1471-2148-14-70.xml",
				"-o", new File("target/junk/").toString(),
				"-g", "regex/genbank.xml",
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
