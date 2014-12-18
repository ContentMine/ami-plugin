package org.xmlcml.ami.visitor.regex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.visitable.SourceElement;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.ami.visitable.txt.TextContainer;
import org.xmlcml.ami.visitable.xml.XMLContainer;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.ArgProcessor;
import org.xmlcml.ami.visitor.EIC;
import org.xmlcml.ami.visitor.SimpleListElement;

public class RegexSearcher extends AbstractSearcher {

	public final static Logger LOG = Logger.getLogger(RegexSearcher.class);
	
	private static final String G          = "-g";
	private static final String REGEX      = "--regex";

	List<RegexComponent> componentList;
	private RegexContainer regexContainer;
	private List<String> regexFiles;

	public RegexSearcher(RegexVisitor visitor) {
		super(visitor);
		setDefaults();
		this.regexContainer = visitor.getRegexContainer();
	}

	@Override
	public void search(HtmlContainer htmlContainer) {
		Element element = htmlContainer.getHtmlElement();
		if (element != null) {
			searchXomElement(element);
		} else {
			LOG.error("NULL HTML element");
		}
	}

	@Override
	public void search(XMLContainer xmlContainer) {
		ensureRegexList();
		LOG.debug("visiting container with  "+(regexContainer.getCompoundRegexList() == null ?
				"null/zero" : regexContainer.getCompoundRegexList().size())+" compound regexes");
		if (regexContainer.getCompoundRegexList() != null) {
			searchXomElement(xmlContainer.getElement());
		}
		LOG.trace("RESULT LIST... "+resultList.size());
		return;
	}

	@Override
	public void search(TextContainer textContainer) {
		searchLineList(textContainer.getLineList());
	}

	private void addComponentListToResults() {
		for (RegexComponent regexComponent : componentList) {
			Element element = regexComponent.createElement();
			resultElement.appendChild(element);
		}
	}

	private void setDefaults() {
		regexContainer = new RegexContainer();
	}
	
	protected AbstractListElement createListElement(SimpleResultList resultSet) {
		return new SimpleListElement((SimpleResultList)resultSet);
	}
	
	private void searchContainer(XMLContainer xmlContainer) {
		ensureRegexList();
		searchXomElement(xmlContainer.getElement());
		addComponentListToResults();
	}

	public void addRegexFile(String string) {
		// TODO Auto-generated method stub
		
	}

	private RegexContainer addRegexFiles(List<String> regexRoots) {
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

	protected void searchContainer(HtmlContainer htmlContainer) {
		this.searchXomElement(htmlContainer.getHtmlElement());
//		debugCountMap();
	}

	// ====== args ========
	protected List<String> extractArgs(ListIterator<String> listIterator) {
		List<String> argList = new ArrayList<String>();
		while (listIterator.hasNext()) {
			String next = listIterator.next();
			if (next.startsWith(ArgProcessor.MINUS)) {
				listIterator.previous();
				break;
			}
			argList.add(next);
		}
		return argList;
	}

	/**
	 * 
	 */
	protected boolean processArg(String arg, ListIterator<String> listIterator) {
		boolean processed = false;
		if (G.equals(arg) || REGEX.equals(arg)) {
			regexFiles = extractArgs(listIterator);
			addRegexFiles(regexFiles);
			processed = true;
		}
		return processed;
	}

	private void searchXomElement(Element xomElement) {
		ensureResultList(sourceElement);
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
			List<RegexComponent> regexComponentList = compoundRegex.getRegexComponentList();
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
				regexResult.addLineNumberAttribute(eic);
				regexResult.addLineValueAttribute(eic);
				regexResultList.add(regexResult);
			}
		}
	}

	private void searchLineList(List<String> lineList) {
		ensureResultList(sourceElement);
		LOG.trace("search XomElement with "+regexContainer.getCompoundRegexList().size()+" compoundRegexes");
		for (CompoundRegex compoundRegex : regexContainer.getCompoundRegexList()) {
			List<RegexResultElement> regexResultList = searchWithRegexComponents(compoundRegex, lineList);
			for (RegexResultElement regexResult : regexResultList) {
				resultList.add(regexResult.getSimpleResult());
			}
		}
		LOG.debug("MADE RESULT LIST: "+resultList.size());
		return;
	}

	private List<RegexResultElement> searchWithRegexComponents(CompoundRegex compoundRegex, List<String> lineList) {
		LOG.debug("Searching lineList with regexComponentList");
		
		List<RegexResultElement> regexResultList = new ArrayList<RegexResultElement>();
		List<RegexComponent> regexComponentList = compoundRegex.getRegexComponentList();
		
		for (int iline = 0; iline < lineList.size(); iline++) {
			EIC eic = new EIC(lineList, iline);
			searchElementInContext(eic, regexResultList, regexComponentList);
		}
		return regexResultList;
	}

	private void ensureResultList(SourceElement sourceElement) {
		if (resultList == null) {
			resultList = new SimpleResultList(sourceElement);
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
