package org.xmlcml.xhtml2stm.visitor.plot;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.visitable.svg.SVGVisitable;
import org.xmlcml.xhtml2stm.visitor.AbstractSearcher;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;
import org.xmlcml.xhtml2stm.visitor.sequence.SequenceVisitor;

public class PlotVisitor extends AbstractVisitor {

	private final static Logger LOG = Logger.getLogger(PlotVisitor.class);
			
	// ===================Called on Visitables===================

	
	@Override
	public void visit(SVGVisitable svgVisitable) {
		doVisit(svgVisitable);
	}
	
	//======================================

	public AbstractSearcher createSearcher() {
		PlotSearcher searcher = new PlotSearcher(this);
		return searcher;
	}

	public static void main(String[] args) throws Exception {
		new SequenceVisitor().processArgs(args);
	}
	
	@Override
	protected void usage() {
		System.err.println("Plot: ");
		super.usage();
	}

	@Override
	public String getDescription() {
		return "Extracts plots.";
	}
	
}