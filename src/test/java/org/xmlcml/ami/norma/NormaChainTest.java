package org.xmlcml.ami.norma;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.regex.RegexVisitor;
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
		Norma norma = new Norma();
		norma.run(args);
	}
	
	/** tests chaining output of Norma to AMI.
	 * 
	 */
	@Test
	public void testChainNorma() throws Exception {
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
}
