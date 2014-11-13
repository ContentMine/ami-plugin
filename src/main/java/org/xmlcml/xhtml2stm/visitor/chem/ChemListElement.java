package org.xmlcml.xhtml2stm.visitor.chem;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResultList;
import org.xmlcml.xhtml2stm.result.SimpleResultWrapper;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;
import org.xmlcml.xhtml2stm.visitor.species.SpeciesSearcher.SpeciesType;

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
