package org.xmlcml.ami.visitor.tree;

import java.io.File;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.tree.nexml.NexmlElement;
import org.xmlcml.ami.visitor.tree.nexml.NexmlNEXML;
import org.xmlcml.xml.XMLUtil;

public class TreeVistorTest {
	
	
	private static final Logger LOG = Logger.getLogger(TreeVistorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testCommandline() throws Exception {
		String[] args = new String[] {
				"-i", new File(Fixtures.TREE_DIR, "birds.clean.svg").toString(),
				"-o", new File(Fixtures.AMI_OUT,"/svg/birds.clean.xml").toString(),
		};
		TreeVisitor.main(args);
	}

	@Test
	@Ignore // outfile not clear
	public void testBirdsSmall() throws Exception {
		File outfile = new File(Fixtures.AMI_OUT,"/svg/birds.clean.small.xml");
		String[] args = new String[] {
				"-i", new File(Fixtures.TREE_DIR, "birds.clean.small.svg").toString(),
				"-o", outfile.toString(),
		};
		TreeVisitor.main(args);
		Element nexmlElement = XMLUtil.parseQuietlyToDocument(new File(outfile, "results.xml")).getRootElement().getChildElements().get(0);
		NexmlNEXML nexml = (NexmlNEXML) NexmlElement.readAndCreateNEXML(nexmlElement);
		String newick = nexml.createNewick();
		LOG.debug("nwk "+newick);
	}

	@Test
	public void testBirdsSmaller() throws Exception {
		String[] args = new String[] {
				"-i", new File(Fixtures.TREE_DIR, "birds.clean.small1.svg").toString(),
				"-o", new File(Fixtures.AMI_OUT,"/svg/birds.clean.small1.xml").toString(),
		};
		TreeVisitor.main(args);
	}

	@Test
	public void testCommandlineGibbons() throws Exception {
		String[] args = new String[] {
				"-i", new File(Fixtures.TREE_DIR, "gibbons.large.svg").toString(),
				"-o", new File(Fixtures.AMI_OUT,"/svg/gibbons.large.xml").toString(),
		};
		TreeVisitor.main(args);
	}

	@Test
	public void testPhyloTree() throws Exception {
		String[] args = new String[] {
				"-i", new File(Fixtures.TREE_DIR, "ijs.0.014126-0-000.pbm.png").toString(),
				"-o", new File(Fixtures.AMI_OUT,"/png/ijs.0.014126-0-000.xml").toString(),
		};
		TreeVisitor.main(args);
	}

}
