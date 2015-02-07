package org.xmlcml.ami.visitor;

import org.junit.Test;
import org.xmlcml.files.EuclidSource;

public class SimpleVisitorTest {

	@Test
	public void testHelp() throws Exception {
		String[] args = new String[] {
				"--help"
		};
		SimpleVisitor.main(args);
	}

	@Test
	public void testArgs() throws Exception {
		String[] args = new String[] {
				"-i", "http://www.biomedcentral.com/content/download/xml/1471-2148-13-191.xml",
				"-e", EuclidSource.XML
		};
		SimpleVisitor.main(args);
	}
}
