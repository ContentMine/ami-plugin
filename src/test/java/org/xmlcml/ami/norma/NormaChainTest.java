package org.xmlcml.ami.norma;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.regex.RegexVisitor;
import org.xmlcml.ami.visitor.species.SpeciesVisitor;
import org.xmlcml.norma.Norma;

public class NormaChainTest {

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
	public void NormaXMLPart2() throws Exception {
		File normaOutputFile = new File("target/plosone/0115884.fromnorma1.html");
		String outputDirName = "target/plosone/0115884.fromnorma1.xml/";
		String[] amiArgs = {
				"-i", normaOutputFile.toString(),
//				"-g", "regex/common.xml",
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
	public void NormaXML2AMI() throws Exception {
		File normaOutputFile = new File("target/plosone/0115884.fromnorma.html");
		String[] args = {
				"-i", new File(Fixtures.PLOSONE_DIR, "journal.pone.0115884/fulltext.xml").toString(),
				"-p", "plosone",
				"-o", normaOutputFile.toString(),
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
		Assert.assertTrue(outputFile1.exists());
		
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
				"-g", "regex/common.xml",
				"-o", outputDirName,
		};
		RegexVisitor visitor = new RegexVisitor();
//		SpeciesVisitor visitor = new SpeciesVisitor();
		visitor.processArgs(amiArgs);
		File outputFile1 = new File(new File(outputDirName), "results.xml");
		Assert.assertTrue(outputFile1.exists());
		
	}
	
	/** tests chaining output of Norma to AMI.
	 * 
	 */
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
		
		String outputDirName = "target/plosone/0115884.pubstyle.species.xml/";
		String[] amiArgs = {
				"-i", outputFile.toString(),
				"-g", "regex/common.xml",
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
				"-g", "regex/common.xml",
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
