package org.xmlcml.xhtml2stm.result;

import org.apache.log4j.Logger;

public class ResultsListElement extends AbstractXHTML2STMElement {

	private static final Logger LOG = Logger.getLogger(ResultsListElement.class);
	public final static String TAG = "resultsList";
	
	/** 
	 * Constructor.
	 */
	public ResultsListElement() {
		super(TAG);
	}
}
