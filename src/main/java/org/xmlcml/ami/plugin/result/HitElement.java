package org.xmlcml.ami.plugin.result;

import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.AbstractAMIElement;

public class HitElement extends AbstractAMIElement {
	
	private static final Logger LOG = Logger.getLogger(HitElement.class);
	
	public final static String TAG = "hit";
	
	public HitElement() {
		super(TAG);
	}
}
