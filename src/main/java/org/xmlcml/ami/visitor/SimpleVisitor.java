package org.xmlcml.ami.visitor;

import java.awt.Container;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.SimpleResultWrapper;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.image.ImageVisitable;
import org.xmlcml.ami.visitable.svg.SVGContainer;
import org.xmlcml.ami.visitable.xml.XMLVisitable;

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
		doVisit(xmlVisitable);
	}

	@Override
	protected void searchContainer(List<SVGContainer> svgContainerList) {
		searchContainer(svgContainerList);
	}

	
	// =======================Called by Visitables===============

	@Override
	protected AbstractSearcher createSearcher() {
		return new SimpleSearcher(this);
	}
	
	public String getDescription() {
		return "SimpleVisitor template for tutorial and development purposes; examples include wordFrquencies and wordLengths";

	}
	
	public Multiset<SimpleResultWrapper> searchXPathPatternAndCollectResults(Container container) {
		Multiset<SimpleResultWrapper> resultSet = HashMultiset.create();
		return resultSet;
	}

	public static void main(String[] args) throws Exception {
		SimpleVisitor visitor = new SimpleVisitor();
		visitor.processArgs(args);
	}
	
}