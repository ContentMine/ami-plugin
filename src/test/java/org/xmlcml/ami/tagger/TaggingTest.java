package org.xmlcml.ami.tagger;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.regex.RegexVisitor;
import org.xmlcml.xml.XMLUtil;

public class TaggingTest {

	
	private static final Logger LOG = Logger.getLogger(TaggingTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
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
				"-r.r", "regex/agriculture.xml",
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
				"--context", "89", "93",
				"-r.r", "regex/agriculture.xml",
				"-x", "//*[@tag='abstract']",
		};
		RegexVisitor.main(args);
		Assert.assertTrue(Fixtures.RAW_0113556.exists());
		String in = FileUtils.readFileToString(Fixtures.RAW_0113556);
//		LOG.debug(Fixtures.RAW_0113556+"; "+in);
		String out = FileUtils.readFileToString(Fixtures.RESULTS_ABSTRACT_0113556);
//		LOG.debug(""+Fixtures.RESULTS_ABSTRACT_0113556 + (int)FileUtils.sizeOf(Fixtures.RESULTS_ABSTRACT_0113556) +"\n "+
//				Fixtures.TEST_RESULTS0113556_XML+"; "+(int)FileUtils.sizeOf(Fixtures.TEST_RESULTS0113556_XML));
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
				"-r.r", "regex/figure.xml",
				"-x", "//*[@tag='figure']",
		};
		RegexVisitor.main(args);
	}
	

}
