package org.xmlcml.ami.visitor;

import org.apache.log4j.Logger;

/** manages document section traversal and extraction by XPath.
 * 
 * @author pm286
 *
 */
public class XPathProcessor {

	private static final Logger LOG = Logger.getLogger(XPathProcessor.class);
	
	private String xpath;

	public XPathProcessor() {
		
	}
	
	public XPathProcessor(String xpath) {
		this.xpath = xpath;
	}

	public String getXPath() {
		return xpath;
	}
	
}
