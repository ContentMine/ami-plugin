package org.xmlcml.ami.visitor.plot;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.plot.GraphPlotBox;
import org.xmlcml.ami.visitor.plot.SVGLineAnalyzer;
import org.xmlcml.graphics.svg.SVGG;

public class SVGLineAnalyzerTest {

	private static final Logger LOG = Logger.getLogger(SVGLineAnalyzerTest.class);

	@Test
	public void testFindAxes() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		SVGLineAnalyzer lineAnalyzer = new SVGLineAnalyzer();
		List<GraphPlotBox> plotBoxList = lineAnalyzer.findGraphPlotBoxList(g);
		LOG.trace("boxes: "+plotBoxList.size());
	}
	
	@Test
	public void testFindAxesBadEps() {
		
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		SVGLineAnalyzer lineAnalyzer = new SVGLineAnalyzer();
		lineAnalyzer.setEpsilon(0.5);
		List<GraphPlotBox> plotBoxList = lineAnalyzer.findGraphPlotBoxList(g);
		LOG.trace("boxes: "+plotBoxList.size());
	}
	
	public void testInternal() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		SVGLineAnalyzer lineAnalyzer = new SVGLineAnalyzer();
		LOG.debug("=====================debug====================");
		LOG.debug(lineAnalyzer.debug());
	}
	
	public void testLineAngles() {
		SVGG g = SVGG.createSVGGChunk(Fixtures.LINEPLOTS_10_2_SVG,  "./svg:g", 0);
		SVGLineAnalyzer lineAnalyzer = new SVGLineAnalyzer();
		LOG.debug("====================line angles======================");
		LOG.debug("LineAngles: \n"+lineAnalyzer.getLineAngleMap());
		GraphPlotBox graphPlotBox = lineAnalyzer.getPlotBox();
		LOG.debug("plot box "+graphPlotBox);
	}
}
