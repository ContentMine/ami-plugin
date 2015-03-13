package org.xmlcml.ami2.plugins.words;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;

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
	private static final String COUNT = "count";
	private static final String LENGTH = "length";
	private static final String LENGTHS = "lengths";
	private static final String FREQUENCIES = "frequencies";
	private static final String FREQUENCY = "frequency";
	private static final String WORD = "word";
	private static final String PROPERTY = "property";
	
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
		ResultsElement element = getWordlengthFrequency();
		wordArgProcessor.addResultsElement(element);
	}

	private void createWordFrequencies() {
		ResultsElement element = getWordFrequency();
		wordArgProcessor.addResultsElement(element);
	}

	private void createWordSets() {
		stopwords = WordSetWrapper.getCommonEnglishStopwordSet();
		List<String> rawWords = wordArgProcessor.extractWordsFromScholarlyHtml();
		currentWords = createUnstoppedWords(rawWords);
		if (wordArgProcessor.getChosenWordTypes().contains(WordArgProcessor.ABBREVIATIONS)) {
			currentWords = createAbbreviations();
		} else if (wordArgProcessor.getChosenWordTypes().contains(WordArgProcessor.CAPITALIZED)) {
			currentWords = createCapitalized();
		} else {
			if (wordArgProcessor.getWordCaseList().contains(WordArgProcessor.IGNORE)) {
				currentWords = toLowerCase(currentWords);
			}
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

	private List<String> createUnstoppedWords(List<String> rawWords) {
		currentWords = new ArrayList<String>();
		for (String word : rawWords) {
			word = word.trim();
			if (!stopwords.contains(word)) {
				currentWords.add(word);
			}
		}
		LOG.trace("current words: "+currentWords.size());
		return currentWords;
	}
	
	private List<String> toLowerCase(List<String> words) {
		ArrayList<String> newList = new ArrayList<String>();
		for (String word : words) {
			newList.add(word.toLowerCase());
		}
		return newList;
	}

	private ResultsElement getWordlengthFrequency() {
		Multiset<Integer> lengthSet = HashMultiset.create();
		for (String word : currentWords) {
			lengthSet.add(word.length());
		}
		ResultsElement lengthsElement = new ResultsElement(LENGTHS);
		for (Entry<Integer> entry : lengthSet.entrySet()) {
			ResultElement lengthElement = new ResultElement(LENGTH);
			lengthElement.addAttribute(new Attribute(LENGTH, String.valueOf(entry.getElement().intValue())));
			lengthElement.addAttribute(new Attribute(COUNT, ""+entry.getCount()));
			lengthsElement.appendChild(lengthElement);
		}
		return lengthsElement;
	}
	
	private ResultsElement getWordFrequency() {
		Multiset<String> wordSet = HashMultiset.create();
		for (String rawWord : currentWords) {
//			rawWord = rawWord.toLowerCase(); // normalize case
			rawWord = rawWord.replaceAll("[\\d+]", ""); // remove numbers
			if (!stopwords.contains(rawWord.toLowerCase()) 
					&& rawWord.length() >= minRawWordLength 
					&& rawWord.length() <= maxRawWordLength) { //remove stopwords and short strings
				wordSet.add(rawWord);
			}
		}		
		entriesSortedByCount = Multisets.copyHighestCountFirst(wordSet).entrySet();
		entriesSortedByValue = ImmutableSortedMultiset.copyOf(wordSet).entrySet();
		Iterable<Multiset.Entry<String>> sortedEntries = getSortedEntries();
		
		ResultsElement lengthsElement = new ResultsElement(FREQUENCIES);
		for (Entry<String> entry : sortedEntries) {
			int count = +entry.getCount();
			if (count < minCountInSet) continue;
			ResultElement lengthElement = new ResultElement(FREQUENCY);
			lengthElement.addAttribute(new Attribute(WORD, String.valueOf(entry.getElement())));
			lengthElement.addAttribute(new Attribute(COUNT, String.valueOf(count)));
			lengthsElement.appendChild(lengthElement);
		}
		return lengthsElement;
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
}
