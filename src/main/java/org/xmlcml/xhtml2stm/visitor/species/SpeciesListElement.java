package org.xmlcml.xhtml2stm.visitor.species;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.ResultList;
import org.xmlcml.xhtml2stm.result.SimpleResult;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;
import org.xmlcml.xhtml2stm.visitor.species.SpeciesSearcher.SpeciesType;

import com.google.common.collect.Multiset.Entry;

public class SpeciesListElement extends AbstractListElement {
	
	private final static Logger LOG = Logger.getLogger(SpeciesListElement.class);
	public final static String TAG = "speciesList";


	public SpeciesListElement() {
		super(TAG);
	}
	
	public SpeciesListElement(SpeciesType type, ResultList resultList) {
		this();
		this.addResultList(resultList, type);
	}

	public void resolveAbbreviations() {
		
	}

	@Override
	protected AbstractResultElement createElement(SimpleResult simpleResult) {
		return new SpeciesResultElement(simpleResult);
	}
	
}
