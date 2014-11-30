package org.xmlcml.ami.visitor.sequence;

import org.apache.log4j.Logger;
import org.xmlcml.ami.Type;
import org.xmlcml.ami.visitable.SourceElement;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.xml.XMLContainer;
import org.xmlcml.ami.visitable.xml.XMLVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.species.SpeciesSearcher;

public class SequenceVisitor extends AbstractVisitor {

	private final static Logger LOG = Logger.getLogger(SequenceVisitor.class);
	
	public enum SequenceType implements Type {
		MIXED,
		CARBOHYDRATE,
		DNA,
		PROTEIN,
		RNA,
	}

	public SequenceVisitor() {
		
	}
	
	// ===================Called on Visitables===================
	
	public void visit(HtmlVisitable htmlVisitable) {
		doVisit(htmlVisitable);
	}
	
	public void visit(XMLVisitable xmlVisitable) {
		doVisit(xmlVisitable);
	}

	// =======================Called by Visitables===============

	public AbstractSearcher createSearcher() {
		AbstractSearcher searcher = new SequenceSearcher(this);
		return searcher;
	}

	public static void main(String[] args) throws Exception {
		new SequenceVisitor().processArgs(args);
	}
	
	@Override
	protected void usage() {
		System.err.println("Sequence: ");
		super.usage();
	}

	@Override
	public String getDescription() {
		return "Extracts sequences.";
	}

}
