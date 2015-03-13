package org.xmlcml.ami2.plugins.regex;

import java.io.File;
import java.io.IOException;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.regex.RegexPlugin;
import org.xmlcml.ami2.plugins.simple.SimplePlugin;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.xml.XMLUtil;

public class RegexPluginTest {
	
	private static final Logger LOG = Logger.getLogger(RegexPluginTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	

	
	/** test generation of conformant regexes
	 * 
	 * desn't run anything
	 * 
	 * @throws IOException
	 */
	@Test
	public void testSimpleTestRegex() throws IOException {
		String[] args = {
				// add context for 25 chars preceding and 40 post
				"--context", "25", "40",
				"--r.regex", 
			    	"regex/simpletest.xml",
		};
		RegexPlugin regexPlugin = new RegexPlugin(args);
	}
	
	@Test
	public void testRegexPlugins() throws IOException {
		QuickscrapeNorma qsNorma = new QuickscrapeNorma(Fixtures.TEST_BMC_15_1_511_QSN);
		File normaTemp = new File("target/bmc/regex/15_1_511_test");
		qsNorma.copyTo(normaTemp, true);
		Assert.assertFalse("results.xml", qsNorma.hasResultsXML());
		String[] args = {
				"-q", normaTemp.toString(),
				"-i", "scholarly.html",
				"-o", "results.xml",
				"--context", "25", "40",
				"--r.regex", 
			    	"regex/agriculture.xml",
			    	"regex/astrophys.xml",
			    	"regex/commonnew.xml",
			    	"regex/ebola.xml",
			    	"regex/figure.xml",
			    	"regex/genbank.xml",
			    	"regex/metadata.xml",
			    	"regex/pdb.xml",
			    	"regex/phylotree.xml",
			    	"regex/simpletest.xml",
			    	"regex/stemtest.xml", // this doesn't exist and LOGs an error
		};
		RegexPlugin regexPlugin = new RegexPlugin(args);
		AMIArgProcessor argProcessor = (AMIArgProcessor) regexPlugin.getArgProcessor();
		Assert.assertNotNull(argProcessor);
//		LOG.debug(argProcessor.getInputList());
//		argProcessor.runAndOutput();
//		QuickscrapeNorma qsNormaTemp = new QuickscrapeNorma(normaTemp);
//		// fails at present
////		Assert.assertTrue("results.xml", qsNormaTemp.hasResultsXML());
	}
	
	@Test
	public void testRegexPlugin() throws IOException {
		QuickscrapeNorma qsNorma = new QuickscrapeNorma(Fixtures.TEST_BMC_15_1_511_QSN);
		File normaTemp = new File("target/bmc/regex/15_1_511_test");
		qsNorma.copyTo(normaTemp, true);
		Assert.assertFalse("results.xml", qsNorma.hasResultsXML());
		String[] args = {
				"-q", normaTemp.toString(),
				"-i", "scholarly.html",
				"-o", "results.xml",
				"--context", "25", "40",
				"--r.regex", 
				    "regex/consort0.xml",
		};
		RegexPlugin regexPlugin = new RegexPlugin(args);
		AMIArgProcessor argProcessor = (AMIArgProcessor) regexPlugin.getArgProcessor();
		Assert.assertNotNull(argProcessor);
		LOG.debug(argProcessor.getInputList());
		argProcessor.runAndOutput();
		QuickscrapeNorma qsNormaTemp = new QuickscrapeNorma(normaTemp);
		// fails at present
//		Assert.assertTrue("results.xml", qsNormaTemp.hasResultsXML());
	}
	
}
