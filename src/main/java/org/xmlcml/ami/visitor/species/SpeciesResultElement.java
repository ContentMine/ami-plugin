package org.xmlcml.ami.visitor.species;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultWrapper;


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

	public SpeciesResultElement(SimpleResultWrapper simpleResult) {
		this();
		addSimpleResultAsXML(simpleResult);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResultWrapper simpleResult) {
		return new SpeciesResultElement(simpleResult);
	}

}
