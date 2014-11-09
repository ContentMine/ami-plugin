package org.xmlcml.xhtml2stm.visitor.tree;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.log4j.Logger;
import org.xmlcml.diagrams.phylo.PhyloTreeAnalyzer;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.image.pixel.PixelComparator.ComparatorType;
import org.xmlcml.svg2xml.paths.LineMerger.MergeMethod;
import org.xmlcml.xhtml2stm.visitable.VisitableContainer;
import org.xmlcml.xhtml2stm.visitable.image.ImageContainer;
import org.xmlcml.xhtml2stm.visitable.image.ImageVisitable;
import org.xmlcml.xhtml2stm.visitable.pdf.PDFVisitable;
import org.xmlcml.xhtml2stm.visitable.svg.SVGContainer;
import org.xmlcml.xhtml2stm.visitable.svg.SVGVisitable;
import org.xmlcml.xhtml2stm.visitable.table.TableVisitable;
import org.xmlcml.xhtml2stm.visitable.xml.XMLVisitable;
import org.xmlcml.xhtml2stm.visitor.AbstractSearcher;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;

public class TreeVisitor extends AbstractVisitor {

	// ===================Called on Visitables===================
	
	private static final Logger LOG = Logger.getLogger(TreeVisitor.class);

	private ComparatorType rootPosition = ComparatorType.LEFT; // default
	
	@Override
	public void visit(ImageVisitable imageVisitable) {
		ensureResultsElement();
		for (VisitableContainer imageContainer : imageVisitable.getImageContainerList()) {
			ensureResultsElement();
			SVGXTree tree = makeTree((ImageContainer)imageContainer);
			resultsElement.appendChild(tree.createNEXML());
			//resultsElement.appendChild(tree);
		}
	}
	
	@Override
	public void visit(PDFVisitable pdfVisitable) {
		notYetImplemented(pdfVisitable);
	}
	
	@Override
	public void visit(SVGVisitable svgVisitable) {
		ensureResultsElement();
		for (VisitableContainer svgContainer : svgVisitable.getSVGContainerList()) {
			SVGXTree tree = makeTree((SVGContainer)svgContainer);
			resultsElement.appendChild(tree.createNEXML());
			//resultsElement.appendChild(tree);
		}
	}
	
	private SVGXTree makeTree(SVGContainer svgContainer) {
		SVGElement svg = (SVGElement)svgContainer.getElement();
		SVGXTree tree = SVGXTree.makeTree(svg, 1.0, MergeMethod.TOUCHING_LINES);
		return tree;
	}

	private SVGXTree makeTree(ImageContainer imageContainer) {
		BufferedImage image = imageContainer.getImage();
		SVGXTree tree = new SVGXTree((SVGElement)null);
		PhyloTreeAnalyzer treeAnalyzer = new PhyloTreeAnalyzer();
		treeAnalyzer.setSelectedIslandIndex(0);
		treeAnalyzer.setComputeLengths(true);
		File inputFile = imageContainer.getFile();
		treeAnalyzer.setInputFile(inputFile);
		File outputFile = new File(new File("target/"), inputFile.getName()+".nwk");
		LOG.debug("writing to output File: "+outputFile);
		treeAnalyzer.setNewickFile(outputFile);
		treeAnalyzer.setImage(image);
		treeAnalyzer.processImageIntoGraphsAndTrees();
		return tree;
	}

	@Override
	public void visit(TableVisitable tableVisitable) {
		notApplicable(tableVisitable);
	}

	@Override
	public void visit(XMLVisitable xmlVisitable) {
		notYetImplemented(xmlVisitable);
	}

	@Override
	public String getDescription() {
		return "Extracts trees (e.g. phylogenetic).";
	}

	// =======================Called on Visitables===============

	@Override
	protected AbstractSearcher createSearcher() {
		return new TreeSearcher(this);
	}
	public static void main(String[] args) throws Exception {
		new TreeVisitor().processArgs(args);
	}
	
	@Override
	protected void usage() {
		System.err.println("Tree: ");
		super.usage();
	}

 }