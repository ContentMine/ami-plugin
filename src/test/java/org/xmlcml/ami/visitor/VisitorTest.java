package org.xmlcml.ami.visitor;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.util.AMIUtil;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitor.regex.RegexVisitor;
import org.xmlcml.ami.visitor.species.SpeciesVisitor;

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
				"-g", "regex/phylotree.xml",
				"-e", AMIUtil.XML
		};
		RegexVisitor.main(args);
	}
	
}
