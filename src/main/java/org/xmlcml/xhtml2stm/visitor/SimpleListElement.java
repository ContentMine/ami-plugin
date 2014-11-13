package org.xmlcml.xhtml2stm.visitor;

import org.apache.log4j.Logger;

import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResultList;
import org.xmlcml.xhtml2stm.result.SimpleResultWrapper;

import com.google.common.collect.Multiset.Entry;

public class SimpleListElement extends AbstractListElement {
	
	private final static Logger LOG = Logger.getLogger(SimpleListElement.class);
	public final static String TAG = "simpleList";


	public SimpleListElement() {
		super(TAG);
	}

	public SimpleListElement(SimpleResultList resultList) {
		this();
		for (SimpleResultWrapper result : resultList) {
			this.appendChild(new VisitorSimpleResultElement(result));
		}
	}


	@Override
	protected AbstractResultElement createElement(SimpleResultWrapper simpleResult) {
		return new VisitorSimpleResultElement(simpleResult);
	}
	
	
}
