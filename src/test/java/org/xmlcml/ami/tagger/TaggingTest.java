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
//				"-x", "//*[*[local-name()='h3' and (.='Abstract' or .='Discussion')]]",  // 1 hit
//				"-x", "//*[*[local-name()='h3' and (.='Discussion')]]",  // 1 hit
//				"-x", "//*[*[local-name()='h3' and (.='Abstract')]]",    // 0 hits
				"-e", "html",
		};
		RegexVisitor.main(args);
	}
	

}
