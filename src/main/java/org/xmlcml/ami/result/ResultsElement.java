package org.xmlcml.ami.result;

import org.apache.log4j.Logger;
import org.xmlcml.ami.AbstractAMIElement;

public class ResultsElement extends AbstractAMIElement {
	
	private static final Logger LOG = Logger.getLogger(ResultsElement.class);
	
	public final static String TAG = "resultsX";
	
	public ResultsElement() {
		super(TAG);
	}


}
