package org.xmlcml.xhtml2stm.result;

import org.apache.log4j.Logger;

public class ResultsElement extends AbstractXHTML2STMElement {

	private static final Logger LOG = Logger.getLogger(ResultsElement.class);
	public final static String TAG = "results";
	
	/** 
	 * Constructor.
	 */
	public ResultsElement() {
		super(TAG);
	}
}
