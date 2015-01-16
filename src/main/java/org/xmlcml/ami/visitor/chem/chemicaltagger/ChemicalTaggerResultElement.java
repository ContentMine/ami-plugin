package org.xmlcml.ami.visitor.chem.chemicaltagger;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultWrapper;

public class ChemicalTaggerResultElement extends AbstractResultElement {

	private final static Logger LOG = Logger.getLogger(ChemicalTaggerResultElement.class);
	public final static String TAG = "OSCAR";

	/** 
	 * Constructor.
	 */
	public ChemicalTaggerResultElement() {
		super(TAG);
	}

	public ChemicalTaggerResultElement(String value) {
		this();
		appendChild(value);
	}
	
	public ChemicalTaggerResultElement(SimpleResultWrapper simpleResult) {
		this();
		addSimpleResultAsXML(simpleResult);
	}

	protected ChemicalTaggerResultElement createElement(SimpleResultWrapper simpleResult) {
		return new ChemicalTaggerResultElement(simpleResult);
	}

}