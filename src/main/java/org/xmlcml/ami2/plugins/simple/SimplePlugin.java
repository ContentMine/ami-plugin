package org.xmlcml.ami2.plugins.simple;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIPlugin;
import org.xmlcml.ami2.plugins.regex.RegexArgProcessor;
import org.xmlcml.files.QuickscrapeNorma;

/** test plugin.
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class SimplePlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(SimplePlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

//	private SimpleArgProcessor argProcessor;
	
	public SimplePlugin(String[] args) {
		super();
		this.argProcessor = new SimpleArgProcessor(args);
	}
	
	public static void main(String[] args) {
		SimpleArgProcessor argProcessor = new SimpleArgProcessor(args);
		argProcessor.runAndOutput();
	}


}
