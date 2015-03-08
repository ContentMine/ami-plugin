package org.xmlcml.ami.plugin.result;

import org.apache.log4j.Logger;


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
