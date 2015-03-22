package org.xmlcml.ami2.plugins.word;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.files.ResultsElementList;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

public class WordCollectionFactory {
	private static final Logger LOG = Logger.getLogger(WordCollectionFactory.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String COUNT2 = "count";
	private static final String VALUE = "value";
	private static final String LENGTH = "length";
	private static final String LENGTHS = "lengths";
	private static final String FREQUENCIES_ATT = "frequencies";
	private static final String FREQUENCY_ATT = "frequency";
	private static final String PROPERTY = "property";

	private static final String DOCUMENT_FREQUENCY   = "documentFrequency";
	private static final String BOOLEAN_FREQUENCIES = "booleanFrequencies";
	private static final String TFIDF_FREQUENCY      = "tfidfFrequency";
	private static final String TFIDF_FREQUENCIES    = "tfidfFrequencies";

	private static final int DEFAULT_MIN_COUNT_IN_SET = 4;            // for set
	private static final int DEFAULT_MIN_RAW_WORD_LENGTH = 3;
	private static final int DEFAULT_MAX_RAW_WORD_LENGTH = 99999;
	
	private WordSetWrapper stopwords;
	private List<String> currentWords;
	private WordArgProcessor wordArgProcessor;
	private List<String> abbreviations;
	private List<String> capitalized;
	private int minCountInSet;
	private int minRawWordLength;
	private int maxRawWordLength;
	private String sortControl = COUNT2;
	private Iterable<Multiset.Entry<String>> entriesSortedByCount;
	private Iterable<Multiset.Entry<String>> entriesSortedByValue;
	private WordResultsElement frequenciesElement;
	private WordResultsElement aggregatedFrequenciesElement;
	private WordResultsElement booleanFrequenciesElement;

	protected WordCollectionFactory(WordArgProcessor wordArgProcessor) {
		this.wordArgProcessor = wordArgProcessor;
		setDefaults();
	}

	private void setDefaults() {
		minCountInSet = DEFAULT_MIN_COUNT_IN_SET;
		minRawWordLength = DEFAULT_MIN_RAW_WORD_LENGTH;
		maxRawWordLength = DEFAULT_MAX_RAW_WORD_LENGTH;
	}

	void extractWords() {
		createWordSets();
		if (wordArgProcessor.getChosenMethods().contains(WordArgProcessor.WORD_LENGTHS)) {
			createWordLengths();
		}
		if (wordArgProcessor.getChosenMethods().contains(WordArgProcessor.WORD_FREQUENCIES)) {
			createWordFrequencies();
		}
	}

	private void createWordLengths() {
		ResultsElement element = getWordlengths();
		wordArgProcessor.addResultsElement(element);
	}

	private void createWordFrequencies() {
		ResultsElement element = getWordFrequencies();
		wordArgProcessor.addResultsElement(element);
	}

	private void createWordSets() {
		List<String> rawWords = wordArgProcessor.extractWordsFromScholarlyHtml();
		currentWords = rawWords;
		if (wordArgProcessor.getChosenWordTypes().contains(WordArgProcessor.ABBREVIATIONS)) {
			currentWords = createAbbreviations();
		} else if (wordArgProcessor.getChosenWordTypes().contains(WordArgProcessor.CAPITALIZED)) {
			currentWords = createCapitalized();
		} else {
			if (wordArgProcessor.getWordCaseList().contains(WordArgProcessor.IGNORE)) {
				currentWords = toLowerCase(currentWords);
			}
		}
//		stopwords = WordSetWrapper.getCommonEnglishStopwordSet();
//		currentWords = applyStopwordFilter(stopwords, rawWords);
		for (WordSetWrapper stopwordSet : wordArgProcessor.getStopwordSetList()) {
			currentWords = applyStopwordFilter(stopwordSet, currentWords);
		}
	}

	private List<String> createAbbreviations() {
		abbreviations = new ArrayList<String>();
		for (String word : currentWords) {
			if (isAbbreviation(word)) {
				abbreviations.add(word);
			}
		}
		return abbreviations;
	}

	private List<String> createCapitalized() {
		capitalized = new ArrayList<String>();
		for (String word : currentWords) {
			if (isCapitalized(word)) {
				capitalized.add(word);
			}
		}
		return capitalized;
	}

	/** includes nonleading uppercase characters or numbers or '-'.
	 * 
	 * @param word
	 * @return
	 */
	private boolean isAbbreviation(String word) {
		if (word == null || word.length() == 0) return false;
		// must start with Uppercase
		if (!Character.isUpperCase(word.charAt(0))) return false;
		int nupper = 0;
		int nlower = 0;
		int npunct = 0;
		int ndigit = 0;
		for (int i = 1; i < word.length(); i++) {
			char c = word.charAt(i);
			if (Character.isUpperCase(c)) {
				nupper++;
			} else if (Character.isLowerCase(c)) {
				nlower++;
			} else if (Character.isDigit(c)) {
				ndigit++;
			} else if (c == '-') {
				npunct++;
			} else {
				return false;
			}
		}
		// very crude
		if (nupper > nlower) {
			return true;
		}
		return false;
	}

	/** starts with uppercase characters and then only lower alpha.
	 * 
	 * @param word
	 * @return
	 */
	private boolean isCapitalized(String word) {
		if (!Character.isUpperCase(word.charAt(0))) return false;
		for (int i = 1; i < word.length(); i++) {
			char c = word.charAt(i);
			if (!Character.isLowerCase(c)) {
				return false;
			}
		}
		return true;
	}

	private List<String> applyStopwordFilter(WordSetWrapper stopwords, List<String> rawWords) {
		currentWords = new ArrayList<String>();
		for (String word : rawWords) {
			word = word.trim();
			if (!stopwords.contains(word.toLowerCase())) {
				currentWords.add(word);
			}
		}
		LOG.debug("stopwords "+stopwords.size()+"; current words: "+currentWords.size());
		return currentWords;
	}
	
	private List<String> toLowerCase(List<String> words) {
		ArrayList<String> newList = new ArrayList<String>();
		for (String word : words) {
			newList.add(word.toLowerCase());
		}
		return newList;
	}

	private WordResultsElement getWordlengths() {
		Multiset<Integer> lengthSet = HashMultiset.create();
		for (String word : currentWords) {
			lengthSet.add(word.length());
		}
		return getWordLengths(lengthSet);
	}

	private WordResultsElement getWordLengths(Multiset<Integer> lengthSet) {
		WordResultsElement lengthsElement = new WordResultsElement(LENGTHS);
		for (Entry<Integer> entry : lengthSet.entrySet()) {
			WordResultElement lengthElement = new WordResultElement(LENGTH);
			lengthElement.setLength(entry.getElement().intValue());
			lengthElement.setCount(entry.getCount());
			lengthsElement.appendChild(lengthElement);
		}
		return lengthsElement;
	}
	
	private WordResultsElement getWordFrequencies() {
		Multiset<String> wordSet = HashMultiset.create();
		for (String rawWord : currentWords) {
//			rawWord = rawWord.toLowerCase(); // normalize case
			rawWord = rawWord.replaceAll("[\\d+]", ""); // remove numbers
//			if (!stopwords.contains(rawWord.toLowerCase()) 
			if (rawWord.length() >= minRawWordLength 
					&& rawWord.length() <= maxRawWordLength) { //remove stopwords and short strings
				wordSet.add(rawWord);
			}
		}		
		entriesSortedByCount = getEntriesSortedByCount(wordSet);
		entriesSortedByValue = getEntriesSortedByValue(wordSet);
		Iterable<Multiset.Entry<String>> sortedEntries = getSortedEntries();
		frequenciesElement =  createFrequenciesElement(sortedEntries);
		return frequenciesElement;
	}

	private WordResultsElement createFrequenciesElement(
			Iterable<Multiset.Entry<String>> sortedEntries) {
		frequenciesElement = new WordResultsElement(FREQUENCIES_ATT);
		for (Entry<String> entry : sortedEntries) {
			int count = +entry.getCount();
			if (count < minCountInSet) continue;
			WordResultElement frequencyElement = new WordResultElement(FREQUENCY_ATT);
			frequencyElement.addAttribute(new Attribute(WordResultElement.WORD_ATT, String.valueOf(entry.getElement())));
			frequencyElement.addAttribute(new Attribute(WordResultElement.COUNT_ATT, String.valueOf(count)));
			frequenciesElement.appendChild(frequencyElement);
		}
		return frequenciesElement;
	}

	/** convenience method (for my memory!) */
	public static Iterable<Entry<String>> getEntriesSortedByValue(Multiset<String> wordSet) {
		return ImmutableSortedMultiset.copyOf(wordSet).entrySet();
	}

	/** convenience method (for my memory!) */
	public static Iterable<Multiset.Entry<String>> getEntriesSortedByCount(Multiset<String> wordSet) {
		return Multisets.copyHighestCountFirst(wordSet).entrySet();
	}

	private Iterable<Multiset.Entry<String>> getSortedEntries() {
		Iterable<Multiset.Entry<String>> sortedEntries = null;
		if (sortControl.equals(COUNT2)) {
			sortedEntries = entriesSortedByCount;
		} else if (sortControl.equals(VALUE)) {
			sortedEntries = entriesSortedByValue;
		}
//		Iterable<Multiset.Entry<String>> sortedEntries = entriesSortedByValue;
		return sortedEntries;
	}

	public int getMinCountInSet() {
		return minCountInSet;
	}

	public void setMinCountInSet(int minCountInSet) {
		this.minCountInSet = minCountInSet;
	}

	public int getMinRawWordLength() {
		return minRawWordLength;
	}

	public void setMinRawWordLength(int minRawWordLength) {
		this.minRawWordLength = minRawWordLength;
	}

	public int getMaxRawWordLength() {
		return maxRawWordLength;
	}

	public void setMaxRawWordLength(int maxRawWordLength) {
		this.maxRawWordLength = maxRawWordLength;
	}

	public WordSetWrapper getStopwords() {
		return stopwords;
	}

	public List<String> getCurrentWords() {
		return currentWords;
	}

	public List<String> getAbbreviations() {
		return abbreviations;
	}

	public List<String> getCapitalized() {
		return capitalized;
	}

	/** aggregate frequencies for each ResultsElement.
	 * 
	 * @param resultsElementList
	 */
	public void createAggregateFrequenciesElement(ResultsElementList resultsElementList) {
		Multiset<String> aggregateSet = this.createAggregateSet(resultsElementList);
		Iterable<Entry<String>> sortedEntries = getEntriesSortedByCount(aggregateSet);
		this.createWordResultElementsAndAddToAggregateFrequenciesElement(sortedEntries);
	}

	private Multiset<String> createAggregateSet(ResultsElementList resultsElementList) {
		Multiset<String> aggregateSet = HashMultiset.create();
		for (ResultsElement resultsElement : resultsElementList) {
			String title = resultsElement.getTitle();
			if (!WordArgProcessor.FREQUENCIES.equals(title)) {
				LOG.debug("Skipped non:frequencies result: "+title);
				continue;
			}
			addResultsToSet(aggregateSet, resultsElement);
		}
		return aggregateSet;
	}

	private WordResultsElement createWordResultElementsAndAddToAggregateFrequenciesElement(Iterable<Entry<String>> sortedEntries) {
		aggregatedFrequenciesElement = new WordResultsElement(WordArgProcessor.FREQUENCIES);
		for (Entry<String> entry : sortedEntries) {
			WordResultElement wordResultElement = new WordResultElement(FREQUENCY_ATT);
			wordResultElement.setWord(entry.getElement());
			wordResultElement.setCount(entry.getCount());
			aggregatedFrequenciesElement.appendChild(wordResultElement);
		}
		return aggregatedFrequenciesElement;
	}

	private void addResultsToSet(Multiset<String> aggregateSet, ResultsElement resultsElement) {
		for (ResultElement resultElement : resultsElement) {
			WordResultElement wordResultElement = (WordResultElement) resultElement;
			String word = wordResultElement.getWord();
			Integer count = wordResultElement.getCount();
			aggregateSet.add(word, count);
		}
	}

	WordResultsElement createAggregatedFrequenciesElement(WordResultsElementList frequenciesElementList) {
		Multiset<String> aggregateSet = createAggregateSet(frequenciesElementList);
		Iterable<Entry<String>> sortedEntries = WordCollectionFactory.getEntriesSortedByCount(aggregateSet);
		aggregatedFrequenciesElement = createWordResultElementsAndAddToAggregateFrequenciesElement(sortedEntries);
		return aggregatedFrequenciesElement;
	}

	WordResultsElement createBooleanFrequencies(AMIArgProcessor wordArgProcessor, WordResultsElementList frequenciesElementList) {
		aggregatedFrequenciesElement = createAggregatedFrequenciesElement(frequenciesElementList);
		booleanFrequenciesElement = new WordResultsElement(BOOLEAN_FREQUENCIES);
		for (ResultElement termElement : aggregatedFrequenciesElement) {
			String word = ((WordResultElement)termElement).getWord();
			int documentCount = frequenciesElementList.getSingleCountsOfWord(word);
			if (documentCount > 0) {
				WordResultElement documentFrequencyElement = new WordResultElement(DOCUMENT_FREQUENCY);
				documentFrequencyElement.setWord(word);
				documentFrequencyElement.setCount(documentCount);
				booleanFrequenciesElement.appendChild(documentFrequencyElement);
			}
		}
		return booleanFrequenciesElement;
	}
	
	/** NYI
	 * 
	 * @param wordArgProcessor
	 * @param frequenciesElementList
	 * @return
	 */
	WordResultsElement createTFIDFFrequencies(AMIArgProcessor wordArgProcessor, WordResultsElementList frequenciesElementList) {
//		WordResultsElement aggregatedFrequenciesElement = createAggregatedFrequenciesElement(frequenciesElementList);
		WordResultsElement booleanFrequencyElement = new WordResultsElement(TFIDF_FREQUENCIES);
//		for (ResultElement termElement : aggregatedFrequenciesElement) {
//			String word = ((WordResultElement)termElement).getWord();
//			int documentCount = frequenciesElementList.getSingleCountsOfWord(word);
//			if (documentCount > 0) {
//				WordResultElement documentFrequencyElement = new WordResultElement(TFIDF_FREQUENCY);
//				documentFrequencyElement.setWord(word);
//				documentFrequencyElement.setCount(documentCount);
//				booleanFrequencyElement.appendChild(documentFrequencyElement);
//			}
//		}
		return booleanFrequencyElement;
	}
	
}
