package org.xmlcml.ami.visitor.chem;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.util.AMITestUtil;
import org.xmlcml.ami.visitor.species.SpeciesVisitor;
import org.xmlcml.ami.util.AMIUtil;

public class SuppdataTest {

	@Test
	@Ignore 
	// FIXME generates PNG in toplevel directory
	public void testSpect() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/pdf/spect.pdf",
				"-o", "target/spect/",
				"-e", AMIUtil.XML
		};
		SpeciesVisitor.main(args);
	}

	@Test
	@Ignore 
	// FIXME generates *.png in toplevel dir
	public void testSpectAcs() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/pdf/acs1.pdf",
				"-o", "target/spect/",
				"-e", AMIUtil.XML
		};
		SpeciesVisitor.main(args);
	}

	@Test
	@Ignore // too long for routine tests
	public void testSuSpectAcs() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/pdf/suspect_ol2015972_si_002.pdf",
				"-o", "target/spect/",
				"-e", AMIUtil.XML
		};
		SpeciesVisitor.main(args);
	}
}
