package org.xmlcml.ami.visitor.chem.oscar;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultWrapper;

public class OSCARResultElement extends AbstractResultElement {

	private final static Logger LOG = Logger.getLogger(OSCARResultElement.class);
	public final static String TAG = "OSCAR";

	/** 
	 * Constructor.
	 */
	public OSCARResultElement() {
		super(TAG);
	}

	public OSCARResultElement(String value) {
		this();
		appendChild(value);
	}
	
	public OSCARResultElement(SimpleResultWrapper simpleResult) {
		this();
		addSimpleResultAsXML(simpleResult);
	}

	protected OSCARResultElement createElement(SimpleResultWrapper simpleResult) {
		return new OSCARResultElement(simpleResult);
	}

}