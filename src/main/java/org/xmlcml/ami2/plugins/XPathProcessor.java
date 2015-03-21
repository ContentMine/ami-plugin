package org.xmlcml.ami2.plugins;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class XPathProcessor {

	
	private static final Logger LOG = Logger.getLogger(XPathProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private String xpath;

	public XPathProcessor(String xpath) {
		this.xpath = xpath;
	}

}
