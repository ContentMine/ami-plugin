package org.xmlcml.ami.visitor.plotold;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.sequence.SequenceVisitor;

@Deprecated // moved to SVG
public class PlotVisitorOld extends AbstractVisitor {

	private final static Logger LOG = Logger.getLogger(PlotVisitorOld.class);
			
	// ===================Called on Visitables===================

	
	@Override
	public void visit(SVGVisitable svgVisitable) {
		throw new RuntimeException("obsolete");
	}
	
	//======================================

	public AbstractSearcher createSearcher() {
		throw new RuntimeException("obsolete");
	}

	public static void main(String[] args) throws Exception {
		throw new RuntimeException("obsolete");
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