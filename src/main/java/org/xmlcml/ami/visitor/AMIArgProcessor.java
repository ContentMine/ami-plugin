package org.xmlcml.ami.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.args.DefaultArgProcessor;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class AMIArgProcessor extends DefaultArgProcessor{
	
	public static final Logger LOG = Logger.getLogger(AMIArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String HELP_NORMA = "Norma help";
	
//	public final static ArgumentOption CONTEXT_OPTION = new ArgumentOption(
//			AMIArgProcessor.class,
//			"-c",
//			"--context",
//			"characterCount",
//			"\n"
//			+ "CONTEXT:\n"
//			+ "The text or other content immediately surrounding the located word/phrase/object. By default this is either\n"
//			+ "+- 100 characters (Publisher limit) or to the end of the object (paragraph). Two integers give the oreceeding and \n"
//			+ "following text. if one integer is given that is used for pre- and post- counts; \n"
//			+ "",
//			Integer.class,
//			new Integer(100),
//			1, 2,
//			"processContext"
//			);
//		
//	public final static List<ArgumentOption> AMI_OPTION_LIST = Arrays.asList(
//			new ArgumentOption[] {
//					CONTEXT_OPTION
//			});
	
	private static String RESOURCE_NAME_TOP = "/org/xmlcml/ami";
	private static String ARGS_RESOURCE = RESOURCE_NAME_TOP+"/"+"args.xml";
	
	Integer[] contextCount = new Integer[] {100, 100};
	
	public AMIArgProcessor() {
		super();
		this.readArgumentOptions(ARGS_RESOURCE);
        for (ArgumentOption argumentOption : argumentOptionList) {
			LOG.debug(argumentOption.getHelp());
		}
	}

	public AMIArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	public void processContext(ArgumentOption argOption, ArgIterator argIterator) {
		List<String> inputs = argIterator.createTokenListUpToNextMinus();
		if (inputs.size() == 0) {
//			LOG.debug(CONTEXT_OPTION.getHelp());
		} else {
			List<Integer> contexts = argOption.processArgs(inputs).getIntegerValues();
			if (contexts.size() == 2) {
				contextCount[0] = contexts.get(0);
				contextCount[1] = contexts.get(1);
			} else if (contexts.size() == 1) {
				contextCount[0] = contexts.get(0);
				contextCount[1] = contexts.get(0);
			} else {
				throw new RuntimeException("Could not parse context "+contexts);
			}
		}
	}

	protected void processHelp() {
		System.out.println(
			"\n"
			+ "====AMI====\n"
			+ "AMI searches and indexes or transforms normlized structured scholarlyHTML (normally output by Norma).\n"
			+ "We believe that this task is independent of the original journal Pubstyle, but sometimes it may be necessary to\n"
			+ "indicate that.\n"
			+ "\n"
			+ "AMI selects the plugin either from the commandline, the explicit main class or (NYI) from a name in the arguments.\n"
			+ ""
			);
		super.processHelp();
	}


}
