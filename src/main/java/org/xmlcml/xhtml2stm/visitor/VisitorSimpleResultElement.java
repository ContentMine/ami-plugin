package org.xmlcml.xhtml2stm.visitor;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResultWrapper;

import com.google.common.collect.Multiset.Entry;


public class VisitorSimpleResultElement extends AbstractResultElement {

	private final static Logger LOG = Logger.getLogger(VisitorSimpleResultElement.class);
	public final static String TAG = "simple";
	
	public VisitorSimpleResultElement() {
		super(TAG);
		
	}

	public VisitorSimpleResultElement(String s) {
		this();
		this.setContent(s);
	}

	public VisitorSimpleResultElement(SimpleResultWrapper simpleResult) {
		this();
		this.setSimpleResult(simpleResult);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResultWrapper simpleResult) {
		throw new RuntimeException("Please implement createElement");
	}


}
