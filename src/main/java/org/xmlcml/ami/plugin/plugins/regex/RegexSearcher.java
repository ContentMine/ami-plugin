package org.xmlcml.ami.plugin.plugins.regex;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;

public class RegexSearcher {

	
	private static final Logger LOG = Logger.getLogger(RegexSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	List<RegexComponent> componentList;

	private CompoundRegex compoundRegex;
	Element resultElement;


	public RegexSearcher(CompoundRegex compoundRegex) {
		this.compoundRegex = compoundRegex;
	}

	// ====== args ========

	private ResultsElement searchXomElement(Element xomElement) {
		List<RegexComponent> regexComponents = compoundRegex.getOrCreateRegexComponentList();
		ResultsElement resultsElement = new ResultsElement();
		for (RegexComponent regexComponent : regexComponents) {
			MatcherResult matcherResult = regexComponent.searchWithPattern(xomElement.getValue()); // crude to start with
			List<ResultElement> resultElementList = matcherResult.createResultElementList();
			for (ResultElement resultElement : resultElementList) {
				resultsElement.appendChild(resultElement);
			}
		}
		return resultsElement;
	}


	// ===============
	
	public void debug() {
		LOG.debug(compoundRegex.getTitle()+"/"+compoundRegex.getRegexValues().size());
	}

	ResultsElement search(List<HtmlP> pElements) {
		ResultsElement resultsElement = new ResultsElement();
		for (HtmlP pElement : pElements) {
			ResultsElement subResultsElement = this.searchXomElement(pElement);
			resultsElement.transferResultElements(subResultsElement);
		}
		return resultsElement;
	}

}
