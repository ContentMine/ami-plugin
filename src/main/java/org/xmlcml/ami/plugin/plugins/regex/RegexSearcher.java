package org.xmlcml.ami.plugin.plugins.regex;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.EIC;
import org.xmlcml.ami.plugin.result.ResultElement;
import org.xmlcml.ami.plugin.result.ResultsElement;
import org.xmlcml.ami.plugin.result.SimpleResultList;
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
	protected SimpleResultList resultList;
	Element resultElement;


	public RegexSearcher(RegexArgProcessor regexArgProcessor, CompoundRegex compoundRegex) {
		this.regexArgProcessor = regexArgProcessor;
		this.compoundRegex = compoundRegex;
	}

//	public ResultsElement search(HtmlElement schHtml) {
//		this.scholarlyHtml = schHtml;
//		searchXomElement(scholarlyHtml);
//		addComponentListToResults();
//		return null;
//	}
	
	private void addComponentListToResults() {
		for (RegexComponent regexComponent : componentList) {
			Element element = regexComponent.createElement();
			resultElement.appendChild(element);
		}
	}

//	private CompoundRegexList addRegexFiles(List<String> regexRoots) {
//		List<File> regexFiles = new ArrayList<File>();
//		for (String regexRoot : regexRoots) {
//			File regexFile = new File(regexRoot);
//			if (regexFile.exists() && !regexFile.isDirectory()) {
//				regexFiles.add(regexFile);
//			} else {
//				throw new RuntimeException("Cannot find regexFile: "+regexFile);
//			}
//		}
//		for (File regexFile : regexFiles) {
//			compoundRegex.readCompoundRegexFile(regexFile);
//		}
//		LOG.trace("regex container "+compoundRegex.getCompoundRegexList());
//		return compoundRegex;
//	}

//	private void ensureRegexList() {
//		compoundRegex.ensureCompoundRegexList();
//	}

//	private void ensureComponentList() {
//		if (componentList == null) {
//			componentList = new ArrayList<RegexComponent>();
//		}
//	}


	// ====== args ========

	private ResultsElement searchXomElement(Element xomElement) {
		ResultsElement resultsElement = searchWithRegexComponents1(compoundRegex, xomElement);
		return resultsElement;
	}

	private void ensureResultList() {
		// TODO Auto-generated method stub
		
	}

	private ResultsElement searchWithRegexComponents1(CompoundRegex compoundRegex, Element element) {
		List<RegexComponent> regexComponents = compoundRegex.getOrCreateRegexComponentList();
		ResultsElement resultsElement = new ResultsElement();
		for (RegexComponent regexComponent : regexComponents) {
			MatcherResult matcherResult = regexComponent.searchWithPattern(element.getValue()); // crude to start with
			List<ResultElement> resultElementList = matcherResult.createResultElementList();
			for (ResultElement resultElement : resultElementList) {
				resultsElement.appendChild(resultElement);
			}
		}
		return resultsElement;
	}

	private List<RegexResultElement> searchWithRegexComponents(CompoundRegex compoundRegex, Element element) {
		LOG.trace("Searching element with regexComponentList: "+element);
		List<RegexResultElement> regexResultList = new ArrayList<RegexResultElement>();
		if (element != null) {
			EIC eic = new EIC(element);
			List<RegexComponent> regexComponentList = compoundRegex.getOrCreateRegexComponentList();
			searchElementInContext(eic, regexResultList, regexComponentList);
		} else {
			LOG.error("null html element");
		}
		return regexResultList;
	}

//	private void searchElement(List<RegexResultElement> regexResultList, List<RegexComponent> regexComponentList) {
//		RegexResultElement regexResult = null;
//		List<RegexComponent> regexComponents = compoundRegex.getOrCreateRegexComponentList();
//		for (RegexComponent regexComponent : regexComponents) {
//			LOG.trace("with: "+regexComponent);
//			MatcherResult matcherResult = regexComponent.searchWithPattern(eic);
//			LOG.trace("finished");
//			if (matcherResult.size() > 0) {
//				regexResult = new RegexResultElement(regexComponent, matcherResult);
////				regexResult.addLineNumberAttribute(eic);
////				regexResult.addLineValueAttribute(eic);
//				regexResultList.add(regexResult);
//			}
//		}
//	}

	private void searchElementInContext(EIC eic,
			List<RegexResultElement> regexResultList,
			List<RegexComponent> regexComponentList) {
		RegexResultElement regexResult = null;
//		int hits = 0;
		for (RegexComponent regexComponent : regexComponentList) {
			LOG.trace("with: "+regexComponent);
			MatcherResult matcherResult = regexComponent.searchWithPatterns(eic);
			LOG.trace("finished");
			if (matcherResult.size() > 0) {
				regexResult = new RegexResultElement(regexComponent, matcherResult);
//				regexResult.addLineNumberAttribute(eic);
//				regexResult.addLineValueAttribute(eic);
				regexResultList.add(regexResult);
			}
		}
	}

	

	// ===============
	
	public void debug() {
		LOG.debug(compoundRegex.getTitle()+"/"+compoundRegex.getRegexValues().size());
	}

	public ResultsElement search(List<HtmlP> pElements) {
		ResultsElement resultsElement = new ResultsElement();
//		int i = 0;
		for (HtmlP pElement : pElements) {
//			LOG.trace(i++);
			ResultsElement subResultsElement = this.searchXomElement(pElement);
			resultsElement.transferResultElements(subResultsElement);
		}
		return resultsElement;
	}

}
