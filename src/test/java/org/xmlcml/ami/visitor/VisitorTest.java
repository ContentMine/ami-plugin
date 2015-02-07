package org.xmlcml.ami.visitor;

import org.junit.Test;
import org.xmlcml.ami.visitor.regex.RegexVisitor;
import org.xmlcml.files.EuclidSource;

public class VisitorTest {

	@Test
	public void testHelp() throws Exception {
		String[] args = new String[] {
				"--help"
		};
		RegexVisitor.main(args);
	}

	@Test
	public void testArgs() throws Exception {
		String[] args = new String[] {
				"-i", "http://www.biomedcentral.com/content/download/xml/1471-2148-13-191.xml",
//				"-r.r", "regex/phylotree.xml",
				"-e", EuclidSource.XML
		};
		RegexVisitor.main(args);
	}
	
}
