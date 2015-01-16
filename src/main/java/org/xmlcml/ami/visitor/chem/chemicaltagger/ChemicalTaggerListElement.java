package org.xmlcml.ami.visitor.chem.chemicaltagger;

import org.apache.log4j.Logger;
import org.xmlcml.ami.Type;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;

public class ChemicalTaggerListElement extends AbstractListElement {
	
	private final static Logger LOG = Logger.getLogger(ChemicalTaggerListElement.class);
	public final static String TAG = "oscarList";


	public ChemicalTaggerListElement() {
		super(TAG);
	}
	
	public ChemicalTaggerListElement(Type type, SimpleResultList resultSet) {
		this();
		this.addResultList(resultSet, type);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResultWrapper resultElement) {
		return new ChemicalTaggerResultElement(resultElement);
	}
	
}
