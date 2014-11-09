package org.xmlcml.xhtml2stm.visitor;

import org.apache.log4j.Logger;

import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.ResultList;
import org.xmlcml.xhtml2stm.result.SimpleResult;

import com.google.common.collect.Multiset.Entry;

public class SimpleListElement extends AbstractListElement {
	
	private final static Logger LOG = Logger.getLogger(SimpleListElement.class);
	public final static String TAG = "simpleList";


	public SimpleListElement() {
		super(TAG);
	}

	public SimpleListElement(ResultList resultList) {
		this();
		for (SimpleResult result : resultList) {
			this.appendChild(new SimpleResultElement(result));
		}
	}


	@Override
	protected AbstractResultElement createElement(SimpleResult simpleResult) {
		return new SimpleResultElement(simpleResult);
	}
	
	
}
