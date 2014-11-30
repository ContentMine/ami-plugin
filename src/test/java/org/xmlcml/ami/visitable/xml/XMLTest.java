package org.xmlcml.ami.visitable.xml;

import java.io.File;

import nu.xom.Builder;
import nu.xom.Document;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;

@Ignore // requires the Internet
public class XMLTest {
	private static final Logger LOG = Logger.getLogger(XMLTest.class);

	@Test
	public void testSimpleParsePLOS() throws Exception {
		Document doc;
		LOG.debug("0");
		doc = new Builder().build(new File(Fixtures.XML_DIR, "journal.pone.0077058.xml"));
		LOG.debug("0077058");
		doc = new Builder().build(new File(Fixtures.XML_DIR, "journal.pone.0079900.xml"));
		LOG.debug("0079900");
		doc = new Builder().build(new File(Fixtures.XML_DIR, "journal.pone.0080147.xml"));
		LOG.debug("0080147");
	}
	
	@Test
	public void testSimpleParsePLOSStandalone() throws Exception {
		Document doc;
		LOG.debug("0");
		doc = new Builder().build(new File(Fixtures.XML_DIR, "journal.pone.0077058.standalone.xml"));
		LOG.debug("0077058");
		doc = new Builder().build(new File(Fixtures.XML_DIR, "journal.pone.0079900.standalone.xml"));
		LOG.debug("0079900");
		doc = new Builder().build(new File(Fixtures.XML_DIR, "journal.pone.0080147.standalone.xml"));
		LOG.debug("0080147");
	}
	
	@Test
	public void testSimpleParsePLOSNoDTD() throws Exception {
		Document doc;
		LOG.debug("0");
		doc = new Builder().build(new File(Fixtures.XML_DIR, "journal.pone.0077058.nodtd.xml"));
		LOG.debug("0077058");
		doc = new Builder().build(new File(Fixtures.XML_DIR, "journal.pone.0079900.nodtd.xml"));
		LOG.debug("0079900");
		doc = new Builder().build(new File(Fixtures.XML_DIR, "journal.pone.0080147.nodtd.xml"));
		LOG.debug("0080147");
	}
}
