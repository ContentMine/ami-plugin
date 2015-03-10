package org.xmlcml.ami.plugin.plugins.regex;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.plugins.AMIArgProcessor;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.files.EuclidSource;
import org.xmlcml.files.ResultsElement;
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
	
	private static final String REGEX = "regex";
	private static String REGEX_RESOURCE_NAME = AMIArgProcessor.PLUGIN_RESOURCE + "/regex";
	private static String ARGS_RESOURCE = REGEX_RESOURCE_NAME+"/"+"args.xml";

	private CompoundRegexList compoundRegexList;
	private Map<String, ResultsElement> resultsByCompoundRegex;
	protected List<String> words;
	
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
		getOrCreateCompoundRegexList();
		for (String regexLocation : regexLocations) {
			LOG.debug("RegexLocation "+regexLocation);
			try {
				CompoundRegex compoundRegex = readAndCreateCompoundRegex(EuclidSource.getInputStream(regexLocation));
				compoundRegexList.add(compoundRegex);
			} catch (Exception e) {
				LOG.error("Cannot parse regexLocation: ("+e+")"+regexLocation);
			}
			
		}
		for (CompoundRegex compoundRegex : compoundRegexList) {
			LOG.debug(compoundRegex);
		}
	}
	
	public void runRegex(ArgumentOption option) {
		List<HtmlP> pElements = extractPElements();
		resultsByCompoundRegex = new HashMap<String, ResultsElement>();
		for (CompoundRegex compoundRegex : compoundRegexList) {
			RegexSearcher regexSearcher = new RegexSearcher(compoundRegex);
			ResultsElement resultsElement = regexSearcher.search(pElements);
			resultsByCompoundRegex.put(compoundRegex.getTitle(), resultsElement);
		}
	}

	public void outputResultElements(ArgumentOption option) {
		for (CompoundRegex compoundRegex : compoundRegexList) {
			String regexTitle = compoundRegex.getTitle();
			ResultsElement resultsElement = resultsByCompoundRegex.get(regexTitle);
			currentQuickscrapeNorma.createResultsDirectoryAndOutputResultsElement(regexTitle, resultsElement, REGEX);
		}
	}

	private CompoundRegexList getOrCreateCompoundRegexList() {
		if (compoundRegexList == null) {
			compoundRegexList = new CompoundRegexList();
		}
		return compoundRegexList;
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

	/** creates a regex from InputStream if possible
	 * 	 * 
	 * @param file
	 * @param is TODO
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	private CompoundRegex readAndCreateCompoundRegex(InputStream is) {
		Element rootElement = null;
		try {
			Document doc = new Builder().build(is);
			rootElement = doc.getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot read or parse regexInputStream", e);
		}
		return new CompoundRegex(this, rootElement);
	}

}
