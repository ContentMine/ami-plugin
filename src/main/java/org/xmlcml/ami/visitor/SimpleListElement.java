package org.xmlcml.ami.visitor;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;

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
