package org.xmlcml.ami2.plugins.words;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.regex.CompoundRegex;
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
	
	public final static String PRESERVE = "preserve";
	public final static String IGNORE = "ignore";
	public final static List<String> CASE_TYPES = Arrays.asList(
		new String[]{
				IGNORE,
				ABBREVIATIONS,
				PRESERVE
		});
	
	private List<WordSetWrapper> stopwordSetList;
	private List<String> chosenMethods = new ArrayList<String>();
	private IntRange wordLengthRange;
	private List<String> chosenWordTypes;
	protected List<String> words;
	private Boolean stemming;
	private List<String> wordCaseList = new ArrayList<String>();

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

	/** use stemming?
	 * 
	 * @param option list of methods (none gives help)
	 * @param argIterator
	 */
	public void parseCase(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		wordCaseList = new ArrayList<String>();
		if (tokens.size() == 0) {
			wordCaseList.add(PRESERVE);
		} else {
			wordCaseList = tokens;
		}
		checkWordCaseList();
	}

	/** use stemming?
	 * 
	 * will have to use import org.apache.lucene.analysis.en.PorterStemFilter;
	 * 
	 * @param option list of methods (none gives help)
	 * @param argIterator
	 */
	public void parseStem(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		stemming = (tokens.size() == 0) ? true : new Boolean(tokens.get(0));
		LOG.info("Stemming noy yet implemented");
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
		currentQuickscrapeNorma.createResultsDirectoryAndOutputResultsElement(option, resultsElementList, QuickscrapeNorma.RESULTS_XML);
	}
	
	// =============================

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

	private void checkWordCaseList() {
		if (wordCaseList.size() == 1 && PRESERVE.equals(wordCaseList.get(0))) {
			// OK
		} else {
			for (int i = wordCaseList.size() - 1; i >= 0; i--) {
				String word = wordCaseList.get(i);
				if (wordCaseList.contains(PRESERVE) || !CASE_TYPES.contains(word)) {
					LOG.error("Removed forbidden/unknown word: "+word);
					wordCaseList.remove(i);
				}
			}
		}
	}

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

	public boolean getStemming() {
		return stemming;
	}

	public List<String> getWordCaseList() {
		return wordCaseList;
	}

	public List<String> getChosenWordTypes() {
		ensureChosenWordTypes();
		return chosenWordTypes;
	}

	private void ensureChosenWordTypes() {
		if (chosenWordTypes == null) {
			chosenWordTypes = new ArrayList<String>();
		}
	}
}
