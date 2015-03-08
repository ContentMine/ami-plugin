package org.xmlcml.ami.plugin.plugins.regex;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.result.ResultElement;
import org.xmlcml.ami.plugin.result.ResultsElement;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlP;

public class RegexSearcher {

	
	private static final Logger LOG = Logger.getLogger(RegexSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	List<RegexComponent> componentList;
	private List<String> regexFiles;

	private RegexArgProcessor regexArgProcessor;
	private CompoundRegex compoundRegex;
	private HtmlElement scholarlyHtml;
	Element resultElement;


	public RegexSearcher(RegexArgProcessor regexArgProcessor, CompoundRegex compoundRegex) {
		this.regexArgProcessor = regexArgProcessor;
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

	public ResultsElement search(List<HtmlP> pElements) {
		ResultsElement resultsElement = new ResultsElement();
		for (HtmlP pElement : pElements) {
			ResultsElement subResultsElement = this.searchXomElement(pElement);
			resultsElement.transferResultElements(subResultsElement);
		}
		return resultsElement;
	}

}
