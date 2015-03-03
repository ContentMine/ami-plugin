package org.xmlcml.ami.plugin.regex;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.AbstractAMIPlugin;
import org.xmlcml.files.QuickscrapeNorma;

/** RegexPlugin
 * 
 * 
 * @author pm286
 *
 */
public class RegexPlugin extends AbstractAMIPlugin {

	private static final Logger LOG = Logger.getLogger(RegexPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public RegexPlugin(String[] args) {
		super();
		this.argProcessor = new RegexArgProcessor(args);
	}

}
