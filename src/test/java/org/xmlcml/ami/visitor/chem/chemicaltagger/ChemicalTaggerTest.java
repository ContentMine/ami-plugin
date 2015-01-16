package org.xmlcml.ami.visitor.chem.chemicaltagger;

import java.io.File;

import org.junit.Test;
import org.xmlcml.ami.Fixtures;

public class ChemicalTaggerTest {

	@Test
	public void testChemicalTagger() throws Exception {
		String[] args = new String[] {
			"-i", "src/test/resources/org/xmlcml/ami/chem/oscar/para.html",
			"-o", new File(Fixtures.AMI_OUT, "chemicaltagger.xml").toString(),
		};
		ChemicalTaggerVisitor.main(args);
	}
}
