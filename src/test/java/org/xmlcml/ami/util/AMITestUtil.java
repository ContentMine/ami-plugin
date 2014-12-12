package org.xmlcml.ami.util;

import java.io.File;

import nu.xom.Nodes;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.xmlcml.xml.XMLUtil;

/** test utilies only.
 * 
 * @author pm286
 *
 */
public class AMITestUtil {

	private final static Logger LOG = Logger.getLogger(AMITestUtil.class);
	
	/** checks file size in bytes .
	 * 
	 * fragile because of XML whitespace.
	 * 
	 * @param outputFile
	 * @param size0
	 */
	public static void assertFileDetails(File outputFile, int size) {
		LOG.debug("file: "+outputFile);
		Assert.assertTrue("file should exist", outputFile.exists());
		long fileSize = FileUtils.sizeOf(outputFile);
		Assert.assertEquals("size: "+size, size, fileSize);
	}

	/** checks nodecount for an XPath..
	 * 
	 * fragile because of XML whitespace.
	 * 
	 * @param outputFile to test
	 * @param count expected count
	 */
	public static void assertNodeCount(File outputFile, int count, String xpath) {
		Assert.assertTrue(outputFile+ " should exist", outputFile.exists());
		Nodes nodes = XMLUtil.parseQuietlyToDocument(outputFile).query(xpath);
		int nodeCount = nodes.size();
		if (count >=0 ){
			Assert.assertEquals("count: "+nodeCount, count, nodeCount);
		} else {
			LOG.debug(outputFile+"; "+nodeCount);
		}
	}

}
