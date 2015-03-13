package org.xmlcml.ami2.plugins;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.args.DefaultArgProcessor;
import org.xmlcml.norma.Norma;

public abstract class AbstractAMIPlugin {

	
	private static final Logger LOG = Logger.getLogger(AbstractAMIPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String ORG_XMLCML_AMI_PLUGIN = "/org/xmlcml/ami2/plugins/";
	
	public AbstractAMIPlugin() {
	}

	protected AMIArgProcessor argProcessor;

	public static void main(String[] args) {
		Norma norma = new Norma();
		norma.run(args);
	}

	public void run(String[] args) {
		argProcessor = new AMIArgProcessor(args);
		argProcessor.runAndOutput();
	}

	public DefaultArgProcessor getArgProcessor() {
		return argProcessor;
	}

}
