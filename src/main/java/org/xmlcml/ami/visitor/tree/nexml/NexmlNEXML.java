package org.xmlcml.ami.visitor.tree.nexml;

import org.apache.log4j.Logger;
import org.xmlcml.pdf2svg.util.PDF2SVGUtil;

public class NexmlNEXML extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlNEXML.class);
	public final static String TAG = "nexml";

	/** constructor.
	 * 
	 */
	public NexmlNEXML() {
		super(TAG);
		this.addNamespaceDeclaration(NEX, NEXML_NS);
		this.addNamespaceDeclaration(XSI, XSI_NS);
		this.addNamespaceDeclaration(PDF2SVGUtil.SVGX_PREFIX, PDF2SVGUtil.SVGX_NS);
	}

	
}
