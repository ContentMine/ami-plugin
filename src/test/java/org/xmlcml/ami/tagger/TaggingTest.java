package org.xmlcml.ami.tagger;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.regex.RegexVisitor;
import org.xmlcml.xml.XMLUtil;

public class TaggingTest {

	/** test agriculture Regex on PLOS paper.
	 * 
	 * SHOWCASE 
	 * 
	 * no tagging used
	 * 
	 * @param file
	 */
	@Test
	public void testPLOSONE_0113556() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/plosone/journal.pone.0113556.tagged.xml",
				"-g", "regex/agriculture.xml",
		};
		RegexVisitor.main(args);
		Assert.assertTrue(Fixtures.RAW_0113556.exists());
		String message = XMLUtil.equalsCanonically(Fixtures.RESULTS_0113556, Fixtures.TEST_RESULTS0113556_XML, true);
		Assert.assertNull(message, message);
		
	}

	/** test agriculture Regex on PLOS paper.
	 * 
	 * SHOWCASE 
	 * 
	 * sections already used, chooses abstract
	 * 
	 * @param file
	 */
	@Test
	public void testPLOSONE_0113556Abstract() throws Exception {
		String[] args = new String[] {
				"-i", Fixtures.TAGGED_0113556.toString(),
				"-o", "target/section.xml",
				"-g", "regex/agriculture.xml",
				"-x", "//*[@tag='abstract']",
		};
		RegexVisitor.main(args);
		Assert.assertTrue(Fixtures.RAW_0113556.exists());
		String message = XMLUtil.equalsCanonically(Fixtures.RESULTS_ABSTRACT_0113556, Fixtures.TEST_RESULTS0113556_XML, true);
		Assert.assertNull(message, message);
		
	}
	

	/** test agriculture Regex on ca 30 PLOS papers locally.
	 * 
	 * @param file
	 */
	@Test
	public void testFigures() throws Exception {
		String[] args = new String[] {
				"-i", Fixtures.TAGGED_0113556.toString(),
				"-o", "target/section.xml",
				"-g", "regex/figure.xml",
				"-x", "//*[@tag='figure']",
		};
		RegexVisitor.main(args);
	}
	

}
