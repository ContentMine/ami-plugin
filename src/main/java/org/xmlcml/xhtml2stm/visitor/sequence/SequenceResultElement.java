package org.xmlcml.xhtml2stm.visitor.sequence;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResultWrapper;

import com.google.common.collect.Multiset.Entry;

public class SequenceResultElement extends AbstractResultElement {

	private final static Logger LOG = Logger.getLogger(SequenceResultElement.class);
	public final static String TAG = "sequence";

	/** 
	 * Constructor.
	 */
	public SequenceResultElement() {
		super(TAG);
	}

	public SequenceResultElement(String value) {
		this();
		appendChild(value);
	}
	
	public SequenceResultElement(SimpleResultWrapper simpleResult) {
		this();
		addSimpleResultAsXML(simpleResult);
	}

	protected SequenceResultElement createElement(SimpleResultWrapper simpleResult) {
		return new SequenceResultElement(simpleResult);
	}

}