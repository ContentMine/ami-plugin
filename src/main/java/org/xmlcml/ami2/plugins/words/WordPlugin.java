package org.xmlcml.ami2.plugins.words;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AbstractAMIPlugin;

/** test plugin.
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class WordPlugin extends AbstractAMIPlugin {

	private static final Logger LOG = Logger.getLogger(WordPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

//	private SimpleArgProcessor argProcessor;
	
	public WordPlugin(String[] args) {
		super();
		this.argProcessor = new WordArgProcessor(args);
	}
	
	public static void main(String[] args) {
		WordArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}


}
