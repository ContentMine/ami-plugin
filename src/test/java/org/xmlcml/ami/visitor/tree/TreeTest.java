package org.xmlcml.ami.visitor.tree;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.tree.SVGXTree;
import org.xmlcml.ami.visitor.tree.TreeAnalyzer;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svg2xml.paths.ComplexLine;
import org.xmlcml.svg2xml.paths.ComplexLine.LineOrientation;
import org.xmlcml.svg2xml.paths.ComplexLine.SideOrientation;
import org.xmlcml.svg2xml.paths.LineMerger;
import org.xmlcml.svg2xml.paths.LineMerger.MergeMethod;

public class TreeTest {
	
	private final static Logger LOG = Logger.getLogger(TreeTest.class);

	public static final double EPS = 0.01;
	
	@Test
	public void testCluster() throws Exception {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.TREE_CLUSTER1_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines((SVGElement)svg.getChildElements().get(0));
		Assert.assertEquals("lines", 249, svgLines.size());
		List<SVGLine> zeroLines = ComplexLine.createSubsetAndRemove(svgLines, LineOrientation.ZERO, EPS);
		Assert.assertEquals("lines", 71, zeroLines.size());
		Assert.assertEquals("lines", 178, svgLines.size());
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS);
		Assert.assertEquals(83, verticalLines.size());
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS);
		Assert.assertEquals(95, horizontalLines.size());
		SVGUtil.debug(svg, new FileOutputStream(Fixtures.TREE_CLUSTER1A_SVG),1);
	}

	@Test
	public void testClusterRedrawTree() throws Exception {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.TREE_CLUSTER1_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines((SVGElement)svg.getChildElements().get(0));
		List<SVGLine> zeroLines = ComplexLine.createSubsetAndRemove(svgLines, LineOrientation.ZERO, EPS);
		extendZeroLines(zeroLines, LineOrientation.HORIZONTAL, 1.5f);
		SVGUtil.debug(svg, new FileOutputStream(Fixtures.TREE_CLUSTER1B_SVG),1);
	}

	private void extendZeroLines(List<SVGLine> svgLines, LineOrientation lineOrientation, double length) {
		for (SVGLine line : svgLines) {
			if (line.isZero(EPS)) {
				Real2 point = line.getXY(0);
				Real2 extension = (lineOrientation.equals(LineOrientation.HORIZONTAL)) ? new Real2(length, 0.0) : new Real2(0.0, length);
				point = point.plus(extension);
				line.setXY(point, 1);
			}
		}
	}
	
	@Test
	public void testCluster1() throws Exception {
		double EPS1 = 0.3;
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.TREE_CLUSTER1_SVG);
		List<SVGLine> svgLines = SVGLine.extractSelfAndDescendantLines((SVGElement)svg.getChildElements().get(0));
		for (SVGLine line : svgLines) {
			line.setStrokeWidth(0.3);
		}
		List<SVGLine> zeroLines = ComplexLine.createSubsetAndRemove(svgLines, LineOrientation.ZERO, EPS1);
		Assert.assertTrue(72 >= zeroLines.size() && zeroLines.size() >= 71);
		//addZeroAsPoints(zeroLines, svg);
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS1);
		Assert.assertEquals(83, verticalLines.size());
		List<SVGLine> joinedVerticalLines = LineMerger.mergeLines(verticalLines, EPS1, MergeMethod.TOUCHING_LINES);
		int size = joinedVerticalLines.size();
		Assert.assertTrue("size "+size, 81 >= size && size >= 80);
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS1);
		extendZeroLines(zeroLines, LineOrientation.HORIZONTAL, 0.5f);
		List<ComplexLine> verticalComplexLines = 
				ComplexLine.createComplexLines(joinedVerticalLines, horizontalLines, EPS1);
		//SVGXTree tree = new SVGXTree();
		//tree.analyzeLines(verticalComplexLines, SideOrientation.MINUS);
		
		SVGUtil.debug(svg, new FileOutputStream(Fixtures.TREE_CLUSTER2A_SVG),1);
	}

	@Test
	public void testCluster7() throws Exception {
		double EPS1 = 0.3;
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.TREE_CLUSTER1_SVG);
		List<SVGElement> lineElements = SVGUtil.getQuerySVGElements(svg,  ".//svg:line");
		List<SVGLine> svgLines = SVGLine.extractLines(lineElements);
		Assert.assertEquals(249, svgLines.size());
		for (SVGLine line : svgLines) {
			line.setStrokeWidth(0.3);
		}
		List<SVGLine> zeroLines = ComplexLine.createSubsetAndRemove(svgLines, LineOrientation.ZERO, EPS1);
		Assert.assertEquals(72, zeroLines.size());
		List<SVGLine> verticalLines = ComplexLine.createSubset(svgLines, LineOrientation.VERTICAL, EPS1);
		Assert.assertEquals(83, verticalLines.size());
		List<SVGLine> horizontalLines = ComplexLine.createSubset(svgLines, LineOrientation.HORIZONTAL, EPS1);
		Assert.assertEquals(94, horizontalLines.size());
		List<ComplexLine> verticalComplexLines = ComplexLine.createComplexLines(verticalLines, horizontalLines, EPS1);
		List<ComplexLine> horizontalComplexLines = ComplexLine.createComplexLines(horizontalLines, verticalLines, EPS1);
		
		SVGXTree tree = new SVGXTree((SVGG)svgLines.get(0).getParent());
		
		List<ComplexLine> emptyEndedHorizontalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(horizontalComplexLines, SideOrientation.EMPTYLIST);
		Assert.assertEquals(0, emptyEndedHorizontalLines.size());
		List<ComplexLine> doubleEndedHorizontalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(horizontalComplexLines, SideOrientation.MINUSPLUSLIST);
		Assert.assertEquals(57, doubleEndedHorizontalLines.size());
		List<ComplexLine> minusEndedHorizontalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(horizontalComplexLines, SideOrientation.MINUSLIST);
		Assert.assertEquals(37, minusEndedHorizontalLines.size());
		List<ComplexLine> plusEndedHorizontalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(horizontalComplexLines, SideOrientation.PLUSLIST);
		Assert.assertEquals(0, plusEndedHorizontalLines.size()); // 0

		
		List<ComplexLine> emptyEndedVerticalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(verticalComplexLines, SideOrientation.EMPTYLIST);
		Assert.assertEquals(18, emptyEndedVerticalLines.size());
		List<ComplexLine> doubleEndedVerticalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(verticalComplexLines, SideOrientation.MINUSPLUSLIST);
		Assert.assertEquals(31, doubleEndedVerticalLines.size());
		List<ComplexLine> minusEndedVerticalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(verticalComplexLines, SideOrientation.MINUSLIST);
		Assert.assertEquals(11, minusEndedVerticalLines.size());
		List<ComplexLine> plusEndedVerticalLines = 
				ComplexLine.extractLinesWithBranchAtEnd(verticalComplexLines, SideOrientation.PLUSLIST);
		Assert.assertEquals(23, plusEndedVerticalLines.size()); // 0

		tree.getTreeAnalyzer().extractLinesWithBranchAtEnd(LineOrientation.HORIZONTAL, SideOrientation.EMPTYLIST);
		
		SVGUtil.debug(svg, new FileOutputStream(Fixtures.TREE_CLUSTER2A_SVG),1);
	}

	@Test
	public void testCluster7a() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.TREE_PANEL1_SVG);
		List<SVGElement> lineElements = SVGUtil.getQuerySVGElements(svg,  ".//svg:line");
		List<SVGLine> svgLines = SVGLine.extractLines(lineElements);
		
		SVGXTree tree = new SVGXTree((SVGG)svgLines.get(0).getParent());
		TreeAnalyzer treeAnalyzer = tree.getTreeAnalyzer();
		treeAnalyzer.analyzeBranchesAtLineEnds(tree, svgLines, EPS);
		
		Assert.assertEquals(0, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.HORIZONTAL, SideOrientation.EMPTYLIST).size());
		Assert.assertEquals(13, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.HORIZONTAL, SideOrientation.MINUSPLUSLIST).size());
		Assert.assertEquals(15, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.HORIZONTAL, SideOrientation.MINUSLIST).size());
		Assert.assertEquals(0, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.HORIZONTAL, SideOrientation.PLUSLIST).size());
		
		Assert.assertEquals(0, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.VERTICAL, SideOrientation.EMPTYLIST).size());
		Assert.assertEquals(14, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.VERTICAL, SideOrientation.MINUSPLUSLIST).size());
		Assert.assertEquals(0, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.VERTICAL, SideOrientation.MINUSLIST).size());
		Assert.assertEquals(0, treeAnalyzer.extractLinesWithBranchAtEnd(LineOrientation.VERTICAL, SideOrientation.PLUSLIST).size());
	}
	
	@Test
	public void testPanel1Tree() {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(Fixtures.TREE_PANEL1_SVG);
		List<SVGElement> lineElements = SVGUtil.getQuerySVGElements(svg, ".//svg:line");
		Assert.assertEquals("lines", 42, lineElements.size());
		List<SVGLine> svgLines = SVGLine.extractLines(lineElements);
		Assert.assertEquals("svgLines", 42, svgLines.size());
		
		SVGG g = (SVGG)svgLines.get(0).getParent();
		//g.debug("g");
		SVGXTree tree = new SVGXTree(g);
		
		TreeAnalyzer treeAnalyzer = tree.getTreeAnalyzer();
		Assert.assertNull("singleEnd null", treeAnalyzer.getSingleEndedLines());
		treeAnalyzer.analyzeBranchesAtLineEnds(tree, svgLines, EPS);
		int[] counts = { 14, 14, 0, 0, 14,   
				         28, 28, 15, 0, 13, 
				         15};
		assertHorizontalVerticalLines(treeAnalyzer, counts, LineOrientation.HORIZONTAL);
		tree.buildTree();
		SVGSVG.wrapAndWriteAsSVG(tree, new File("target/panel1Tree.svg"));
		//tree.debug("Tree");
	}

	@Test
	public void testImage82SmallTree() {
		int[] countsa = {28, 28, 21}; 
		int[] countsb = {7, 7, 0, 0, 7, 
		        14, 14, 8, 0,  6,
		        8};
		testTree(Fixtures.TREE_GIBBONS_SMALL_SVG, countsa, countsb, "target/82smallTree.svg", LineOrientation.HORIZONTAL);
	}
	
	@Test
	public void testImage82Tree() {
		int[] countsa = {43, 43, 32}; 
		int[] countsb = {11, 11, 0, 1, 10, 
		        21, 21, 11, 0,  10,
		        11};
		testTree(Fixtures.TREE_GIBBONS_LARGE_SVG, countsa, countsb, "target/82Tree.svg", LineOrientation.HORIZONTAL);
		
	}
	
	@Test
	public void testImage32aTree() {
		int[] countsa = {229, 229, 173}; 
		int[] countsb = new int[] {57, 57, 0, 1, 56, 
		        115, 115, 60, 0,  52,
		        60};
		testTree(Fixtures.TREE_BIRDS_CLEAN_SVG, countsa, countsb, "target/tree32a.svg", LineOrientation.HORIZONTAL);
	}

	@Test
	public void testImage32aNewTree() {
		SVGElement svg =  SVGElement.readAndCreateSVG(Fixtures.TREE_GIBBONS_SMALL_SVG);
		SVGXTree tree = makeTree(svg, 1.0, MergeMethod.TOUCHING_LINES);
		SVGSVG.wrapAndWriteAsSVG(tree, new File("target/newTree.svg"));
	}

	@Test
	public void testMakeTree() {
		SVGElement svg =  SVGElement.readAndCreateSVG(Fixtures.TREE_GIBBONS_SMALL_SVG);
		SVGXTree tree = SVGXTree.makeTree(svg, 1.0, MergeMethod.TOUCHING_LINES);
		SVGSVG.wrapAndWriteAsSVG(tree, new File("target/makeTree.svg"));
	}
	
	@Test
	public void testMakeTree32() {
		SVGElement svg =  SVGElement.readAndCreateSVG(Fixtures.TREE_BIRDS_CLEAN_SVG);
		SVGXTree tree = SVGXTree.makeTree(svg, 1.0, MergeMethod.TOUCHING_LINES);
		SVGSVG.wrapAndWriteAsSVG(tree, new File("target/makeTree32.svg"));
	}
	
	@Test
	public void testTreeWithNexML() {
		SVGElement svg =  SVGElement.readAndCreateSVG(Fixtures.TREE_GIBBONS_SMALL_SVG);
		SVGXTree tree = SVGXTree.makeTree(svg, 1.0, MergeMethod.TOUCHING_LINES);
		//CMLUtil.debug(tree.getNEXML(), "nexml");
		SVGSVG.wrapAndWriteAsSVG(tree, new File("target/smallGibbons.svg"));
	}

	@Test
	public void testLargeTreeWithNexML() {
		SVGElement svg =  SVGElement.readAndCreateSVG(Fixtures.TREE_GIBBONS_LARGE_SVG);
		SVGXTree tree = SVGXTree.makeTree(svg, 1.0, MergeMethod.TOUCHING_LINES);
		//CMLUtil.debug(tree.getNEXML(), "nexml");
		SVGSVG.wrapAndWriteAsSVG(tree, new File("target/Gibbons.svg"));
	}

	@Test
	public void testBirds() {
		SVGElement svg =  SVGElement.readAndCreateSVG(Fixtures.TREE_BIRDS_CLEAN_SVG);
		SVGXTree tree = SVGXTree.makeTree(svg, 1.0, MergeMethod.TOUCHING_LINES);
		//CMLUtil.debug(tree.getNEXML(), "nexml");
		SVGSVG.wrapAndWriteAsSVG(tree, new File("target/birds.svg"));
	}

	/** 
	 * Checks matching of species to nodes
	 * 
	 * &lt;text stroke="none" fill="#000000" 
     *       x="400.984"              y="105.165" font-size="8.762" font-family="Helvetica" font-weight="normal" font-style="italic" xmlns:svgx="http://www.xml-cml.org/schema/svgx"&gt;L&lt;/text&gt;
	 * Luscinia   / ((400.984,439.925),(96.403,105.165))
	 */
	@Test
	public void testBirdsSmall() {
		SVGElement svg =  SVGElement.readAndCreateSVG(Fixtures.TREE_BIRDS_CLEAN_SMALL_SVG);
		SVGXTree tree = SVGXTree.makeTree(svg, 1.0, MergeMethod.TOUCHING_LINES);
		//CMLUtil.debug(tree.getNEXML(), "nexml");
		SVGSVG.wrapAndWriteAsSVG(tree, new File("target/birdsSmall.svg"));
	}
	

	// ========================================================
	
	private void testTree(File insvg, int[] countsa, int[] countsb, String outfile, LineOrientation orientation) {
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(insvg);
		SVGPolyline.replacePolyLinesBySplitLines(svg);
		List<SVGElement> lineElements = SVGUtil.getQuerySVGElements(svg, ".//svg:line");
		Assert.assertEquals("lines", countsa[0], lineElements.size());
		List<SVGLine> svgLines = SVGLine.extractLines(lineElements);
		Assert.assertEquals(countsa[1], svgLines.size());
		svgLines = LineMerger.mergeLines(svgLines, 1.0, MergeMethod.TOUCHING_LINES);		
		Assert.assertEquals(countsa[2], svgLines.size());
		SVGG g = (SVGG)svgLines.get(0).getParent();
		SVGXTree tree = new SVGXTree(g);
		
		TreeAnalyzer treeAnalyzer = tree.getTreeAnalyzer();
		treeAnalyzer.analyzeBranchesAtLineEnds(svgLines);
		assertHorizontalVerticalLines(treeAnalyzer, countsb, orientation);
		tree.buildTree();
		SVGSVG.wrapAndWriteAsSVG(tree, new File(outfile));
		//tree.debug("Tree");
	}
	
	private void assertHorizontalVerticalLines(TreeAnalyzer treeAnalyzer, int[] counts, LineOrientation treeOrientation ) {
		Assert.assertEquals("vertical", counts[0], treeAnalyzer.getVerticalLineContainer().getLines().size());
		Assert.assertEquals("verticalComplex", counts[1], treeAnalyzer.getVerticalLineContainer().getComplexLines().size());
		Assert.assertEquals("verticalPlus", counts[2], treeAnalyzer.getVerticalLineContainer().getMinusEndedLines().size());
		Assert.assertEquals("verticalMinus", counts[3], treeAnalyzer.getVerticalLineContainer().getPlusEndedLines().size());
		Assert.assertEquals("verticalDouble", counts[4], treeAnalyzer.getVerticalLineContainer().getDoubleEndedLines().size());
		Assert.assertEquals("horizontal", counts[5], treeAnalyzer.getHorizontalLineContainer().getLines().size());
		Assert.assertEquals("horizontalComplex", counts[6], treeAnalyzer.getHorizontalLineContainer().getComplexLines().size());
		Assert.assertEquals("horizontalPlus", counts[7], treeAnalyzer.getHorizontalLineContainer().getMinusEndedLines().size());
		Assert.assertEquals("horizontalMinus", counts[8], treeAnalyzer.getHorizontalLineContainer().getPlusEndedLines().size());
		Assert.assertEquals("horizontalDouble", counts[9], treeAnalyzer.getHorizontalLineContainer().getDoubleEndedLines().size());
		Assert.assertEquals("treeOrientation", treeOrientation, treeAnalyzer.getTreeOrientation());
		Assert.assertNotNull("singleEnd not null", treeAnalyzer.getSingleEndedLines());
		Assert.assertEquals("singleEnd", counts[10], treeAnalyzer.getSingleEndedLines().size());
	}

	// MergeMethod.TOUCHING_LINES
	private SVGXTree makeTree(SVGElement root, double eps, MergeMethod method) {
		SVGPolyline.replacePolyLinesBySplitLines(root);
		List<SVGLine> lines = SVGLine.extractSelfAndDescendantLines(root);
		lines = LineMerger.mergeLines(lines, eps, method);		
		SVGXTree tree = new SVGXTree(root);
		TreeAnalyzer treeAnalyzer = tree.getTreeAnalyzer();
		treeAnalyzer.analyzeBranchesAtLineEnds(tree, lines, eps);
		tree.buildTree();
		return tree;
	}
	
}
