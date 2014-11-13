package org.xmlcml.xhtml2stm.visitor.chem;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.html.HtmlSub;
import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.SimpleResultList;
import org.xmlcml.xhtml2stm.visitable.html.HtmlContainer;
import org.xmlcml.xhtml2stm.visitable.svg.SVGContainer;
import org.xmlcml.xhtml2stm.visitable.svg.SVGVisitable;
import org.xmlcml.xhtml2stm.visitor.AbstractSearcher;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;
import org.xmlcml.xhtml2stm.visitor.SimpleListElement;

import com.google.common.util.concurrent.UncheckedTimeoutException;

public class ChemSearcher extends AbstractSearcher {
	
	private final static Logger LOG = Logger.getLogger(ChemSearcher.class);
	
	private static final int TIMEOUT = 350000000;
	private static final String CHEM_SUB_DIRECTORY = "/"; // no subdirectory at present

	File outputDirectory;
	
	protected ChemSearcher(AbstractVisitor visitor) {
		super(visitor);
	}

	@Override
	protected void search(HtmlContainer htmlContainer) {
		searchForSubscripts(htmlContainer);
	}
	
	@Override
	protected void search(SVGContainer svgContainer) {
		LOG.trace("ChemVisitor: now visiting an SVGVisitable");
		outputDirectory = new File(visitor.getOrCreateVisitorOutput().getOutputDirectoryFile(), CHEM_SUB_DIRECTORY);
		outputDirectory.mkdir();
		try {
			createAndSaveCML(svgContainer);
		} catch (UncheckedTimeoutException e) {
			LOG.warn(e.getMessage());
		}
		throw new RuntimeException("chem search NYI");
	}
	
	private void createAndSaveCML(SVGContainer svgContainer) {
		LOG.info("Working with svgContainer: "+ svgContainer.getName());
		
		MoleculeCreator cmlCreator = new MoleculeCreator(svgContainer, TIMEOUT);
		cmlCreator.getReactionsAndMolecules();
		try {
			cmlCreator.createAnnotatedVersionOfOutput(outputDirectory);
		} catch (Throwable t) {
			
		}
		try {
			cmlCreator.createAnnotatedVersionOfInput(outputDirectory);
		} catch (Throwable t) {
			
		}
	}
	
	/** 
	 * This is just a test at present, especially as spaces are not correct yet.
	 * <p>
	 * HCO <sub>2</sub> H
	 */
	private void searchForSubscripts(HtmlContainer htmlContainer) {
		List<HtmlSub> subList = HtmlSub.extractSelfAndDescendantLines(htmlContainer.getHtmlElement());
		LOG.debug("subscripts: "+subList.size());
	}
	
	@Override
	protected AbstractListElement createListElement(SimpleResultList resultList) {
		AbstractListElement listElement = new ChemListElement(resultList);
		return listElement;
	}


}
