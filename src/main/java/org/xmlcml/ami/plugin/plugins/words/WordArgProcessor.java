package org.xmlcml.ami.plugin.plugins.words;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.plugins.AMIArgProcessor;
import org.xmlcml.ami.plugin.plugins.regex.CompoundRegex;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.files.ResultsElement;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class WordArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(WordArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static String RESOURCE_WORD_NAME_TOP = AMIArgProcessor.PLUGIN_RESOURCE + "/words";
	private static String ARGS_RESOURCE = RESOURCE_WORD_NAME_TOP+"/"+"args.xml";
	
	private static final String WORDS = "words";
	public final static String WORD_LENGTHS = "wordLengths";
	public final static String WORD_FREQUENCIES = "wordFrequencies";
	public final static List<String> ANALYSIS_METHODS = Arrays.asList(
		new String[]{
				WORD_FREQUENCIES,
				WORD_LENGTHS
		});
	
	public final static String ABBREVIATIONS = "abbreviation";
	public final static String CAPITALIZED = "capitalized";
	public final static List<String> WORD_TYPES = Arrays.asList(
		new String[]{
				ABBREVIATIONS,
				CAPITALIZED
		});
	
	private List<WordSetWrapper> stopwordSetList;
	private List<String> chosenMethods;
	private IntRange wordLengthRange;
	private List<String> chosenWordTypes;
	protected List<String> words;

	public WordArgProcessor() {
		super();
		this.readArgumentOptions(ARGS_RESOURCE);
        for (ArgumentOption argumentOption : argumentOptionList) {
			LOG.trace("WORD "+argumentOption.getHelp());
		}
	}

	public WordArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	/** select methods to use
	 * 
	 * @param option list of methods (none gives help)
	 * @param argIterator
	 */
	public void parseWords(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		if (tokens.size() == 0) {
			helpMethods();
		} else {
			chosenMethods = getChosenList(ANALYSIS_METHODS, tokens);
		}
	}

	private void helpMethods() {
		System.err.println("ANALYSIS METHODS");
		for (String method : ANALYSIS_METHODS) {
			System.err.println("  "+method);
		}
	}
	
	private void helpWordTypes() {
		System.err.println("WORD TYPES");
		for (String type : WORD_TYPES) {
			System.err.println("  "+type);
		}
	}

	public void parseStopwords(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		List<String> stopwordLocations = option.processArgs(tokens).getStringValues();
		ensureStopwordSetList();
		for (String stopwordLocation : stopwordLocations) {
			addStopwords(stopwordLocation);
		}
	}

	public void parseWordLengths(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		if (tokens.size() != 2) {
			LOG.error("Must give min and max word length: "+tokens);
		} else {
			try {
				wordLengthRange = new IntRange((int)new Integer(tokens.get(0)), (int)new Integer(tokens.get(1)));
				if (wordLengthRange.getMin() < 1 || wordLengthRange.getMax() < 1) {
					throw new RuntimeException("bad word lengths: "+tokens);
				}
			} catch (Exception e) {
				throw new RuntimeException("Bad word length arguments: "+tokens);
			}
		}
		
	}
	
	public void parseWordTypes(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		if (tokens.size() == 0) {
			helpWordTypes();
		} else {
			chosenWordTypes = getChosenList(WORD_TYPES, tokens);
		}
	}
	
	public void extractWords(ArgumentOption option) {
		WordCollectionFactory wordCollectionFactory = new WordCollectionFactory(this);
		wordCollectionFactory.extractWords();
	}
	
	public void outputWords(ArgumentOption option) {
		currentQuickscrapeNorma.createResultsDirectoryAndOutputResultsElement(WORDS, resultsElement, QuickscrapeNorma.RESULTS_XML);
	}
	
	// =============================

	private void addStopwords(String stopwordLocation) {
		WordSetWrapper stopwordSet = WordSetWrapper.createStopwordSet(stopwordLocation);
		if (stopwordSet != null) {
			stopwordSetList.add(stopwordSet);
		}
	}
	
	private void ensureStopwordSetList() {
		if (stopwordSetList == null) {
			stopwordSetList = new ArrayList<WordSetWrapper>();
		}
	}
	
	public IntRange getWordLengthRange() {
		return wordLengthRange;
	}

	public List<WordSetWrapper> getStopwordSetList() {
		return stopwordSetList;
	}

	public List<String> getChosenMethods() {
		return chosenMethods;
	}

	public List<String> getChosenWordTypes() {
		return chosenWordTypes;
	}

	

}
