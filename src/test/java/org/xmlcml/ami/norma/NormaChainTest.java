package org.xmlcml.ami.norma;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.regex.RegexVisitor;
import org.xmlcml.ami.visitor.species.SpeciesVisitor;
import org.xmlcml.norma.Norma;

public class NormaChainTest {

	
	private static final Logger LOG = Logger.getLogger(NormaChainTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/** tests chaining output of Norma to AMI.
	 * 
	 */
	@Test
	public void testChainNorma0() {
		File outputFile = new File("target/plosone/0115884.pubstyle.html");
		String[] args = {
				"-i", new File(Fixtures.PLOSONE_DIR, "journal.pone.0115884/fulltext.html").toString(),
				"-p", "plosone",
				"-o", outputFile.toString(),
		};
		org.xmlcml.norma.Norma norma = new Norma();
		norma.run(args);
	}
	
	/** tests chaining output of Norma to AMI.
	 * 
	 */
	@Test
	public void NormaXMLPart1() {
		File outputFile = new File("target/plosone/0115884.fromnorma1.html");
		String[] args = {
				"-i", new File(Fixtures.PLOSONE_DIR, "journal.pone.0115884/fulltext.html").toString(),
				"-p", "plosone",
				"-o", outputFile.toString(),
		};
		org.xmlcml.norma.Norma norma = new Norma();
		norma.run(args);
	}
	
	/** tests chaining output of Norma to AMI.
	 * 
	 */
	@Test
	@Ignore // FIXME
	public void NormaXMLPart2() throws Exception {
		File normaOutputFile = new File("target/plosone/0115884.fromnorma1.html");
		String outputDirName = "target/plosone/0115884.fromnorma1.xml/";
		String[] amiArgs = {
				"-i", normaOutputFile.toString(),
//				"-r.r", "regex/common.xml",
				"-o", outputDirName,
		};
//		RegexVisitor visitor = new RegexVisitor();
		SpeciesVisitor visitor = new SpeciesVisitor();
		visitor.processArgs(amiArgs);
		File outputFile1 = new File(new File(outputDirName), "results.xml");
		Assert.assertTrue(outputFile1.exists());
	}
	
	/** tests chaining output of Norma to AMI.
	 * 
	 */
	@Test
	@Ignore // not a well structured example // FIXME
	public void NormaXML2AMI() throws Exception {
		File normaOutputFile = new File("target/plosone/0115884.fromnorma.html");
		LOG.error("Use getResource() for stylesheet");
		String[] args = {
				"-q", new File(Fixtures.PLOSONE_DIR, "journal.pone.0115884").toString(),
				"--input", "fulltext.xml",
				"-p", "plosone",
// works but ucky, need to get it as a resource
				"--xsl", "src/main/resources/org/xmlcml/norma/pubstyle/nlm/toHtml.xsl",
				"--output", "scholarly.html",
		};
		org.xmlcml.norma.Norma norma = new Norma();
		norma.run(args);
		
		String outputDirName = "target/plosone/0115884.fromnorma.xml/";
		String[] amiArgs = {
				"-i", normaOutputFile.toString(),
				"-o", outputDirName,
		};
		SpeciesVisitor visitor = new SpeciesVisitor();
		visitor.processArgs(amiArgs);
		File outputFile1 = new File(new File(outputDirName), "results.xml");
		LOG.error("FILE OUTPUT FILE");
//		Assert.assertTrue(outputFile1.exists());
		
	}
	
	
	/** tests chaining output of Norma to AMI.
	 * 
	 */
	@Test
	public void testChainNormaRegexHTML() throws Exception {
		File outputFile = new File("target/plosone/0115884.pubstyle.html");
		String[] normaArgs = {
				"-i", new File(Fixtures.PLOSONE_DIR, "journal.pone.0115884/fulltext.html").toString(),
				"-p", "plosone",
				"-o", outputFile.toString(),
		};
		Norma norma = new Norma();
		norma.run(normaArgs);
		
		String outputDirName = "target/plosone/0115884.pubstyle.xml/";
		String[] amiArgs = {
				"-i", outputFile.toString(),
				"-r.r", "regex/common.xml",
				"-o", outputDirName,
		};
		RegexVisitor visitor = new RegexVisitor();
//		SpeciesVisitor visitor = new SpeciesVisitor();
		visitor.processArgs(amiArgs);
		File outputFile1 = new File(new File(outputDirName), "results.xml");
		LOG.error("FIX OUTPUT FILE");
//		Assert.assertTrue(outputFile1.exists());
		
	}
	
	/** tests chaining output of Norma to AMI.
	 * 
	 */
	@Ignore // FIXME
	@Test
	public void testChainNormaSpecies() throws Exception {
		// the input file had to be manually edited because Tidy didn't manage to make good HTML
		File outputFile = new File("target/plosone/0115884.pubstyle.html");
		String[] normaArgs = {
				"-i", new File(Fixtures.PLOSONE_DIR, "journal.pone.0115884/fulltext.manually.html").toString(),
				"-p", "plosone",
				"-o", outputFile.toString(),
		};
		Norma norma = new Norma();
		norma.run(normaArgs);
		Assert.assertTrue(outputFile.exists());
		
		String outputDirName = "target/plosone/0115884.pubstyle.xml/";
		String[] amiArgs = {
				"-i", outputFile.toString(),
				"-r.r", "regex/common.xml",
				"-o", outputDirName,
		};
//		RegexVisitor visitor = new RegexVisitor();
		SpeciesVisitor visitor = new SpeciesVisitor();
		visitor.processArgs(amiArgs);
		File outputFile1 = new File(new File(outputDirName), "results.xml");
		Assert.assertTrue(outputFile1.exists());
		
	}
	
	@Test 
	public void testReadSHTML() throws Exception {
		File outputFile = new File("target/bmc/15_1_511.common.html");
		String[] args = {
				"-i", "src/test/resources/org/xmlcml/ami/bmc/15_1_511.html",
				"-r.r", "regex/common.xml",
				"-o", outputFile.toString(),
		};
		
		RegexVisitor visitor = new RegexVisitor();
		visitor.processArgs(args);
//		Assert.assertTrue(outputFile.exists());
//		HtmlElement htmlElement = new HtmlFactory().parse(outputFile);
//		List<HtmlElement> divElements = HtmlUtil.getQueryHtmlElements(htmlElement, "//*[local-name()='div']");
//		Assert.assertEquals("div elements "+divElements.size(), 219, divElements.size()); 
//		List<HtmlElement> spanElements = HtmlUtil.getQueryHtmlElements(htmlElement, "//*[local-name()='span']");
//		Assert.assertEquals("span elements "+spanElements.size(), 1054, spanElements.size()); 
//		List<HtmlElement> pElements = HtmlUtil.getQueryHtmlElements(htmlElement, "//*[local-name()='p']");
//		Assert.assertEquals("p elements "+pElements.size(), 147, pElements.size()); 
	}
	
}
