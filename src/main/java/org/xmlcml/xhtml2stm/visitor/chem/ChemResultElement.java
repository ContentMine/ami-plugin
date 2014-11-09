package org.xmlcml.xhtml2stm.visitor.chem;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResult;

import com.google.common.collect.Multiset.Entry;

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
	
	public ChemResultElement(SimpleResult simpleResult) {
		this();
		addEntry(simpleResult);
	}

	protected ChemResultElement createElement(SimpleResult simpleResult) {
		return new ChemResultElement(simpleResult);
	}

}