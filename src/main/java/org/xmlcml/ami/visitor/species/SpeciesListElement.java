package org.xmlcml.ami.visitor.species;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;
import org.xmlcml.ami.visitor.species.SpeciesSearcher.SpeciesType;

public class SpeciesListElement extends AbstractListElement {
	
	private final static Logger LOG = Logger.getLogger(SpeciesListElement.class);
	public final static String TAG = "speciesList";


	public SpeciesListElement() {
		super(TAG);
	}
	
	public SpeciesListElement(SpeciesType type, SimpleResultList resultList) {
		this();
		this.addResultList(resultList, type);
	}

	public void resolveAbbreviations() {
		
	}

	@Override
	protected AbstractResultElement createElement(SimpleResultWrapper simpleResult) {
		return new SpeciesResultElement(simpleResult);
	}
	
}
