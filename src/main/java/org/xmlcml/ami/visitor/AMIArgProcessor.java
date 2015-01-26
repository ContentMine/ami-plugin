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
	
	public final static ArgumentOption CONTEXT_OPTION = new ArgumentOption(
			AMIArgProcessor.class,
			"-c",
			"--context",
			"characterCount",
			"\n"
			+ "CONTEXT:\n"
			+ "The text or other content immediately surrounding the located word/phrase/object. By default this is either\n"
			+ "+- 100 characters (Publisher limit) or to the end of the object (paragraph). Two integers give the oreceeding and \n"
			+ "following text. if one integer is given that is used for pre- and post- counts; \n"
			+ "",
			Integer.class,
			new Integer(100),
			1, 2,
			"processContext"
			);
		
	public final static List<ArgumentOption> AMI_OPTION_LIST = Arrays.asList(
			new ArgumentOption[] {
					CONTEXT_OPTION
			});
	
	Integer contextCount = 100;
	
	public AMIArgProcessor() {
		super();
	}

	public AMIArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	protected List<ArgumentOption> getOptionList() {
		List<ArgumentOption> optionList = new ArrayList<ArgumentOption>(AMI_OPTION_LIST);
		optionList.addAll(super.getArgumentOptionList());
		return optionList;
	}
	

	// =============== METHODS ==============

	public void processContext(ArgumentOption argOption, ArgIterator argIterator) {
		List<String> inputs = argIterator.createTokenListUpToNextMinus();
		if (inputs.size() == 0) {
			LOG.debug(CONTEXT_OPTION.getHelp());
		} else {
			String c = argOption.processArgs(inputs).getStringValue();
			contextCount = new Integer(c);
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
