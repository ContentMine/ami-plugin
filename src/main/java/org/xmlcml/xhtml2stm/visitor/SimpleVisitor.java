package org.xmlcml.xhtml2stm.visitor;

import java.awt.Container;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.SimpleResult;
import org.xmlcml.xhtml2stm.visitable.VisitableContainer;
import org.xmlcml.xhtml2stm.visitable.html.HtmlContainer;
import org.xmlcml.xhtml2stm.visitable.html.HtmlVisitable;
import org.xmlcml.xhtml2stm.visitable.image.ImageVisitable;
import org.xmlcml.xhtml2stm.visitable.pdf.PDFVisitable;
import org.xmlcml.xhtml2stm.visitable.svg.SVGContainer;
import org.xmlcml.xhtml2stm.visitable.svg.SVGVisitable;
import org.xmlcml.xhtml2stm.visitable.table.TableVisitable;
import org.xmlcml.xhtml2stm.visitable.xml.XMLVisitable;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** 
 * A simple visitor which records basic information.
 * <p>
 * Mainly as a tutorial and also as testing.
 * 
 * @author pm286
 */
public class SimpleVisitor extends AbstractVisitor {
	
	private final static Logger LOG = Logger.getLogger(SimpleVisitor.class);

	@Override
	public void visit(HtmlVisitable htmlVisitable) {
		doVisit(htmlVisitable);
	}

	@Override
	public void visit(ImageVisitable imageVisitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(XMLVisitable xmlVisitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(PDFVisitable pdfVisitable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SVGVisitable svgVisitable) {
	}
	
	@Override
	protected void searchContainer(List<SVGContainer> svgContainerList) {
		searchContainer(svgContainerList);
	}


	@Override
	public void visit(TableVisitable tableVisitable) {
		LOG.error("We don't have TableVisitable yet");
	}
	
	// =======================Called by Visitables===============

	@Override
	protected AbstractSearcher createSearcher() {
		return new SimpleSearcher(this);
	}
	
	public String getDescription() {
		return "SimpleVisitor template for tutorial and development purposes; does nothing";

	}
	
	public Multiset<SimpleResult> searchXPathPatternAndCollectResults(Container container) {
		Multiset<SimpleResult> resultSet = HashMultiset.create();
		return resultSet;
	}

}