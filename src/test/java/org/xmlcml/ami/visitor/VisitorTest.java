package org.xmlcml.ami.visitor;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.species.SpeciesVisitor;

public class VisitorTest {

	@Test
	@Ignore
	public void testHtmlVisitable() throws Exception {
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.addFile(Fixtures.METABOLITE_00039_HTML);
		htmlVisitable.addFile(new File(Fixtures.HTML_DIR, "pb1.html"));
		
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		speciesVisitor.visit(htmlVisitable);
	}
}
