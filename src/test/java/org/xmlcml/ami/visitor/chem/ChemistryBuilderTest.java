package org.xmlcml.ami.visitor.chem;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.chem.ChemistryBuilder;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Units;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;

/**
 * @author pm286
 */
//@Ignore ("At least several depend on SVG text processing and titles")
public class ChemistryBuilderTest {

	private final static Logger LOG = Logger.getLogger(ChemistryBuilderTest.class);
	public static final Angle MAX_ANGLE = new Angle(0.12, Units.RADIANS);
	public static final Double MAX_WIDTH = 2.0;
	
	public static void main(String[] args) {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(args[0])));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
	}
	
	@Test
	@Ignore("Too slow; added reduced version for regression testing")
	public void testWedges() {
		SVGElement el = SVGElement.readAndCreateSVG(Fixtures.WEDGES);
		for (int i = 0; i < el.getChildElements().size(); i++) {
			try {
				((SVGElement) el.getChildElements().get(i)).applyTransformAttributeAndRemove();
			} catch (Exception e) {
				
			}
		}
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(el);
		simpleBuilder.createHigherPrimitives();
		/*Iterator<Joinable> it = simpleBuilder.higherPrimitives.getJoinableList().iterator();
		while (it.hasNext()) {
			if (it.next() instanceof WedgeBond) {
				it.remove();
			}
		}*/
		simpleBuilder.draw();
		Assert.assertEquals(el.getChildElements("polyline", SVGSVG.SVG_NS).size(), simpleBuilder.wedgeBonds.size());//4 are tiny so are sensitive to the group simplification distance threshold; 2 aren't valid stereochemical bonds, so it would be fine if they failed
	}

	@Test
	public void testWedgesReduced() {
		SVGElement el = SVGElement.readAndCreateSVG(Fixtures.WEDGESREDUCED);
		for (int i = 0; i < el.getChildElements().size(); i++) {
			try {
				((SVGElement) el.getChildElements().get(i)).applyTransformAttributeAndRemove();
			} catch (Exception e) {
				
			}
		}
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(el);
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals(el.getChildElements("polyline", SVGSVG.SVG_NS).size(), simpleBuilder.wedgeBonds.size());
	}
	
	@Test
	public void testArrowheads() {
		SVGElement el = SVGElement.readAndCreateSVG(Fixtures.ARROWHEADS);
		for (int i = 0; i < el.getChildElements().size(); i++) {
			try {
				((SVGElement) el.getChildElements().get(i)).applyTransformAttributeAndRemove();
			} catch (Exception e) {
				
			}
		}
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(el);
		simpleBuilder.createHigherPrimitives();
		/*Iterator<Joinable> it = simpleBuilder.higherPrimitives.getJoinableList().iterator();
		while (it.hasNext()) {
			if (it.next() instanceof WedgeBond) {
				it.remove();
			}
		}*/
		simpleBuilder.draw();
		Assert.assertEquals(el.getChildElements("path", SVGSVG.SVG_NS).size() + el.getChildElements("polyline", SVGSVG.SVG_NS).size(), simpleBuilder.wedgeBonds.size());
	}
	
	@Test
	@Ignore("Too slow; added reduced version for regression testing")
	public void testThickLines() {
		SVGElement el = SVGElement.readAndCreateSVG(Fixtures.THICKLINES);
		for (int i = 0; i < el.getChildElements().size(); i++) {
			try {
				((SVGElement) el.getChildElements().get(i)).applyTransformAttributeAndRemove();
			} catch (Exception e) {
				
			}
		}
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(el);
		simpleBuilder.createHigherPrimitives();
		/*Iterator<Joinable> it = simpleBuilder.higherPrimitives.getJoinableList().iterator();
		while (it.hasNext()) {
			if (it.next() instanceof WedgeBond) {
				it.remove();
			}
		}*/
		simpleBuilder.draw();
		Assert.assertEquals(0, simpleBuilder.wedgeBonds.size());
		/*for (SVGPolygon polygon : simpleBuilder.getDerivedPrimitives().getPolygonList()) {
			Assert.assertEquals(4, polygon.getReal2Array().size());
		}*/
	}
	
	@Test
	@Ignore("Too slow; added reduced version for regression testing")
	public void testThickLinesReduced() {
		SVGElement el = SVGElement.readAndCreateSVG(Fixtures.THICKLINESREDUCED);
		for (int i = 0; i < el.getChildElements().size(); i++) {
			try {
				((SVGElement) el.getChildElements().get(i)).applyTransformAttributeAndRemove();
			} catch (Exception e) {
				
			}
		}
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(el);
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals(0, simpleBuilder.wedgeBonds.size());
	}
	
	@Test
	public void test2_10() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_10_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 44, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 19, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 20, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 2, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 0, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 7, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 48, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 56, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 25 + 1, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	public void test2_11() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_11_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 6, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 6, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 4, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 0, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 0, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 1, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 11, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 8, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 6, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	public void test2_13() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_13_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 13, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 6, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 7, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 0, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 0, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 3, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 16, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 17, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 10, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	public void test2_15() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_15_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 40, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 24, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 21, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 1, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 2, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 7, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 55, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 59, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 25 + 3, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	public void test2_16() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_16_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 20, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 7, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 10, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 0, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 0, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 5, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 22, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 24, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 14 + 1, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	//TODO 2_18
	
	@Test
	public void test2_21() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_21_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 62, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 37, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 27, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 4, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 2, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 6, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 76, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		Assert.assertEquals("junctions", 36 + 3, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	@Ignore // FIXME fails test on junctions
	public void test2_22() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_22_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 78, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 61, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 24, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 6, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 2, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 5, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 98, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		Assert.assertEquals("junctions", 33, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	public void test2_23() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_23_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 24, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 19, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 16, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 0, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 0, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 4, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 39, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 37, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 21, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	public void test2_25() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_25_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 35, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 13, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 15, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 2, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 3, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 5, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 38, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		Assert.assertEquals("junctions", 21 + 1, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}

	//TODO
	@Test
	@Ignore("Waiting for clutter-ignoring code, although possibly should not be here at all")
	public void test3_10() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_3_10_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 35, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 13, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 15, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 2, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 3, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 5, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 38, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		Assert.assertEquals("junctions", 21, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}

	//TODO
	@Test
	@Ignore("Waiting for delocalisation-handling / clutter-ignoring code")
	public void test3_15() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_3_15_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 105 + 119 + 3 + 40 + 12, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 57 + 2, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 93 + 3 + 40 + 12, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 21, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 9 + 3, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 6, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 57 + 93 + 21 + 9 + 6 + 2 + 3 + 3 + 40 + 12, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		Assert.assertEquals("junctions", 24 + 23 + 23 + 24 + 8 + 3, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	public void test5_11() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_5_11_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 47, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 0, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 29, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 3, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 5, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 4, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 41, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 68, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 24 + 15, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}	
	
	@Test
	public void test5_12() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_5_12_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 47, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 0, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 29, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 2, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 6, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 6, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 43, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 67, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 25 + 17, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	//TODO
	@Test
	@Ignore("Waiting for image to text conversion")
	public void test5_13() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_5_13_SVG));
		simpleBuilder.createHigherPrimitives();
		//first 37, 48, 26; second 39, 51, 27
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 88, simpleBuilder.getDerivedPrimitives().getLineList().size());//FIXME lines were missed in the PDF to SVG conversion stage;
		Assert.assertEquals("texts", 0, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 54, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 5, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 9, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 11, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 78, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 130, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 42, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	//TODO
	@Test
	@Ignore("Waiting for image to text conversion")
	public void test5_14() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_5_14_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 95, simpleBuilder.getDerivedPrimitives().getLineList().size());//FIXME lines were missed in the PDF to SVG conversion stage;
		Assert.assertEquals("texts", 0, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 58, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 5, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 7, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 13, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 83, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 143, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 48, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	public void test6_5() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_02_00100_6_5_SVG));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 63, simpleBuilder.getDerivedPrimitives().getLineList().size());//30
		Assert.assertEquals("texts", 78, simpleBuilder.getDerivedPrimitives().getTextList().size());//43
		Assert.assertEquals("higher lines", 35, simpleBuilder.getHigherPrimitives().getLineList().size());//18
		Assert.assertEquals("hatches", 0, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 3, simpleBuilder.getDerivedPrimitives().getPolygonList().size());//2
		Assert.assertEquals("tram lines", 14, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());//6
		Assert.assertEquals("joinables", 133, simpleBuilder.getHigherPrimitives().getJoinableList().size());//69
		//Assert.assertEquals("raw" junctions", 41, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 59 + 11, simpleBuilder.getHigherPrimitives().getJunctionList().size());//25
	}
	
	@Test
	public void test8_3() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.8.3.svg")));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 76, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 69, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 42, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 0, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 2, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 17, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 130, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		Assert.assertEquals("junctions", 57 + 6, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	//TODO
	@Test
	@Ignore("Waiting for advanced wedge handling")
	public void test9_6() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.9.6.svg")));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("charges", 3, simpleBuilder.getHigherPrimitives().getLineChargeList().size());
		Assert.assertTrue(false);
		//Assert.assertEquals("derived lines", 76, simpleBuilder.getDerivedPrimitives().getLineList().size());
		//Assert.assertEquals("texts", 69, simpleBuilder.getDerivedPrimitives().getTextList().size());
		//Assert.assertEquals("higher lines", 42, simpleBuilder.getHigherPrimitives().getLineList().size());
		//Assert.assertEquals("hatches", 0, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		//Assert.assertEquals("polygons", 2, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		//Assert.assertEquals("tram lines", 17, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		//Assert.assertEquals("joinables", 130, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("junctions", 57, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	@Ignore("Needs scale-based configuration")
	public void testSegments() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "segments.svg")));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 21, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 0, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 11, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 0, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 0, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 5, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 15, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		Assert.assertEquals("junctions", 10, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	@Ignore
	public void testJunctionMerging2_25() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_25_SVG));
		simpleBuilder.createHigherPrimitives();
		//no hatches; should be 25, 32, 20; l of Cl not circular enough, =O too near other bonds
		Assert.assertEquals("lines", 22, simpleBuilder.getHigherPrimitives().getJunctionList().size());
		Assert.assertEquals("lines", 15, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("lines", 5, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
	}
	
	@Test
	@Ignore
	public void testWedgesOld() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_18_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createRawAndDerivedLines();
		Assert.assertEquals("lines", 21, simpleBuilder.getDerivedPrimitives().getLineList().size());
	}
	
	@Test
	@Ignore
	public void testHard() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_25_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createRawAndDerivedLines();
		Assert.assertEquals("lines", 25, simpleBuilder.getDerivedPrimitives().getLineList().size());
	}
		
	@Test
	@Ignore
	public void testWithElementPNG5_11() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_5_11_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createRawAndDerivedLines();
		simpleBuilder.draw();
		Assert.assertEquals("lines", 46, simpleBuilder.getDerivedPrimitives().getLineList().size());//FIXME should be 47
	}
	
	@Test
	@Ignore
	public void testWithElementPNG5_12() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_5_12_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createRawAndDerivedLines();
		simpleBuilder.draw();
		Assert.assertEquals("lines", 45, simpleBuilder.getDerivedPrimitives().getLineList().size());
	}
	
	@Test
	public void testTramLinesG2_11() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_11_SVG));
		simpleBuilder.createHigherPrimitives();
		//List<SVGLine> lineList = simpleBuilder.createRawAndDerivedLines();
		Assert.assertEquals("lines", 6, simpleBuilder.getDerivedPrimitives().getLineList().size());
		//TramLineManager tramLineManager = new TramLineManager();
		//List<TramLine> tramLineList = tramLineManager.createTramLineList(lineList);
		//Assert.assertEquals("tramLines", 1, tramLineList.size());
		Assert.assertEquals("tramLines", 1, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
	}
	
	@Test
	public void testTramLines() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_13_SVG));
		//Assert.assertNull("singleLines", simpleBuilder.getDerivedPrimitives().getLineList());
		//Assert.assertNull("explicitLines", simpleBuilder.getRawPrimitives().getLineList());
		//Assert.assertNull("implicitLines", simpleBuilder.getDerivedPrimitives().getLineList());
		Assert.assertNull("derivedPrimitives", simpleBuilder.getDerivedPrimitives());
		simpleBuilder.createHigherPrimitives();
		//Assert.assertNull("paths", simpleBuilder.getExplicitPathList());
		//simpleBuilder.createRawAndDerivedLines();
		//Assert.assertEquals("singleLines", 13, simpleBuilder.getDerivedPrimitives().getLineList().size());
		//Assert.assertEquals("explicitLines", null, simpleBuilder.getRawPrimitives().getLineList().size());
		//Assert.assertEquals("implicitLines", 13, simpleBuilder.getDerivedPrimitives().getLineList().size());
		// creating lines has removed paths
		//Assert.assertEquals("paths", 0, simpleBuilder.getCurrentPathList().size());
		//List<TramLine> tramLineList = simpleBuilder.createTramLineList();
		//Assert.assertEquals("implicitLines", 13, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("lLines", 13, simpleBuilder.getDerivedPrimitives().getLineList().size());
		//TramLine creation removes single lines
		//Assert.assertEquals("tramLines", 3, tramLineList.size());
		Assert.assertEquals("tramLines", 3, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("singleLines", 7, simpleBuilder.getHigherPrimitives().getLineList().size());
		//Assert.assertEquals("explicitLines", 0, simpleBuilder.getRawPrimitives().getLineList().size());

	}
	
	@Test
	public void testTramLines2_11() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_11_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createTramLineList();
		Assert.assertEquals("lines", 4, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("tramLines", 1, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		//Assert.assertEquals("paths", 0, simpleBuilder.getRawPrimitives().getLineList().size());
	}
	
	@Test
	public void testTramLines2_13() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_13_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createTramLineList();
		Assert.assertEquals("tramLines", 3, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("singleLines", 13, simpleBuilder.getDerivedPrimitives().getLineList().size());
	}
	
	@Test
	public void testTramLines2_18() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_18_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createTramLineList();
		//Assert.assertEquals("paths", 1, simpleBuilder.getRawPrimitives().getLineList().size());
		Assert.assertEquals("tramLines", 5, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("singleLines", 21, simpleBuilder.getDerivedPrimitives().getLineList().size());
	}
	
	@Test
	public void testTramLines2_23() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_23_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createTramLineList();
		Assert.assertEquals("tramLines", 4, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		//Assert.assertEquals("paths", 0, simpleBuilder.getRawPrimitives().getLineList().size());
		Assert.assertEquals("paths", 16, simpleBuilder.getHigherPrimitives().getLineList().size());
	}
	
	@Test
	@Ignore
	public void testTramLines2_25() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_25_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createTramLineList();
		Assert.assertEquals("tramLines", 5, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		//Assert.assertEquals("paths", 1, simpleBuilder.getRawPrimitives().getLineList().size());
		Assert.assertEquals("paths", 15, simpleBuilder.getHigherPrimitives().getLineList().size());
	}
	
	@Test
	@Ignore
	public void testTramLines5_11() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_5_11_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createTramLineList();
		simpleBuilder.draw();
		Assert.assertEquals("tramLines", 4, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		//Assert.assertEquals("paths", 0, simpleBuilder.getRawPrimitives().getLineList().size());
		Assert.assertEquals("paths", 28, simpleBuilder.getHigherPrimitives().getLineList().size());//FIXME should be 39
	}
	
	@Test
	@Ignore
	public void testWedgeHash() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_18_SVG));
		simpleBuilder.createHigherPrimitives();
		//simpleBuilder.createRawAndDerivedLines();
		// this contained a rect translated to a line
		//Assert.assertEquals("explicitLines", 1, simpleBuilder.getRawPrimitives().getLineList().size());
		//Assert.assertEquals("implicitLines", 21, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("singleLines", 21, simpleBuilder.getHigherPrimitives().getLineList().size());
		//Assert.assertEquals("paths", 0, simpleBuilder.getCurrentPathList().size());
		// polygon and 5 circles
		//Assert.assertEquals("shapes", 6, simpleBuilder.getCurrentShapeList().size());
		// creating lines has removed paths
		//Assert.assertEquals("paths", 0, simpleBuilder.getCurrentPathList().size());
		//List<TramLine> tramLineList = simpleBuilder.createTramLineList();
		Assert.assertEquals("implicitLines", 21, simpleBuilder.getDerivedPrimitives().getLineList().size());
		//TramLine creation removes single lines
		Assert.assertEquals("singleLines", 12, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("tramLines", 5, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("explicitLines", 1, simpleBuilder.getRawPrimitives().getLineList().size());

	}
		
	@Test
	public void testJunctionWithTram() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_11_SVG));
		simpleBuilder.createHigherPrimitives();
		//List<SVGLine> lineList = simpleBuilder.createRawAndDerivedLines();
		Assert.assertEquals("lines", 6, simpleBuilder.getDerivedPrimitives().getLineList().size());
		//TramLineManager tramLineManager = new TramLineManager();
		//List<TramLine> tramLineList = tramLineManager.createTramLineList(lineList);
		//lineList = tramLineManager.removeUsedTramLinePrimitives(lineList);
		//Assert.assertEquals("tramLines", 1, tramLineList.size());
		Assert.assertEquals("tramLines", 1, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		//List<Joinable> joinableList = JoinManager.makeJoinableList(lineList);
		//Assert.assertEquals("no tram", 4, joinableList.size());
		//joinableList.add(tramLineList.get(0));
		//Assert.assertEquals("joinable", 5, joinableList.size());
		//List<Junction> junctionList = simpleBuilder.createRawJunctionList();
		//Assert.assertEquals("junction", 7, junctionList.size());
		//Assert.assertEquals("junction", 8, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
	}

	@Test
	public void testJunctionWithTramAndText() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.IMAGE_2_11_SVG));
		simpleBuilder.createHigherPrimitives();
		//List<SVGLine> lineList = simpleBuilder.createRawAndDerivedLines();
		Assert.assertEquals("lines", 6, simpleBuilder.getDerivedPrimitives().getLineList().size());
		//TramLineManager tramLineManager = new TramLineManager();
		//List<TramLine> tramLineList = tramLineManager.createTramLineList(lineList);
		//lineList = tramLineManager.removeUsedTramLinePrimitives(lineList);
		//List<Joinable> joinableList = JoinManager.makeJoinableList(lineList);
		//joinableList.addAll(tramLineList);
		//List<SVGText> textList = simpleBuilder.createRawTextList();
		//for (SVGText svgText : textList) {
			//joinableList.add(new JoinableText(svgText));
		//}
		Assert.assertEquals("text", 11, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//List<Junction> junctionList = simpleBuilder.createRawJunctionList();
		//for (Junction junction : junctionList) {
		//for (Junction junction : simpleBuilder.getHigherPrimitives().getRawJunctionList()) {
			//LOG.trace(junction);
		//}
		//Assert.assertEquals("junction", 8, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
	}
	
	@Test
	public void testSmall1() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.SMALL_TEST_1));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
	}
	
	@Test
	public void testSmall2() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.SMALL_TEST_2));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
	}
	
	@Test
	public void testSmall3() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.SMALL_TEST_3));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("lines", 8, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("lines", 3, simpleBuilder.getHigherPrimitives().getLineList().size());
	}
	
	@Test
	public void testSmall4() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.SMALL_TEST_4));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("lines", 8, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("lines", 3, simpleBuilder.getHigherPrimitives().getLineList().size());
		//Assert.assertEquals("junctions", 7, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 2 + 3, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}
	
	@Test
	public void testSmall5() {
		ChemistryBuilder simpleBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(Fixtures.SMALL_TEST_5));
		simpleBuilder.createHigherPrimitives();
		simpleBuilder.draw();
		Assert.assertEquals("derived lines", 5, simpleBuilder.getDerivedPrimitives().getLineList().size());
		Assert.assertEquals("texts", 0, simpleBuilder.getDerivedPrimitives().getTextList().size());
		Assert.assertEquals("higher lines", 5, simpleBuilder.getHigherPrimitives().getLineList().size());
		Assert.assertEquals("hatches", 0, simpleBuilder.getHigherPrimitives().getHatchedBondList().size());
		Assert.assertEquals("polygons", 1, simpleBuilder.getDerivedPrimitives().getPolygonList().size());
		Assert.assertEquals("tram lines", 0, simpleBuilder.getHigherPrimitives().getDoubleBondList().size());
		Assert.assertEquals("joinables", 6, simpleBuilder.getHigherPrimitives().getJoinableList().size());
		//Assert.assertEquals("raw" junctions", 9, simpleBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 2 + 5, simpleBuilder.getHigherPrimitives().getJunctionList().size());
	}

}