package org.xmlcml.ami.visitor.chem.oscar;

import java.io.File;

import org.junit.Test;
import org.xmlcml.ami.Fixtures;

public class OscarTest {

	@Test
	public void testOscar() throws Exception {
		String[] args = new String[] {
			"-i", "src/test/resources/org/xmlcml/ami/chem/oscar/para.html",
			"-o", new File(Fixtures.AMI_OUT, "oscar.xml").toString(),
		};
		OscarVisitor.main(args);
	}
}
