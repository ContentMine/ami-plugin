package org.xmlcml.ami2.plugins.word;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.AMIPlugin;

import com.google.common.collect.Multiset;

/** a wrapper for a set of words.
 * 
 * Can hold both  Set<String> and Multiset<String>
 * 
 * @author pm286
 *
 */
public class WordSetWrapper {

	
	private static final String XML = ".xml";
	private static final Logger LOG = Logger.getLogger(WordSetWrapper.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static WordSetWrapper COMMON_ENGLISH_STOPWORDS;
	public static final String COMMON_ENGLISH_STOPWORDS_TXT = AMIPlugin.ORG_XMLCML_AMI_PLUGIN+"word/stopwords.txt";
	
	private Set<String> wordSet;
	private Multiset<String> multiset;

	public WordSetWrapper() {
	}
	
	public WordSetWrapper(Set<String> wordSet) {
		this();
		this.wordSet = wordSet;
	}
	
	public Set<String> getWordSet() {
		return wordSet;
	}

	public void setWordSet(Set<String> wordSet) {
		this.wordSet = wordSet;
	}

	public Multiset<String> getMultiset() {
		return multiset;
	}

	public void setMultiset(Multiset<String> multiset) {
		this.multiset = multiset;
	}

	public WordSetWrapper(Multiset<String> multiset) {
		this();
		this.multiset = multiset;
	}

	public static WordSetWrapper createStopwordSet(String stopwordLocation) {
		WordSetWrapper stopwordSet = null;
		Set<String> stopwords = getStopwords(stopwordLocation); 
		if (stopwords != null) {
			stopwordSet = new WordSetWrapper(stopwords);
		}
		return stopwordSet;
	}

	public static WordSetWrapper getCommonEnglishStopwordSet() {
		if (COMMON_ENGLISH_STOPWORDS == null) {
			Set<String> stopwords = getStopwords(COMMON_ENGLISH_STOPWORDS_TXT);
			COMMON_ENGLISH_STOPWORDS = new WordSetWrapper(stopwords);
		}
		return COMMON_ENGLISH_STOPWORDS;
	}
	
	private static Set<String> getStopwords(String stopwordsResource) {
		Set<String> stopwords0 = new HashSet<String>();
		InputStream stopwordsStream = AMIArgProcessor.class.getResourceAsStream(stopwordsResource);
		if (stopwordsStream == null) {
			LOG.debug("Cannot read stopword stream: "+stopwordsResource);
		} else if (stopwordsResource.endsWith(XML)){
		} else {
			try {
				List<String> lines = IOUtils.readLines(stopwordsStream);
				for (String line : lines) {
					stopwords0.add(line.trim());
				}
			} catch (IOException e) {
				throw new RuntimeException("cannot find stopwords "+stopwordsResource);
			}
		}
		LOG.trace("stopword set: "+stopwords0.size());
		return stopwords0;
	}

	public boolean contains(String word) {
		return wordSet == null ? false : wordSet.contains(word);
	}

	public int size() {
		return wordSet == null ? 0 : wordSet.size();
	}


}
