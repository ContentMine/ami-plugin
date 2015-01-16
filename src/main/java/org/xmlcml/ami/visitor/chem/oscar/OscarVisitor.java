package org.xmlcml.ami.visitor.chem.oscar;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;

public class OscarVisitor extends AbstractVisitor {

	private final static Logger LOG = Logger.getLogger(OscarVisitor.class);
	
	public OscarVisitor() {
		
	}
	
	// ===================Called on Visitables===================
	
	public void visit(HtmlVisitable htmlVisitable) {
		doVisit(htmlVisitable);
	}
	
	// =======================Called by Visitables===============

	public AbstractSearcher createSearcher() {
		AbstractSearcher searcher = new ChemicalTaggerSearcher(this);
		return searcher;
	}

	public static void main(String[] args) throws Exception {
		new OscarVisitor().processArgs(args);
	}
	
	@Override
	protected void usage() {
		System.err.println("OSCAR chemical entity identification: ");
		super.usage();
	}

	@Override
	public String getDescription() {
		return "Extracts chemistry from names.";
	}

}
