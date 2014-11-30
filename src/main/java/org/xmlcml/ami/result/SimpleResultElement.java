package org.xmlcml.ami.result;

import nu.xom.Element;

public class SimpleResultElement extends AbstractResultElement {

	public final static String TAG = "resultElement";
	public SimpleResultElement() {
		this(TAG);
	}

	public SimpleResultElement(String tag) {
		super(tag);
	}

	@Override
	/** returns null.
	 * 
	 */
	protected AbstractResultElement createElement(SimpleResultWrapper simpleResult) {
		return null;
	}

}
