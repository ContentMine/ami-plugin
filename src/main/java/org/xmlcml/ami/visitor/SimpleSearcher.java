package org.xmlcml.ami.visitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;
import org.xmlcml.ami.visitable.VisitableContainer;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.ami.visitable.xml.XMLContainer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

public class SimpleSearcher extends AbstractSearcher {

	
	public static final String STOPWORDS_TXT = "/org/xmlcml/ami/visitor/words/stopwords.txt";
	private static final Logger LOG = Logger.getLogger(SimpleSearcher.class);
	private static final int MIN_COUNT = 4;
	private Set<String> stopwords;
	private List<String> rawWords;
	private VisitableContainer container;
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected SimpleSearcher(AbstractVisitor visitor) {
		super(visitor);
	}

	@Override
	protected AbstractListElement createListElement(SimpleResultList resultList) {
		AbstractListElement listElement = new SimpleListElement(resultList);
		return listElement;
	}

	/** very simple demo search counts lengths of words.
	 * 
	 * Do not use for anything serious
	 * 
	 */
	@Override
	protected void search(HtmlContainer htmlContainer) {
		this.container = htmlContainer;
		simpleSearch();
		LOG.trace("RESULT LIST... "+(resultList == null ? "null results" : ""+resultList.size()));
	}

	/** very simple demo search counts lengths of words.
	 * 
	 * Do not use for anything serious
	 * 
	 */
	@Override
	protected void search(XMLContainer xmlContainer) {
		container = xmlContainer;
		simpleSearch();
		LOG.trace("RESULT LIST... "+(resultList == null ? "null results" : ""+resultList.size()));
	}

	private void simpleSearch() {
		AMIArgProcessor amiArgProcessor = abstractVisitor.getArgProcessor();
		List<String> params = amiArgProcessor.getParams();
		if (params != null && params.size() >= 1) {
			if (AMIArgProcessor.WORD_LENGTHS.equals(params.get(0))) {
				createWordLengths();
			} else if (AMIArgProcessor.WORD_FREQUENCIES.equals(params.get(0))) {
				createWordFrequencies();
			}
		} else {
			createWordLengths();
		}
		return;
	}

	private void createWordLengths() {
		splitToWords();
		Element element = getWordlengthFrequency();
		outputResults(element);
	}

	private void createWordFrequencies() {
		splitToWords();
		Element element = getWordFrequency();
		outputResults(element);
	}

	private List<String> splitToWords() {
		stopwords = getStopwords(STOPWORDS_TXT);
		rawWords = new ArrayList<String>();
		String value = container.getElement().getValue().trim();
		String[] words = value.split("\\W\\s*");
		for (String word : words) {
			word = word.trim();
			if (!stopwords.contains(word)) {
				rawWords.add(word);
			}
		}
		return rawWords;
	}

	public static Set<String> getStopwords(String stopwordsResource) {
		InputStream stopwordsStream = SimpleSearcher.class.getResourceAsStream(stopwordsResource);
		Set<String> stopwords0 = new HashSet<String>();
		try {
			List<String> lines = IOUtils.readLines(stopwordsStream);
			for (String line : lines) {
				stopwords0.add(line.trim());
			}
		} catch (IOException e) {
			throw new RuntimeException("cannot find stopwords "+stopwordsResource);
		}
		return stopwords0;
	}

	private void outputResults(Element lengthsElement) {
		ensureResultList(sourceElement);
		SimpleResultWrapper resultsWrapper = new SimpleResultWrapper();
		resultsWrapper.setResultElement(lengthsElement);
		resultList.add(resultsWrapper);
		
		LOG.debug("MADE RESULT LIST: "+resultList.size());
	}

	private Element getWordlengthFrequency() {
		Multiset<Integer> lengthSet = HashMultiset.create();
		for (String word : rawWords) {
			lengthSet.add(word.length());
		}
		Element lengthsElement = new Element("lengths");
		for (Entry<Integer> entry : lengthSet.entrySet()) {
			Element lengthElement = new Element("length");
			lengthElement.addAttribute(new Attribute("length", ""+entry.getElement().intValue()));
			lengthElement.addAttribute(new Attribute("count", ""+entry.getCount()));
			lengthsElement.appendChild(lengthElement);
		}
		return lengthsElement;
	}
	
	private Element getWordFrequency() {
		Multiset<String> wordSet = HashMultiset.create();
		for (String rawWord : rawWords) {
			rawWord = rawWord.toLowerCase(); // normalize case
			rawWord = rawWord.replaceAll("[\\d+]", ""); // remove numbers
			if (!stopwords.contains(rawWord) && rawWord.length() > 3) { //remove stopwords and short strings
				wordSet.add(rawWord);
			}
		}		
		Iterable<Multiset.Entry<String>> entriesSortedByCount = 
			Multisets.copyHighestCountFirst(wordSet).entrySet();
		Iterable<Multiset.Entry<String>> entriesSortedByValue =
			ImmutableSortedMultiset.copyOf(wordSet).entrySet();
		Iterable<Multiset.Entry<String>> sortedEntries = entriesSortedByCount;
//		Iterable<Multiset.Entry<String>> sortedEntries = entriesSortedByValue;
		
		Element lengthsElement = new Element("frequencies");
		for (Entry<String> entry : sortedEntries) {
			int count = +entry.getCount();
			if (count < MIN_COUNT) continue;
			Element lengthElement = new Element("frequency");
			lengthElement.addAttribute(new Attribute("word", ""+entry.getElement()));
			lengthElement.addAttribute(new Attribute("count", ""+count));
			lengthsElement.appendChild(lengthElement);
		}
		return lengthsElement;
	}
}
