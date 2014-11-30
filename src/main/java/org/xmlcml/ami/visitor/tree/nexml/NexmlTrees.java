package org.xmlcml.ami.visitor.tree.nexml;

import nu.xom.Attribute;

import org.apache.log4j.Logger;

public class NexmlTrees extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlTrees.class);
	public final static String TAG = "trees";

	/** constructor.
	 * 
	 */
	public NexmlTrees() {
		super(TAG);
	}

	public void setOtus(String otus) {
		this.addAttribute(new Attribute("otus", otus));
	}
	
}
