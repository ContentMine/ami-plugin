package org.xmlcml.ami.plugin.plugins;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.args.DefaultArgProcessor;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.norma.Norma;
import org.xmlcml.norma.NormaArgProcessor;

public abstract class AbstractAMIPlugin {

	
	private static final Logger LOG = Logger.getLogger(AbstractAMIPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private QuickscrapeNorma qsNorma;

	public AbstractAMIPlugin() {
	}

	public AbstractAMIPlugin(QuickscrapeNorma qsNorma) {
		this.qsNorma = qsNorma;
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
