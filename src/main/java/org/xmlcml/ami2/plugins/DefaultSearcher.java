package org.xmlcml.ami2.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlP;

public class DefaultSearcher {

	
	private static final Logger LOG = Logger.getLogger(DefaultSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected Pattern pattern;
	private AMIArgProcessor argProcessor;
	protected Integer[] contextCounts;

	public DefaultSearcher(AMIArgProcessor argProcessor, Pattern pattern) {
		this.pattern = pattern;
		this.argProcessor = argProcessor;
		contextCounts = argProcessor.getContextCount();
	}

	/** create resultsElement.
	 * 
	 * May be empy if no hits
	 * 
	 * @param xomElement
	 * @return
	 */
	protected ResultsElement searchXomElement(Element xomElement) {
		ResultsElement resultsElement = new ResultsElement();
		List<ResultElement> resultElementList = search(xomElement.getValue()); // crude to start with
		for (ResultElement resultElement : resultElementList) {
			resultsElement.appendChild(resultElement);
		}
		return resultsElement;
	}

	protected List<ResultElement> search(String value) {
		List<ResultElement> resultElementList = new ArrayList<ResultElement>();
		Matcher matcher = pattern.matcher(value);
		int start = 0;
		while (matcher.find(start)) {
			ResultElement resultElement = createResultElement(value, matcher);
			resultElementList.add(resultElement);
			start = matcher.end();
		}
		return resultElementList;
	}

	private ResultElement createResultElement(String value, Matcher matcher) {
		ResultElement resultElement = new ResultElement();
		String match = matcher.group(0);
		int preEnd = matcher.start();
		int preStart = Math.max(0, preEnd - contextCounts[0]);
		int postStart = matcher.end();
		int postEnd = Math.min(value.length(), postStart + contextCounts[1]);
		resultElement.setPre(unmark(value.substring(preStart, preEnd)));
		resultElement.setMatch(unmark(match));
		resultElement.setPost(unmark(value.substring(postStart, postEnd)));
		return resultElement;
	}
	
	protected String unmark(String s) {
		return s;
	}

	public ResultsElement search(List<HtmlP> pElements) {
		ResultsElement resultsElement = new ResultsElement();
		for (HtmlP pElement : pElements) {
			ResultsElement subResultsElement = this.searchXomElement(pElement);
			if (subResultsElement.size() > 0) {
				resultsElement.transferResultElements(subResultsElement);
			}
		}
		return resultsElement;
	}

}
