package org.xmlcml.xhtml2stm.visitor.sequence;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResult;

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
	
	public SequenceResultElement(SimpleResult simpleResult) {
		this();
		addEntry(simpleResult);
	}

	protected SequenceResultElement createElement(SimpleResult simpleResult) {
		return new SequenceResultElement(simpleResult);
	}

}