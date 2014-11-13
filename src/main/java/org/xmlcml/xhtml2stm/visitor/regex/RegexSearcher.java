package org.xmlcml.xhtml2stm.visitor.regex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.SimpleResultList;
import org.xmlcml.xhtml2stm.visitable.SourceElement;
import org.xmlcml.xhtml2stm.visitable.html.HtmlContainer;
import org.xmlcml.xhtml2stm.visitable.xml.XMLContainer;
import org.xmlcml.xhtml2stm.visitor.AbstractSearcher;
import org.xmlcml.xhtml2stm.visitor.ArgProcessor;
import org.xmlcml.xhtml2stm.visitor.ElementInContext;
import org.xmlcml.xhtml2stm.visitor.SimpleListElement;

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
		searchXomElement(htmlContainer.getHtmlElement());
	}

	@Override
	public void search(XMLContainer xmlContainer) {
		ensureRegexList();
		LOG.trace("visiting container with  "+(regexContainer.getCompoundRegexList() == null ?
				"null/zero" : regexContainer.getCompoundRegexList().size())+" compound regexes");
		if (regexContainer.getCompoundRegexList() != null) {
			searchXomElement(xmlContainer.getElement());
//			debugCountMap();
		}
		return;
	}

	private void addComponentListToResults() {
		for (RegexComponent regexComponent : componentList) {
			Element element = regexComponent.createElement();
			resultsElement.appendChild(element);
		}
	}

	private void setDefaults() {
		regexContainer = new RegexContainer();
	}
	
	protected AbstractListElement createListElement(SimpleResultList resultSet) {
		return new SimpleListElement((SimpleResultList)resultSet);
	}
	
	void searchContainer(XMLContainer xmlContainer) {
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
		LOG.trace("search XomElement with "+regexContainer.getCompoundRegexList().size()+" compoundRegexes");
		for (CompoundRegex compoundRegex : regexContainer.getCompoundRegexList()) {
			List<RegexResultElement> regexResultList = searchWithRegexComponents(compoundRegex, xomElement);
			for (RegexResultElement regexResult : regexResultList) {
				resultList.add(regexResult.getSimpleResult());
			}
		}
		LOG.debug("MADE RESULT LIST: "+resultList.size());
		return;
	}

	private List<RegexResultElement> searchWithRegexComponents(CompoundRegex compoundRegex, Element element) {
		LOG.debug("Searching element with regexComponentList");
		ElementInContext eic = new ElementInContext(element);
		List<RegexResultElement> regexResultList = new ArrayList<RegexResultElement>();
		List<RegexComponent> regexComponentList = compoundRegex.getRegexComponentList();
		for (RegexComponent regexComponent : regexComponentList) {
			LOG.debug("with: "+regexComponent);
			MatcherResult matcherResult = regexComponent.searchWithPatterns(eic);
			LOG.debug("finished");
			if (matcherResult.size() > 0) {
				RegexResultElement regexResult = new RegexResultElement(regexComponent, matcherResult);
				regexResultList.add(regexResult);
			}
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
