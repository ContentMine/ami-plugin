package org.xmlcml.xhtml2stm.visitor.sequence;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.Type;
import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResultList;
import org.xmlcml.xhtml2stm.result.SimpleResultWrapper;

import com.google.common.collect.Multiset.Entry;

public class SequenceListElement extends AbstractListElement {
	
	private final static Logger LOG = Logger.getLogger(SequenceListElement.class);
	public final static String TAG = "sequenceList";


	public SequenceListElement() {
		super(TAG);
	}
	
	public SequenceListElement(Type type, SimpleResultList resultSet) {
		this();
		this.addResultList(resultSet, type);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResultWrapper resultElement) {
		return new SequenceResultElement(resultElement);
	}
	
}
