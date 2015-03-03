package org.xmlcml.ami.plugin.result;

import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.AbstractAMIElement;

public class HitsElement extends AbstractAMIElement {
	
	private static final Logger LOG = Logger.getLogger(HitsElement.class);
	
	public final static String TAG = "hits";
	
	public HitsElement() {
		super(TAG);
	}


}
