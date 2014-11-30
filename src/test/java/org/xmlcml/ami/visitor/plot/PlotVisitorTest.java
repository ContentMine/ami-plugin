package org.xmlcml.ami.visitor.plot;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitor.plot.PlotVisitor;
import org.xmlcml.graphics.svg.SVGElement;

public class PlotVisitorTest {

	private final static Logger LOG = Logger.getLogger(PlotVisitorTest.class);
	
	@Test
	@Ignore // fix visitable to return null
	public void testHtmlVisitable() throws Exception {
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.addFile(Fixtures.METABOLITE_00039_HTML);
		htmlVisitable.addFile(new File(Fixtures.HTML_DIR, "pb1.html"));
		
		PlotVisitor plotVisitor = new PlotVisitor();
		try {
			plotVisitor.visit(htmlVisitable);
			Assert.fail("Should fail)");
		} catch (Exception e) {
			LOG.error("Failed as expected "+e);
		}
	}

	@Test
	public void testSVGVisitable() throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		File file = new File(Fixtures.FIGURE_DIR, "astro.g.6.2.fig.left.svg");
		svgVisitable.addSVGElement(file, SVGElement.readAndCreateSVG(file));
		
		PlotVisitor plotVisitor = new PlotVisitor();
		plotVisitor.visit(svgVisitable);
	}

	@Test
	@Ignore
	public void testSVGVisitableDir() throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		File[] files = Fixtures.MOLECULES_DIR.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.toString().endsWith(".svg")) {
					svgVisitable.addSVGFile(file);
				}
			}
		}
		PlotVisitor plotVisitor = new PlotVisitor();
		plotVisitor.visit(svgVisitable);
	}

}
