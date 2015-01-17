package org.xmlcml.ami.visitor.tree;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.VisitableContainer;
import org.xmlcml.ami.visitable.image.ImageContainer;
import org.xmlcml.ami.visitable.image.ImageVisitable;
import org.xmlcml.ami.visitable.pdf.PDFVisitable;
import org.xmlcml.ami.visitable.svg.SVGContainer;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitable.table.TableVisitable;
import org.xmlcml.ami.visitable.xml.XMLVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.diagrams.phylo.PhyloTreeAnalyzer;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.linestuff.LineMerger.MergeMethod;
import org.xmlcml.image.pixel.PixelComparator.ComparatorType;

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
		if (svgVisitable != null) {
			for (VisitableContainer svgContainer : svgVisitable.getSVGContainerList()) {
				SVGXTree tree = makeTree((SVGContainer)svgContainer);
				resultsElement.appendChild(tree.createNEXML());
				//resultsElement.appendChild(tree);
			}
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
		LOG.trace("writing to output File: "+outputFile);
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