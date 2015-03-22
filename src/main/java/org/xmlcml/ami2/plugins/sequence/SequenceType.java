package org.xmlcml.ami2.plugins.sequence;

import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SequenceType {
	
	private static final Logger LOG = Logger.getLogger(SequenceType.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private String type;
	private Pattern pattern;

	public SequenceType(String type, String patternString) {
		this.type = type;
		try {
			this.pattern = Pattern.compile(patternString);
		} catch (Exception e) {
			LOG.debug("Cannot parse regex: "+patternString+"; "+e);
			throw new RuntimeException("BAD REGEX: "+patternString, e);
		}
	}

	public Pattern getPattern() {
		return pattern;
	}

	public String getType() {
		return type;
	}

	
}
