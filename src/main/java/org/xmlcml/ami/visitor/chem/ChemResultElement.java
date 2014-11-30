package org.xmlcml.ami.visitor.chem;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultWrapper;

public class ChemResultElement extends AbstractResultElement {

	private final static Logger LOG = Logger.getLogger(ChemResultElement.class);
	public final static String TAG = "chem";

	/** 
	 * Constructor.
	 */
	public ChemResultElement() {
		super(TAG);
	}

	public ChemResultElement(String value) {
		this();
		appendChild(value);
	}
	
	public ChemResultElement(SimpleResultWrapper simpleResult) {
		this();
		addSimpleResultAsXML(simpleResult);
	}

	protected ChemResultElement createElement(SimpleResultWrapper simpleResult) {
		return new ChemResultElement(simpleResult);
	}

}