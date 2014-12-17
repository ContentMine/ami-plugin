package org.xmlcml.ami.tagger;

import org.junit.Test;
import org.xmlcml.ami.visitor.regex.RegexVisitor;
import org.xmlcml.euclid.Util;

public class TaggingTest {

	/** test agriculture Regex on ca 30 PLOS papers locally.
	 * 
	 * @param file
	 */
	@Test
	public void testTagging() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/sections/bmc/1471-2229.14.188.htm",
				"-o", "target/section.xml",
				"-g", "regex/agriculture.xml",
				"-t", "bmc",
				"-x", "//*[@tag='abstract' or @tag='discussion']",
				"-e", "html",
		};
		RegexVisitor.main(args);
	}
	
	/** test agriculture Regex on ca 30 PLOS papers locally.
	 * 
	 * @param file
	 */
	@Test
	public void testFigures() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/sections/bmc/1471-2229.14.188.htm",
				"-o", "target/section.xml",
				"-g", "regex/figure.xml",
				"-t", "bmc",
				"-x", "//*[local-name()='div' and @class='fig']",
				"-e", "html",
		};
		RegexVisitor.main(args);
	}
	

}
