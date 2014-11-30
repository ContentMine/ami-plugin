package org.xmlcml.ami.visitor.tree;

import java.io.File;

import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.tree.TreeVisitor;

public class TreeVistorTest {
	
	@Test
	public void testCommandline() throws Exception {
		String[] args = new String[] {
				"-i", new File(Fixtures.TREE_DIR, "birds.clean.svg").toString(),
				"-o", new File(Fixtures.AMI_OUT,"/svg/birds.clean.xml").toString(),
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
