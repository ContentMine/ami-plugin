package org.xmlcml.xhtml2stm.visitor.chem;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.xhtml2stm.util.Util;
import org.xmlcml.xhtml2stm.visitor.species.SpeciesVisitor;

public class SuppdataTest {

	@Test
	@Ignore 
	// FIXME generates PNG in toplevel directory
	public void testSpect() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/xhtml2stm/pdf/spect.pdf",
				"-o", "target/spect/",
				"-e", Util.XML
		};
		SpeciesVisitor.main(args);
	}

	@Test
	@Ignore 
	// FIXME generates *.png in toplevel dir
	public void testSpectAcs() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/xhtml2stm/pdf/acs1.pdf",
				"-o", "target/spect/",
				"-e", Util.XML
		};
		SpeciesVisitor.main(args);
	}

	@Test
	@Ignore // too long for routine tests
	public void testSuSpectAcs() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/xhtml2stm/pdf/suspect_ol2015972_si_002.pdf",
				"-o", "target/spect/",
				"-e", Util.XML
		};
		SpeciesVisitor.main(args);
	}
}
