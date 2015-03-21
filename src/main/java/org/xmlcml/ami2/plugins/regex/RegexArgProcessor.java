package org.xmlcml.ami2.plugins.regex;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.files.EuclidSource;
import org.xmlcml.files.QuickscrapeNorma;
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
	
	private CompoundRegexList compoundRegexList;
	private Map<String, ResultsElement> resultsByCompoundRegex;
	protected List<String> words;
	
	public RegexArgProcessor() {
		super();
	}

	public RegexArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	public void parseRegex(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens= argIterator.createTokenListUpToNextNonDigitMinus(option);
		createCompoundRegexes(option, tokens);
	}

	public void runRegex(ArgumentOption option) {
		runRegex();
	}

	public void outputResultElements(ArgumentOption option) {
		outputResultElementsx(option);
	}

	// =========================
	
	/** might need to refcator option to use its name.
	 * 
	 * @param option
	 */
	private void outputResultElementsx(ArgumentOption option) {
		resultsElementList = new ArrayList<ResultsElement>();
		for (CompoundRegex compoundRegex : compoundRegexList) {
			String regexTitle = compoundRegex.getTitle();
			ResultsElement resultsElement = resultsByCompoundRegex.get(regexTitle);
			resultsElement.setTitle(regexTitle);
			resultsElementList.add(resultsElement);
		}
		currentQuickscrapeNorma.createResultsDirectoriesAndOutputResultsElement(option, resultsElementList, QuickscrapeNorma.RESULTS_XML);
	}

	private void runRegex() {
		List<HtmlP> pElements = extractPElements();
		resultsByCompoundRegex = new HashMap<String, ResultsElement>();
		for (CompoundRegex compoundRegex : compoundRegexList) {
			RegexSearcher regexSearcher = new RegexSearcher(compoundRegex);
			ResultsElement resultsElement = regexSearcher.search(pElements);
			resultsByCompoundRegex.put(compoundRegex.getTitle(), resultsElement);
		}
	}

	private void createCompoundRegexes(ArgumentOption option, List<String> tokens) {
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
