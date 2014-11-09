package org.xmlcml.xhtml2stm.visitor;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResult;

import com.google.common.collect.Multiset.Entry;


public class SimpleResultElement extends AbstractResultElement {

	private final static Logger LOG = Logger.getLogger(SimpleResultElement.class);
	public final static String TAG = "simple";
	
	public SimpleResultElement() {
		super(TAG);
		
	}

	public SimpleResultElement(String s) {
		this();
		this.setContent(s);
	}

	public SimpleResultElement(SimpleResult simpleResult) {
		this();
		this.setSimpleResult(simpleResult);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResult simpleResult) {
		throw new RuntimeException("Please implement createElement");
	}


}
