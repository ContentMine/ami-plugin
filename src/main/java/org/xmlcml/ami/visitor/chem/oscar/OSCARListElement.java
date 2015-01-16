package org.xmlcml.ami.visitor.chem.oscar;

import org.apache.log4j.Logger;
import org.xmlcml.ami.Type;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;

public class OSCARListElement extends AbstractListElement {
	
	private final static Logger LOG = Logger.getLogger(OSCARListElement.class);
	public final static String TAG = "oscarList";


	public OSCARListElement() {
		super(TAG);
	}
	
	public OSCARListElement(Type type, SimpleResultList resultSet) {
		this();
		this.addResultList(resultSet, type);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResultWrapper resultElement) {
		return new OSCARResultElement(resultElement);
	}
	
}
