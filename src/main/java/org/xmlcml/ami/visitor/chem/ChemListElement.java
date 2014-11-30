package org.xmlcml.ami.visitor.chem;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.species.SpeciesSearcher.SpeciesType;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;

import com.google.common.collect.Multiset.Entry;

public class ChemListElement extends AbstractListElement {
	
	private final static Logger LOG = Logger.getLogger(ChemListElement.class);
	public final static String TAG = "chemList";


	public ChemListElement() {
		super(TAG);
	}
	
	public ChemListElement(SpeciesType type, SimpleResultList resultList) {
		this();
		this.addResultList(resultList, type);
	}

	public ChemListElement(SimpleResultList resultList) {
		this();
		this.addResultList(resultList, null);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResultWrapper simpleResult) {
		return new ChemResultElement(simpleResult);
	}
	
}
