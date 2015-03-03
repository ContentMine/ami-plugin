package org.xmlcml.ami.result;

import nu.xom.Attribute;
import nu.xom.Element;

public class ResultElement extends Element {

	public static final String TAG = "result";

	public ResultElement() {
		super(TAG);
	}

	public void setValue(String name, String value) {
		Attribute attribute = new Attribute(name, value);
		this.addAttribute(attribute);
	}
	
}
