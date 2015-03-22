package org.xmlcml.ami2.plugins.sequence;

import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.DefaultSearcher;

public class SequenceSearcher extends DefaultSearcher {

	
	public static final Logger LOG = Logger.getLogger(SequenceSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private SequenceType sequenceType;


	public SequenceSearcher(AMIArgProcessor argProcessor, SequenceType sequenceType, Pattern pattern) {
		super(argProcessor, pattern);
		this.sequenceType = sequenceType;

	}
	
	public SequenceType getSequenceType() {
		return sequenceType;
	}

	public String getType() {
		return sequenceType.getType();
	}

}
