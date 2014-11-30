package org.xmlcml.ami.visitor.tree.nexml;

import nu.xom.Attribute;

import org.apache.log4j.Logger;

public class NexmlEdge extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlEdge.class);
	public final static String TAG = "edge";
	private static final String TARGET = "target";
	private static final String SOURCE = "source";

	/** constructor.
	 * 
	 */
	public NexmlEdge() {
		super(TAG);
	}

	public void setTarget(String target) {
		this.addAttribute(new Attribute(TARGET, target));
	}

	public void setSource(String source) {
		this.addAttribute(new Attribute(SOURCE, source));
	}

	
}
