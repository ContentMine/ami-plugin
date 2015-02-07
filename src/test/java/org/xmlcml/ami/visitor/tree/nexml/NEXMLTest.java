package org.xmlcml.ami.visitor.tree.nexml;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;

public class NEXMLTest {
	
	private static final File BIRDS_SMALL1_NEXML = new File(Fixtures.TREE_DIR, "birds.small1.nexml");

	@Test
	public void testReadNEXML() throws Exception {
		NexmlNEXML nexml = (NexmlNEXML) NexmlElement.readAndCreateNEXML(BIRDS_SMALL1_NEXML);
		Assert.assertNotNull("nexml should not be null", nexml);
	}

	@Test
	public void testWriteNewick() throws Exception {
		NexmlNEXML nexml = (NexmlNEXML) NexmlElement.readAndCreateNEXML(BIRDS_SMALL1_NEXML);
		String newick = nexml.createNewick();
		Assert.assertEquals("newick", "((N4:80.0,N5:75.0)N6:28.0,(N3:102.0,(N1:54.0,N2:66.0)N9:40.0)N8:12.0)N7;", newick);
	}

}
