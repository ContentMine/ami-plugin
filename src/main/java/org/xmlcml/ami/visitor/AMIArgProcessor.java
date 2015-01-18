package org.xmlcml.ami.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.args.DefaultArgProcessor;
import org.xmlcml.norma.Pubstyle;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class AMIArgProcessor extends DefaultArgProcessor{
	
	private static final Logger LOG = Logger.getLogger(AMIArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String HELP_NORMA = "Norma help";
	
	public final static ArgumentOption PUBSTYLE_OPTION = new ArgumentOption(
			"-p",
			"--pubstyle",
			"pub_code",
			"\n"
			+ "PUBSTYLE:\n"
			+ "Code or mnemomic to identifier the publisher or journal style. \n"
			+ "this is a list of journal/publisher styles so Norma knows how to interpret the input. At present only one argument \n"
			+ "is allowed. The pubstyle determines the format of the XML or HTML, the metadata, and\n"
			+ "soon how to parse the PDF. At present we'll use mnemonics such as 'bmc' or 'biomedcentral.com' or 'cellpress'.\n"
			+ "To get a list of these use "+"--pubstyle"+" without arguments. Note: under early development and note also that \n"
			+ "publisher styles change and can be transferred between publishers and journals.\n"
			+ "\n"
			+ "Note that Pubstyle should be largely eliminated by Norma/lization but we don't know whether some Journal-specific \n"
			+ "semantics will carry through to AMI\n",
			String.class,
			Pubstyle.PLOSONE.toString(),
			1, 1
			);
		
	public final static ArgumentOption CONTEXT_OPTION = new ArgumentOption(
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
			1, 2
			);
		
	private List<String> pubstyleList;

	private Pubstyle pubstyle;

	public AMIArgProcessor() {
		super();
	}

	public AMIArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public boolean parseArgs(String[] commandLineArgs) {
		List<String> args = Arrays.asList(commandLineArgs);
		ListIterator<String> listIterator = args.listIterator();
		return parseArgs(listIterator);
	}

	protected boolean parseArgs(ListIterator<String> listIterator) {
		boolean parsed = true;
		while(listIterator.hasNext()) {
			if (!super.parseArgs(listIterator)) {
				parsed &= parseArgs1(listIterator);
			}
		}
		return parsed;
	}
	
	protected boolean parseArgs1(ListIterator<String> listIterator) {
		boolean processed = false;
		if (listIterator.hasNext()) {
			processed = true;
			String arg = listIterator.next();
			LOG.trace("norma:"+arg);
			if (!arg.startsWith(MINUS)) {
				LOG.error("Parsing failed at: ("+arg+"), expected \"-\" trying to recover");
			} else if (PUBSTYLE_OPTION.matches(arg)) {
				processPubstyle(PUBSTYLE_OPTION, listIterator); 
			} else {
				processed = false;
				LOG.error("Unknown arg: ("+arg+"), trying to recover");
			}
		}
		return processed;
	}

	private void processPubstyle(ArgumentOption argOption, ListIterator<String> listIterator) {
		List<String> inputs = createTokenListUpToNextMinus(listIterator);
		if (inputs.size() == 0) {
			pubstyleList = new ArrayList<String>();
			Pubstyle.help();
		} else {
			String name = argOption.processArgs(inputs).getString();
			pubstyle = Pubstyle.getPubstyle(name);
		}
	}

	protected void processHelp() {
		System.out.println(
				"\n"
				+ "====AMI====\n"
				+ "Norma searches and indexes or transforms normlized structured scholarlyHTML (normally output by Norma).\n"
				+ "We believe that this task is independent of the original journal Pubstyle, but sometimes it may be necessary to\n"
				+ "indicate that.\n"
				+ "\n"
				+ "AMI selects the plugin either from the commandline, the explicit main class or (NYI) from a name in the arguments.\n"
				+ ""
				);
		System.out.println(PUBSTYLE_OPTION.getHelp());
		super.processHelp();
	}

	public Pubstyle getPubstyle() {
		return pubstyle;
	}

}
