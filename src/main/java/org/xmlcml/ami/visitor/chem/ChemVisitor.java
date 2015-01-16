package org.xmlcml.ami.visitor.chem;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLReaction;

public class ChemVisitor extends AbstractVisitor {

	private final static Logger LOG = Logger.getLogger(ChemVisitor.class);
	
	private List<CMLReaction> reactions = new ArrayList<CMLReaction>();
	private List<CMLMolecule> molecules = new ArrayList<CMLMolecule>();
	
	public List<CMLMolecule> getMolecules() {
		if (molecules.size() > 0) {
			return molecules;
		}
		List<CMLMolecule> molecules = new ArrayList<CMLMolecule>();
		for (CMLReaction r : reactions) {
			if (r.getReactantList() != null) {
				molecules.addAll(r.getReactantList().getMolecules());
			}
			if (r.getProductList() != null) {
				molecules.addAll(r.getProductList().getMolecules());
			}
		}
		return molecules;
	}
	
	public List<CMLReaction> getReactions() {
		return reactions;
	}
	
	// ===================Called on Visitables===================

	@Override
	public void visit(HtmlVisitable htmlVisitable) {
		doVisit(htmlVisitable);
	}
	
	@Override
	public void visit(SVGVisitable svgVisitable) {
		doVisit(svgVisitable);
	}

	// =======================Called by Visitables===============

	@Override 
	protected AbstractSearcher createSearcher() {
		return new ChemSearcher(this);
	}
	
	@Override
	public String getDescription() {
		return "Extracts chemical reactions.";
	}

	// =======================Called on Visitables===============
	
	public static void main(String[] args) throws Exception {
		new ChemVisitor().processArgs(args);
	}
	
	@Override
	protected void usage() {
		System.err.println("Chem: ");
		super.usage();
	}

}