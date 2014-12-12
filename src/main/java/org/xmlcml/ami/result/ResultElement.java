package org.xmlcml.ami.result;

import org.apache.log4j.Logger;
import org.xmlcml.ami.AbstractAMIElement;

public class ResultElement extends AbstractAMIElement {
	
	private static final Logger LOG = Logger.getLogger(ResultElement.class);
	
	public final static String TAG = "result";
	
	public ResultElement() {
		super(TAG);
	}


}
