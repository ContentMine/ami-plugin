package org.xmlcml.ami.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.VisitableInput;
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
	
	// MUST have leading slash
	public final static String RESOURCE_NAME_TOP = "/org/xmlcml/ami/visitor";
	private static String ARGS_RESOURCE = RESOURCE_NAME_TOP+"/"+"args.xml";
	
	public static final String WORD_FREQUENCIES = "wordFrequencies";
	public static final String WORD_LENGTHS = "wordLengths";
	public static List<String> HARDCODED_PARAMS = Arrays.asList(new String[] {WORD_LENGTHS, WORD_FREQUENCIES});
	
	Integer[] contextCount = new Integer[] {100, 100};
	
	private List<VisitableInput> visitableInputList;
	private VisitorOutput visitorOutput;
	private XPathProcessor xPathProcessor;

	private List<String> params;


	
	public AMIArgProcessor() {
		super();
		readArgConfiguration();
	}

	private void readArgConfiguration() {
		LOG.trace(ARGS_RESOURCE);
		this.readArgumentOptions(ARGS_RESOURCE);
        for (ArgumentOption argumentOption : argumentOptionList) {
			LOG.trace(argumentOption.getHelp());
		}
	}

	public AMIArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	public void parseInput(ArgumentOption divOption, ArgIterator argIterator) {
		super.parseInput(divOption, argIterator);
		if (inputList.size() > 0) {
			visitableInputList = new ArrayList<VisitableInput>();
			for (String input : inputList) {
				VisitableInput visitableInput = new VisitableInput(input);
				visitableInputList.add(visitableInput);
			}
		}
	}
	
	public void parseXpath(ArgumentOption argOption, ArgIterator argIterator) {
		List<String> xpaths = argIterator.createTokenListUpToNextMinus(argOption);
		if (xpaths.size() == 0) {
//			LOG.debug(XPATH_OPTION).getHelp());
		} else if (xpaths.size() > 1) {
			LOG.debug("Exactly one xpath required");
		} else {
			xPathProcessor = new XPathProcessor(xpaths.get(0));
		}
	}

	public void parseContext(ArgumentOption argOption, ArgIterator argIterator) {
		List<String> contexts = argIterator.createTokenListUpToNextMinus(argOption);
		if (contexts.size() == 0) {
			throw new IllegalArgumentException("required argument/s missing");
		}
		contextCount[0] = new Integer(contexts.get(0));
		if (contexts.size() == 2) {
			contextCount[1] = new Integer(contexts.get(1));
		} else {
			contextCount[1] = contextCount[0];
		}
	}

	public void parseParam(ArgumentOption argOption, ArgIterator argIterator) {
		setParams(argIterator.createTokenListUpToNextMinus(argOption));
		for (String param : getParams()) {
			if (!HARDCODED_PARAMS.contains(param)) {
				LOG.debug("The parameters can be "+HARDCODED_PARAMS +"found..."+getParams()+";");
				throw new RuntimeException("Bad param: "+param);
			}
		}
	}

	public void parseTest(ArgumentOption argOption, ArgIterator argIterator) {
		List<String> test = argIterator.createTokenListUpToNextMinus(argOption);
		LOG.debug("The test strings are..."+test+"; override this if you want to use your own parseTest()");
	}


	protected void printHelp() {
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
		super.printHelp();
	}

	public XPathProcessor getXPathProcessor() {
		return xPathProcessor;
	}

	public VisitorOutput getVisitorOutput() {
		return visitorOutput;
	}

	public List<VisitableInput> getVisitableInputList() {
		return visitableInputList;
	}

	public void debug() {
		// TODO Auto-generated method stub
		
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}


}
