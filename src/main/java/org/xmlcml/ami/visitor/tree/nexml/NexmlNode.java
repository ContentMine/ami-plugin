package org.xmlcml.ami.visitor.tree.nexml;

import nu.xom.Attribute;

import org.apache.log4j.Logger;

public class NexmlNode extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlNode.class);
	public final static String TAG = "node";
	private static final String ROOT = "root";
	private static final String OTU = "otu";

	/** constructor.
	 * 
	 */
	public NexmlNode() {
		super(TAG);
	}

	public void setRoot(String bool) {
		this.addAttribute(new Attribute(ROOT, bool));
	}

	public void setOtu(String otuId) {
		this.addAttribute(new Attribute(OTU, otuId));
	}

	
}
