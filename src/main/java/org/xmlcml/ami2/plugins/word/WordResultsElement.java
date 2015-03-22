package org.xmlcml.ami2.plugins.word;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.xml.XMLUtil;

public class WordResultsElement extends ResultsElement {

	
	private static final Logger LOG = Logger
			.getLogger(WordResultsElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private Set<String> wordSet;

	public WordResultsElement(String title) {
		super(title);
	}

	public WordResultsElement(ResultsElement resultsElement) {
		if (resultsElement == null) {
			throw new RuntimeException("Null ResultsElement");
		}
		XMLUtil.copyAttributesFromTo(resultsElement, this);
		for (ResultElement resultElement : resultsElement) {
			WordResultElement wordResultElement = new WordResultElement(resultElement);
			this.appendChild(wordResultElement);
		}
	}

	private void ensureSet() {
		getOrCreateResultElementList();
		wordSet = new HashSet<String>();
		for (ResultElement resultElement : resultElementList) {
			String word = ((WordResultElement) resultElement).getWord();
			wordSet.add(word);
		}
	}

	public boolean contains(String word) {
		ensureSet();
		return wordSet.contains(word);
	}

	public IntArray getCountArray() {
		getOrCreateResultElementList();
		IntArray countArray = new IntArray();
		for (ResultElement resultElement : resultElementList) {
			WordResultElement wordResultElement = (WordResultElement) resultElement;
			countArray.addElement(wordResultElement.getCount());
		}
		return countArray;
	}


}
