package org.xmlcml.ami2.plugins.species;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.files.ResultElement;

public class SpeciesResultElement extends ResultElement {

	private static final Logger LOG = Logger.getLogger(SpeciesResultElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String COUNT_ATT  = "count";
	public static final String LENGTH_ATT = "length";
	public static final String WORD_ATT   = "word";

	public SpeciesResultElement() {
		super();
	}
	
//	public SpeciesResultElement(ResultElement resultElement) {
//		XMLUtil.copyAttributes(resultElement, this);
//	}
	
	public SpeciesResultElement(String title) {
		super(title);
	}
	
	
	
	
}
