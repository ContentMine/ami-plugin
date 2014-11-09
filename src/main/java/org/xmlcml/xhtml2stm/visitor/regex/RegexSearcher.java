package org.xmlcml.xhtml2stm.visitor.regex;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.ResultList;
import org.xmlcml.xhtml2stm.result.ResultsElement;
import org.xmlcml.xhtml2stm.visitable.html.HtmlContainer;
import org.xmlcml.xhtml2stm.visitable.xml.XMLContainer;
import org.xmlcml.xhtml2stm.visitable.xml.XMLVisitable;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;
import org.xmlcml.xhtml2stm.visitor.ArgProcessor;
import org.xmlcml.xhtml2stm.visitor.AbstractSearcher;
import org.xmlcml.xhtml2stm.visitor.SimpleListElement;

public class RegexSearcher extends AbstractSearcher {

	private final static Logger LOG = Logger.getLogger(RegexSearcher.class);
	
	private final static File REGEX_DIRECTORY_BASE = new File("src/main/resources/org/xmlcml/xhtml2stm/visitor/regex");
	private final static String REGEX_SUFFIX = ".xml";
	
	private static final String G          = "-g";
	private static final String REGEX      = "--regex";

	private Map<RegexComponent, Integer> totalCountMap;
	private RegexContainer regexContainer;
	private List<String> regexFiles;

	public RegexSearcher(AbstractVisitor visitor) {
		super(visitor);
		setDefaults();
	}
	
	@Override
	protected void search(HtmlContainer htmlContainer) {
		searchXomElement(htmlContainer.getHtmlElement());
		debugCountMap();
	}

	@Override
	public void search(XMLContainer xmlContainer) {
		ensureRegexList();
//		LOG.debug("visiting XML of "+xmlVisitable.getXMLContainerList().size() + " visitables");
//		for (XMLContainer xmlContainer : xmlVisitable.getXMLContainerList()) {
			LOG.debug("visiting container with  "+(regexContainer.getCompoundRegexList() == null ?
					"null/zero" : regexContainer.getCompoundRegexList().size())+" compound regexes");
			if (regexContainer.getCompoundRegexList() != null) {
				searchXomElement(xmlContainer.getElement());
				debugCountMap();
			}
//		}
//		if (totalCountMap.size() > 0) {
//			resultsElement = new ResultsElement();
//			addCountMapToResults(totalCountMap);
//		}
	}

	private void addCountMapToResults(Map<RegexComponent, Integer> countMap) {
		for (RegexComponent regexComponent : countMap.keySet()) {
			Element element = regexComponent.toXML();
			resultsElement.appendChild(element);
		}
		
	}



	private void setDefaults() {
		regexContainer = new RegexContainer();
	}
	
	protected AbstractListElement createListElement(ResultList resultSet) {
		return new SimpleListElement((ResultList)resultSet);
	}
	
	void searchContainer(XMLContainer xmlContainer) {
		ensureRegexList();
		searchXomElement(xmlContainer.getElement());
		debugCountMap();
		if (totalCountMap.size() > 0) {
			resultsElement = new ResultsElement();
			addCountMapToResults(totalCountMap);
		}
	}

//	private void addCountMapToResults(Map<RegexComponent, Integer> countMap) {
//		for (RegexComponent regexComponent : countMap.keySet()) {
//			Element element = regexComponent.toXML();
//			resultsElement.appendChild(element);
//		}
//	}
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
		LOG.debug("regex container "+regexContainer.getCompoundRegexList());
		return regexContainer;
	}

	private void ensureRegexList() {
		regexContainer.ensureCompoundRegexList();
	}

	private void ensureTotalCountMap() {
		if (totalCountMap == null) {
			totalCountMap = new HashMap<RegexComponent, Integer>();
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
		debugCountMap();
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

	private void debugCountMap() {
		ensureTotalCountMap();
		for (RegexComponent regexComponent : totalCountMap.keySet()) {
			Integer count = totalCountMap.get(regexComponent);
			LOG.debug(regexComponent.getPattern()+": "+count);
		}
	}

	private void searchXomElement(Element element) {
		LOG.debug("search XomElement with "+regexContainer.getCompoundRegexList().size()+" compoundRegexes");
		for (CompoundRegex compoundRegex : regexContainer.getCompoundRegexList()) {
			RegexResults regexResults = compoundRegex.searchWithRegexComponents(element);
			//regexResults.debug();
			Map<RegexComponent, Integer> countMap = regexResults.getCountMap();
			ensureTotalCountMap();
			LOG.debug("Hits: "+countMap.keySet().size());
			for (RegexComponent regexComponent : countMap.keySet()) {
				recordResults(countMap, regexComponent);
			}
		}
	}

	private void recordResults(Map<RegexComponent, Integer> countMap,
			RegexComponent regexComponent) {
		Integer count = countMap.get(regexComponent);
		if (count != null && count != 0) {
			Integer oldCount = totalCountMap.get(regexComponent);
			oldCount = oldCount == null ? 0 : oldCount;
			totalCountMap.put(regexComponent, oldCount + count);
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
