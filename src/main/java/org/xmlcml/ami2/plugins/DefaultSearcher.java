package org.xmlcml.ami2.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.lookups.AbstractLookup;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlP;

public class DefaultSearcher {

	
	private static final String NOT_FOUND = "NOT_FOUND";
	private static final Logger LOG = Logger.getLogger(DefaultSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected Pattern pattern;
	private AMIArgProcessor argProcessor;
	protected Integer[] contextCounts;
	private String match;
	private AbstractLookup lookup;

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

	protected ResultElement createResultElement(String value, Matcher matcher) {
		ResultElement resultElement = new ResultElement();
		matchAndAddPrePost(value, matcher, resultElement);
		return resultElement;
	}

	protected void matchAndAddPrePost(String value, Matcher matcher,
			ResultElement resultElement) {
		match = matcher.group(0);
		int preEnd = matcher.start();
		int preStart = Math.max(0, preEnd - contextCounts[0]);
		int postStart = matcher.end();
		int postEnd = Math.min(value.length(), postStart + contextCounts[1]);
		resultElement.setPre(flattenTags(value.substring(preStart, preEnd)));
		match = flattenTags(match);
		resultElement.setMatch(match);
		resultElement.setPost(flattenTags(value.substring(postStart, postEnd)));
		lookupMatchAndAddLookupRefs(resultElement);
	}

	private void lookupMatchAndAddLookupRefs(ResultElement resultElement) {
		Map<String, AbstractLookup> lookupInstanceByName = argProcessor.getOrCreateLookupInstanceByName();
		for (String lookupName : lookupInstanceByName.keySet()) {
			AbstractLookup lookup = lookupInstanceByName.get(lookupName);
			Map<String, String> lookupRefByMatch = lookup.getOrCreateLookupRefByMatch();
			String lookupRef = lookupRefByMatch.get(match);
			if (lookupRef == null) {
				try {
					lookupRef = lookup.lookup(match);
				} catch (IOException e) {
					LOG.debug("Cannot find match: "+match+" in "+lookupName);
				}
				lookupRef = lookupRef == null ? NOT_FOUND : lookupRef;
				lookupRefByMatch.put(match,  lookupRef);
			}
			if (!(NOT_FOUND.equals(lookupRef))) {
				resultElement.addAttribute(new Attribute(lookupName, lookupRef));
			}
		}
	}
	
	protected String flattenTags(String s) {
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
