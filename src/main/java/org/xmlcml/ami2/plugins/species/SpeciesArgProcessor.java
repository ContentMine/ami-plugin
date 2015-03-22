package org.xmlcml.ami2.plugins.species;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.sequence.SequenceSearcher;
import org.xmlcml.ami2.plugins.sequence.SequenceType;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlP;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SpeciesArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = Logger.getLogger(SpeciesArgProcessor.class);
	private ResultsElement resultsElement;
	private HashMap<String, SpeciesSearcher> speciesSearcherByTypeStringMap;
	private List<SpeciesSearcher> speciesSearcherList;
	private HashMap<String, ResultsElement> resultsElementBySpeciesTypeMap;
	private Boolean expandAbbreviations;
	
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public SpeciesArgProcessor() {
		super();
	}

	public SpeciesArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	public SpeciesArgProcessor(String argString) {
		this(argString.split("\\s+"));
	}

	// =============== METHODS ==============
	
	public void initSpecies(ArgumentOption option) {
		List<Element> values = option.getOrCreateValues();
		for (Element valueElement : values) {
			try {
				createAndStoreSpeciesSearchers(valueElement);
			} catch (Exception e) {
				LOG.error("Could not create SpeciesSearcher "+valueElement.getAttributeValue(NAME)+"; "+e.getCause());
				continue;
			}
		}
	}

	public void parseTypes(ArgumentOption option, ArgIterator argIterator) {
		List<String> types = argIterator.getStrings(option);
		convertToSpecieSearchers(types);
	}

	public void parseAbbreviations(ArgumentOption option, ArgIterator argIterator) {
		expandAbbreviations = argIterator.getBoolean(option);
	}

	public void runExtractSpecies(ArgumentOption option) {
		ensureResultsElementBySpeciesType();
		for (SpeciesSearcher speciesSearcher : speciesSearcherList) {
			List<HtmlP> pElements = extractPElements();
			resultsElement = speciesSearcher.search(pElements);
			resultsElementBySpeciesTypeMap.put(speciesSearcher.getSpeciesType(), resultsElement);
		}
	}

	private void ensureResultsElementBySpeciesType() {
		if (resultsElementBySpeciesTypeMap == null) {
			resultsElementBySpeciesTypeMap = new HashMap<String, ResultsElement>();
		}
	}

	public void outputSpecies(ArgumentOption option) {
		outputResultElements(option);
	}
	
	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
//		summaryMethods = argIterator.getStrings(option);
		LOG.debug("summary methods not yet written");
	}
	
	public void finalSummary(ArgumentOption option) {
		LOG.debug("final summary not yet written");
	}

	// =============================

	private void createAndStoreSpeciesSearchers(Element valueElement) {
		ensureSpeciesSearcherByTypeString();
		String type = valueElement.getAttributeValue(NAME);
		String patternString = valueElement.getValue();
		Pattern pattern = null;
		try {
			pattern = Pattern.compile(patternString);
		} catch (Exception e) {
			LOG.debug("BAD REGEX: "+patternString);
			throw new RuntimeException("Bad regex", e);
		}
		SpeciesSearcher speciesSearcher = new SpeciesSearcher(this, type, pattern);
		speciesSearcherByTypeStringMap.put(type, speciesSearcher);
	}

	private void ensureSpeciesSearcherByTypeString() {
		if (speciesSearcherByTypeStringMap == null) {
			speciesSearcherByTypeStringMap = new HashMap<String, SpeciesSearcher>();
		}
	}

	private void convertToSpecieSearchers(List<String> types) {
		if (speciesSearcherByTypeStringMap == null) {
			throw new RuntimeException("No species read");
		}
		ensureSpeciesSearchers();
		for (String type : types) {
			SpeciesSearcher speciesSearcher = speciesSearcherByTypeStringMap.get(type);
			if (speciesSearcher == null) {
				LOG.error("unknown sequenceType: "+type+"; skipped");
			} else {
				speciesSearcherList.add(speciesSearcher);
			}
		}
	}

	private void ensureSpeciesSearchers() {
		if (speciesSearcherList == null) {
			speciesSearcherList = new ArrayList<SpeciesSearcher>();
		}
	}

	private void outputResultElements(ArgumentOption option) {
		resultsElementList = new ArrayList<ResultsElement>();
		for (SpeciesSearcher speciesSearcher : speciesSearcherList) {
			String type = speciesSearcher.getSpeciesType();
			ResultsElement resultsElement = resultsElementBySpeciesTypeMap.get(type);
			if (resultsElement != null) {
				resultsElement.setTitle(type);
				resultsElementList.add(resultsElement);
			}
		}
		currentQuickscrapeNorma.createResultsDirectoriesAndOutputResultsElement(
				option, resultsElementList, QuickscrapeNorma.RESULTS_XML);
	}


}
