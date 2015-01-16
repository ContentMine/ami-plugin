package org.xmlcml.ami.visitor.sequence;

import org.apache.log4j.Logger;
import org.xmlcml.ami.Type;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;

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
