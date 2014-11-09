package org.xmlcml.xhtml2stm.visitor.species;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResult;

import com.google.common.collect.Multiset.Entry;


public class SpeciesResultElement extends AbstractResultElement {

	private final static Logger LOG = Logger.getLogger(SpeciesResultElement.class);
	public final static String TAG = "species";

	/** constructor.
	 * 
	 */
	public SpeciesResultElement() {
		super(TAG);
	}

	public SpeciesResultElement(String value) {
		this();
		this.appendChild(value);
	}

	public SpeciesResultElement(SimpleResult simpleResult) {
		this();
		addEntry(simpleResult);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResult simpleResult) {
		return new SpeciesResultElement(simpleResult);
	}

}
