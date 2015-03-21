package org.xmlcml.ami2.plugins;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.args.DefaultArgProcessor;

public class AMIPlugin {

	
	private static final Logger LOG = Logger.getLogger(AMIPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String ORG_XMLCML_AMI_PLUGIN = "/org/xmlcml/ami2/plugins/";
	public static final String ORG_XMLCML_AMI_CLASSNAME = "org.xmlcml.ami2.plugins";

	static Map<String, String> argProcessorNameByName = null;
	static {
		argProcessorNameByName = new HashMap<String, String>();
		putClass("regex");
		putClass("sequence");
		putClass("simple");
		putClass("species");
		putClass("word");
	}

	private static void putClass(String plugin) {
		argProcessorNameByName.put(plugin, ORG_XMLCML_AMI_CLASSNAME+"."+plugin+".RegexArgProcessor");
	}
	
	public AMIPlugin() {
	}

	protected AMIArgProcessor argProcessor;

	public static void main(String[] args) {
		new AMIPlugin().run(args);
	}

	public void run(String[] args) {
		if (args.length > 0) {
			String plugin = args[0];
			String argProcessorName = argProcessorNameByName.get(plugin);
			if (argProcessorName == null) {
				throw new RuntimeException("Cannot find class for plugin: "+plugin);
			}
			LOG.debug("argProcessor: "+argProcessorName);
			Class argProcessorClass = null;
			try {
				argProcessorClass = Class.forName(argProcessorName);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Cannot create class: "+argProcessorName, e);
			}
			DefaultArgProcessor  argProcessor = null;
			try {
				argProcessor = (DefaultArgProcessor) argProcessorClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Cannot instantiate class: "+argProcessorName, e);
			}
			LOG.debug(argProcessor);
			argProcessor.printHelp(null, null);;
		}
//		argProcessor = new AMIArgProcessor();
//		argProcessor.parseArgs(args);
//		argProcessor.runAndOutput();
	}

	public DefaultArgProcessor getArgProcessor() {
		return argProcessor;
	}
}
