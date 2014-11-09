package org.xmlcml.xhtml2stm.result;

import nu.xom.Element;

public class SimpleResultElement extends AbstractResultElement {

	public SimpleResultElement(String tag) {
		super(tag);
	}

	@Override
	/** returns null.
	 * 
	 */
	protected AbstractResultElement createElement(SimpleResult simpleResult) {
		return null;
	}

}
