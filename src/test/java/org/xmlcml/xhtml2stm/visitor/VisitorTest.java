package org.xmlcml.xhtml2stm.visitor;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.xhtml2stm.Fixtures;
import org.xmlcml.xhtml2stm.visitable.html.HtmlVisitable;
import org.xmlcml.xhtml2stm.visitor.species.SpeciesVisitor;

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
