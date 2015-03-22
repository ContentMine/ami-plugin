package org.xmlcml.ami2.plugins.word;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMIPlugin;

/** test plugin.
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class WordPlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(WordPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

//	private SimpleArgProcessor argProcessor;
	
	public WordPlugin(String[] args) {
		super();
		this.argProcessor = new WordArgProcessor();
		argProcessor.parseArgs(args);

	}
	
	public static void main(String[] args) {
		AMIArgProcessor argProcessor = new WordArgProcessor();
		argProcessor.parseArgsRunAndOutput(args);
	}


}
