package org.xmlcml.ami.plugin.plugins.regex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.EIC;
import org.xmlcml.ami.plugin.result.SimpleResultList;
import org.xmlcml.html.HtmlElement;

public class RegexSearcher {

	
	private static final Logger LOG = Logger.getLogger(RegexSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	List<RegexComponent> componentList;
	private List<String> regexFiles;

	private List<CompoundRegexList> regexContainerList;
	private RegexArgProcessor regexArgProcessor;
	private CompoundRegexList regexContainer;
	private HtmlElement scholarlyHtml;
	protected SimpleResultList resultList;
	Element resultElement;


	public RegexSearcher(RegexArgProcessor regexArgProcessor) {
		this.regexArgProcessor = regexArgProcessor;
	}

	public void search(HtmlElement schHtml) {
		this.scholarlyHtml = schHtml;
		ensureRegexList();
		regexContainerList = regexArgProcessor.getRegexContainerList();
		for (CompoundRegexList regexContainer : regexContainerList) {
			this.regexContainer = regexContainer;
			searchXomElement(scholarlyHtml);
			addComponentListToResults();
		}
	}
	private void addComponentListToResults() {
		for (RegexComponent regexComponent : componentList) {
			Element element = regexComponent.createElement();
			resultElement.appendChild(element);
		}
	}

	private CompoundRegexList addRegexFiles(List<String> regexRoots) {
		List<File> regexFiles = new ArrayList<File>();
		for (String regexRoot : regexRoots) {
			File regexFile = new File(regexRoot);
			if (regexFile.exists() && !regexFile.isDirectory()) {
				regexFiles.add(regexFile);
			} else {
				throw new RuntimeException("Cannot find regexFile: "+regexFile);
			}
		}
		for (File regexFile : regexFiles) {
			regexContainer.readCompoundRegexFile(regexFile);
		}
		LOG.trace("regex container "+regexContainer.getCompoundRegexList());
		return regexContainer;
	}

	private void ensureRegexList() {
		regexContainer.ensureCompoundRegexList();
	}

	private void ensureComponentList() {
		if (componentList == null) {
			componentList = new ArrayList<RegexComponent>();
		}
	}

	public CompoundRegex getCompoundRegex(String title) {
		return regexContainer.getCompoundRegexByTitle(title);
	}

	public List<CompoundRegex> getCompoundRegexList() {
		return regexContainer.getCompoundRegexList();
	}


	// ====== args ========

	private void searchXomElement(Element xomElement) {
	//	ensureResultList(sourceElement);
		LOG.debug("search XomElement with "+regexContainer.getCompoundRegexList().size()+" compoundRegexes");
		for (CompoundRegex compoundRegex : regexContainer.getCompoundRegexList()) {
			List<RegexResultElement> regexResultList = searchWithRegexComponents(compoundRegex, xomElement);
			for (RegexResultElement regexResult : regexResultList) {
				resultList.add(regexResult.getSimpleResult());
			}
		}
		LOG.trace("MADE RESULT LIST: "+resultList.size());
		LOG.trace("RESULT "+resultList.toString());
		return;
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
		LOG.debug("regex list "+regexContainer.getCompoundRegexList());
		for (CompoundRegex compoundRegex : regexContainer.getCompoundRegexList()) {
//			compoundRegex.debug();
			LOG.debug(compoundRegex.getTitle()+"/"+compoundRegex.getRegexValues().size());
		}
	}

}
