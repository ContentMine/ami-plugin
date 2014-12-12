package org.xmlcml.ami.visitor.species;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.xml.XMLVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;

public class SpeciesVisitor extends AbstractVisitor {

	public final static Logger LOG = Logger.getLogger(SpeciesVisitor.class);
	
	public SpeciesVisitor() {
		
	}
	
	// ===================Called on Visitables===================
	
	public void visit(HtmlVisitable htmlVisitable) {
		doVisit(htmlVisitable);
	}
	
	public void visit(XMLVisitable xmlVisitable) {
		LOG.debug("Visitable: "+xmlVisitable.toString());
		doVisit(xmlVisitable);
	}

	// =======================Called by Visitables===============

	public AbstractSearcher createSearcher() {
		AbstractSearcher searcher = new SpeciesSearcher(this);
		return searcher;
	}

	// =======================Called by Visitables===============


	public static void main(String[] args) throws Exception {
		new SpeciesVisitor().processArgs(args);
	}
	
	@Override
	protected void usage() {
		System.err.println("Species: ");
		super.usage();
	}

	@Override
	public String getDescription() {
		return "Extracts species with either genus or binomial names.";
	}

}
