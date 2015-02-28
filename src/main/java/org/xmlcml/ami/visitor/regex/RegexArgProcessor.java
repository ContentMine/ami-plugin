package org.xmlcml.ami.visitor.regex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.visitor.AMIArgProcessor;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class RegexArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(RegexArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static String RESOURCE_REGEX_NAME_TOP = AMIArgProcessor.RESOURCE_NAME_TOP + "/regex";
	private static String ARGS_RESOURCE = RESOURCE_REGEX_NAME_TOP+"/"+"args.xml";

	private List<RegexContainer> regexContainerList;
	
	public RegexArgProcessor() {
		super();
		this.readArgumentOptions(ARGS_RESOURCE);
        for (ArgumentOption argumentOption : argumentOptionList) {
			LOG.trace("REGEX "+argumentOption.getHelp());
		}
	}

	public RegexArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	public void parseRegex(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens= argIterator.createTokenListUpToNextMinus(option);
		List<String> regexLocations = option.processArgs(tokens).getStringValues();
		ensureRegexContainerList();
		for (String regexLocation : regexLocations) {
			try {
				RegexContainer regexContainer = parseRegex(regexLocation);
				regexContainerList.add(regexContainer);
			} catch (IOException e) {
				LOG.error("Could not parse "+regexLocation);
			}
		}
	}
	
	// =============================

	private void ensureRegexContainerList() {
		if (regexContainerList == null) {
			regexContainerList = new ArrayList<RegexContainer>();
		}
	}

	private RegexContainer parseRegex(String regexLocation) throws IOException {
		CompoundRegex compoundRegex = CompoundRegex.readAndCreateRegex(regexLocation);
		LOG.debug("CR: "+compoundRegex);
		RegexContainer regexContainer = new RegexContainer();
		regexContainer.addCompoundRegex(compoundRegex);
		return regexContainer;
	}

	protected void printHelp() {
		System.out.println(
			"\n"
			+ "====REGEX====\n"
					+ "process regex help NYI"
			);
		super.printHelp();
	}

	public List<RegexContainer> getRegexContainerList() {
		ensureRegexContainerList();
		return regexContainerList;
	}
}
