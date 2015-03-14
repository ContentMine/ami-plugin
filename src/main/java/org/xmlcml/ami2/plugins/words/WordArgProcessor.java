package org.xmlcml.ami2.plugins.words;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.html.HtmlStyle;
import org.xmlcml.xml.XMLUtil;

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
	
	static final String WORD_ARG_PROCESSOR = "words";
	private static String RESOURCE_WORD_NAME_TOP = AMIArgProcessor.PLUGIN_RESOURCE + "/"+WORD_ARG_PROCESSOR;
	private static String ARGS_RESOURCE = RESOURCE_WORD_NAME_TOP+"/"+ARGS_XML;
	
//	private static final String WORDS = "words";
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

	public static final String FREQUENCIES = "frequencies";

	private static final String TFIDF = "tfidf";
	private static final String TFIDF_XML = "tfidf.xml";
	private static final String TFIDF_HTML = "tfidf.html";
	private static final String AGGREGATE_FREQUENCY = "aggregate";
	private static final String AGGREGATE_XML = "aggregate.xml";
	private static final String AGGREGATE_HTML = "aggregate.html";
	private static final String BOOLEAN_FREQUENCY = "booleanFrequency";
	private static final String BOOLEAN_FREQUENCY_XML = "booleanFrequency.xml";
	private static final String BOOLEAN_FREQUENCY_HTML = "booleanFrequency.html";
	private static final String TFIDF_FREQUENCY = "tfidfFrequency";
	private static final String TFIDF_FREQUENCY_XML = "tfidfFrequency.xml";
	private static final String TFIDF_FREQUENCY_HTML = "tfidfFrequency.html";
	private static final double MIN_FONT = 10;
	private static final double MAX_FONT = 30;
	
	private List<WordSetWrapper> stopwordSetList;
	private List<String> chosenMethods = new ArrayList<String>();
	private IntRange wordLengthRange;
	private List<String> chosenWordTypes;
	protected List<String> words;
	private Boolean stemming;
	private List<String> wordCaseList = new ArrayList<String>();
	private List<String> summaryMethods;
	WordResultsElementList frequenciesElementList;
	WordResultsElement aggregatedFrequenciesElement;
	private int minCount;
	private int maxCount;
	private WordResultsElement booleanFrequencyElement;

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
	
	public void parseMinCount(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		minCount = new Integer(tokens.get(0));
	}
	
	public void parseMaxCount(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		maxCount = new Integer(tokens.get(0));
	}
	
	public void runExtractWords(ArgumentOption option) {
		WordCollectionFactory wordCollectionFactory = new WordCollectionFactory(this);
		wordCollectionFactory.extractWords();
	}
	
	public void outputWords(ArgumentOption option) {
		currentQuickscrapeNorma.createResultsDirectoryAndOutputResultsElement(option, resultsElementList, QuickscrapeNorma.RESULTS_XML);
	}
	
	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		if (tokens.size() == 0) {
			LOG.error("parseSummary needs a list of actions");
		} else {
			summaryMethods = tokens;
		}
	}
	
	public void finalSummary(ArgumentOption option) {
		WordResultsElementList frequenciesElementList = this.aggregateOverQSNormaList(WordArgProcessor.WORD_ARG_PROCESSOR, WordArgProcessor.FREQUENCIES);
		WordCollectionFactory wordCollectionFactory = new WordCollectionFactory(this);
		for (String method : summaryMethods) {
			runSummaryMethod(frequenciesElementList, wordCollectionFactory, method);
		}
	}

	private void runSummaryMethod(WordResultsElementList frequenciesElementList,
			WordCollectionFactory wordCollectionFactory, String method) {
		if (AGGREGATE_FREQUENCY.equals(method) && summaryFileName != null) {
			aggregatedFrequenciesElement = wordCollectionFactory.createAggregatedFrequenciesElement(frequenciesElementList);
			writeResultsElement(new File(summaryFileName, AGGREGATE_XML), aggregatedFrequenciesElement);
			writeResultsElementAsHTML(new File(summaryFileName, AGGREGATE_HTML), aggregatedFrequenciesElement);
		} else if (BOOLEAN_FREQUENCY.equals(method) && summaryFileName != null) {
			booleanFrequencyElement = wordCollectionFactory.createBooleanFrequencies(this, frequenciesElementList);
			writeResultsElement(new File(summaryFileName, BOOLEAN_FREQUENCY_XML), booleanFrequencyElement);
			writeResultsElementAsHTML(new File(summaryFileName, BOOLEAN_FREQUENCY_HTML), booleanFrequencyElement);
		} else if (TFIDF_FREQUENCY.equals(method) && summaryFileName != null) {
			WordResultsElement tfidfFrequencyElement = wordCollectionFactory.createTFIDFFrequencies(this, frequenciesElementList);
			writeResultsElement(new File(summaryFileName, TFIDF_XML), tfidfFrequencyElement);
			writeResultsElementAsHTML(new File(summaryFileName, TFIDF_HTML), tfidfFrequencyElement);
		}
	}

	private static void writeResultsElement(File outputFile, ResultsElement resultsElement) {
		try {
			outputFile.getParentFile().mkdirs();
			XMLUtil.debug(resultsElement, new FileOutputStream(outputFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file "+outputFile, e);
		}
	}
	
	// =============================

	public WordResultsElementList aggregateOverQSNormaList(String pluginName, String methodName) {
		WordResultsElementList resultsElementList = new WordResultsElementList();
		for (QuickscrapeNorma qsn : quickscrapeNormaList) {
			WordResultsElement resultsElement = new WordResultsElement(qsn.getResultsElement(pluginName, methodName));
			resultsElementList.add(resultsElement);
		}
		return resultsElementList;
	}

	private void writeResultsElementAsHTML(File outputFile, WordResultsElement wordResultsElement) {
		IntArray countArray = wordResultsElement.getCountArray();
		LOG.debug("CountArray: "+countArray);
		IntRange countRange = countArray.getRange();
		RealRange realCountRange = new RealRange(countRange);
		RealRange fontRange = new RealRange(MIN_FONT, MAX_FONT);
		double countToFont = realCountRange.getScaleTo(fontRange);
		LOG.debug("countToFont "+countToFont);
		RealArray fontSizeArray = new RealArray(countArray);
		fontSizeArray = fontSizeArray.multiplyBy(countToFont);
		fontSizeArray = fontSizeArray.addScalar(MIN_FONT);
		IntArray fontSizeIntArray = fontSizeArray.createIntArray();
		
		Set<Integer> fontSizeSet = fontSizeIntArray.createIntegerSet();
//		LOG.debug("FontSizes: "+fontSizeIntArray);
//		LOG.debug("FontSet: "+fontSizeSet);
		HtmlElement html = createHtmlElement(wordResultsElement, fontSizeIntArray, fontSizeSet);
		try {
			outputFile.getParentFile().mkdirs();
			XMLUtil.debug(html, new FileOutputStream(outputFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file "+outputFile, e);
		}

		
	}

	private HtmlElement createHtmlElement(WordResultsElement wordResultsElement,
			IntArray fontSizeIntArray, Set<Integer> fontSizeSet) {
		HtmlElement html = new HtmlHtml();
		HtmlStyle style = new HtmlStyle();
		html.appendChild(style);
		style.addCss("* { font-family : helvetica;}");
		for (Integer fontSize : fontSizeSet) {
			String cssStyle = ".font"+fontSize+" { font-size : "+fontSize+"; }";
			style.addCss(cssStyle);
		}
		HtmlBody body = new HtmlBody();
		html.appendChild(body);
		HtmlP p = new HtmlP();
		body.appendChild(p);
		addWordsWithFontSizesInSpans(wordResultsElement, fontSizeIntArray, p);
		return html;
	}

	private void addWordsWithFontSizesInSpans(WordResultsElement wordResultsElement,
			IntArray fontSizeIntArray, HtmlP p) {
		int i = 0;
		for (ResultElement resultElement : wordResultsElement) {
			WordResultElement wordResultElement = (WordResultElement) resultElement;
			String word = wordResultElement.getWord();
			int count = wordResultElement.getCount();
			int fontSize = fontSizeIntArray.elementAt(i);
			HtmlSpan span = new HtmlSpan();
			span.setClassAttribute("font"+fontSize);
			span.appendChild(word+" ");
			p.appendChild(span);
			i++;
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
