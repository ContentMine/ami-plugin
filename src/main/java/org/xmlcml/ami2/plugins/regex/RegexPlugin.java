package org.xmlcml.ami2.plugins.regex;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIPlugin;
import org.xmlcml.files.QuickscrapeNorma;

/** RegexPlugin
 * 
 * 
 * @author pm286
 *
 */
public class RegexPlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(RegexPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public RegexPlugin(String[] args) {
		super();
		this.argProcessor = new RegexArgProcessor(args);
		
	}

	public static void main(String[] args) {
		RegexArgProcessor argProcessor = new RegexArgProcessor(args);
		argProcessor.runAndOutput();
	}
}
