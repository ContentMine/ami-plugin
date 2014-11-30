package org.xmlcml.ami.visitor.tree.nexml;

import nu.xom.Attribute;

import org.apache.log4j.Logger;

public class NexmlTree extends NexmlElement {

	private static final String TYPE = "type";
	private final static Logger LOG = Logger.getLogger(NexmlTree.class);
	public final static String TAG = "tree";

	/** constructor.
	 * 
	 */
	public NexmlTree() {
		super(TAG);
	}

	public void setType(String type) {
		this.addAttribute(new Attribute(XSI+":"+TYPE, XSI_NS, type));
	}

	
}
