package org.xmlcml.ami.result;

import org.apache.log4j.Logger;
import org.xmlcml.ami.AbstractAMIElement;

public class ResultsListElement extends AbstractAMIElement {

	private static final Logger LOG = Logger.getLogger(ResultsListElement.class);
	public final static String TAG = "resultsList";
	
	/** 
	 * Constructor.
	 */
	public ResultsListElement() {
		super(TAG);
	}
}
