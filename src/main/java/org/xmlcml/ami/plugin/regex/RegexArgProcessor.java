package org.xmlcml.ami.plugin.regex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.AMIArgProcessor;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;

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
	
	private static String REGEX_RESOURCE_NAME = AMIArgProcessor.PLUGIN_RESOURCE + "/regex";
	private static String ARGS_RESOURCE = REGEX_RESOURCE_NAME+"/"+"args.xml";

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
	
	public void runRegex(ArgumentOption option) {
		LOG.debug("Running REGEX NYI");
		List<HtmlP> pElements = extracted();
		LOG.debug(pElements.size());
	}

	private List<HtmlP> extracted() {
		HtmlElement htmlElement = AMIArgProcessor.getScholarlyHtmlElement(currentQuickscrapeNorma);
		List<HtmlP> pElements = HtmlP.extractSelfAndDescendantIs(htmlElement);
		return pElements;
	}

	public void outputResultElements(ArgumentOption option) {
		LOG.debug("Output REGEX NYI");
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

	public List<RegexContainer> getRegexContainerList() {
		ensureRegexContainerList();
		return regexContainerList;
	}

	@Override
	/** parse args and resolve their dependencies.
	 * 
	 * (don't run any argument actions)
	 * 
	 */
	public void parseArgs(String[] args) {
		super.parseArgs(args);
	}

}
