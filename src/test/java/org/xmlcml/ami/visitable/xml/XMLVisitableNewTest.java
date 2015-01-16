package org.xmlcml.ami.visitable.xml;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.xml.XMLUtil;

public class XMLVisitableNewTest {
	@Test
	@Ignore //DTD problem
	public void testGetDoi() throws Exception {
		Element xmlElement = XMLUtil.parseQuietlyToDocumentWithoutDTD(Fixtures.PLOSONE_0080753_XML).getRootElement();
		XMLContainer xmlContainer = new XMLContainer(Fixtures.PLOSONE_0080753_XML, xmlElement);
		String doi = xmlContainer.getDoi();
		Assert.assertEquals("doi", "10.1371/journal.pone.0080753", doi);
	}

}
