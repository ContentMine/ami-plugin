package org.xmlcml.ami.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;
import org.xmlcml.ami.visitable.SourceElement;
import org.xmlcml.xml.XMLUtil;

/** a container for a list of text and elements.
 * 
 * contains a list of EICs and some extra search management.
 * Uses either or both XPath and Patterns to search and add to list
 * can search either one or more XMLElements, or lines of text
 * 
 * @author pm286
 *
 */
public class DocumentFragment {

	private final static Logger LOG = Logger.getLogger(DocumentFragment.class);
	
	private List<EIC> eicList;
	protected List<Pattern> patternList;
	private List<String> xPathList;
	private int maxChar = 100; // maximum allowed by some publishers
	private AbstractSearcher abstractSearcher;
	private SourceElement sourceElement;
	private DocumentFragment resultFragment;
	private SimpleResultList resultList;
	
	public DocumentFragment() {
		ensureLists();
	}
	
	private void ensureLists() {
		if (eicList == null) {
			eicList = new ArrayList<EIC>();
		}
		if (patternList == null) {
			patternList = new ArrayList<Pattern>();
		}
		if (xPathList == null) {
			xPathList = new ArrayList<String>();
		}
	}

	/** creates copy of this but without EICList.
	 * 
	 * copies patternList and xPathList
	 * 
	 * @return
	 */
	private DocumentFragment createTemplateFragment() {
		DocumentFragment fragment = new DocumentFragment();
		fragment.patternList = new ArrayList<Pattern>(this.patternList);
		fragment.xPathList = new ArrayList<String>(this.xPathList);
		return fragment;
	}

	public void setMaxChar(int maxChar) {
		this.maxChar = maxChar;
	}
	
	public int getMaxChar() {
		return maxChar;
	}
	
	public void add(EIC eic) {
		ensureLists();
		eicList.add(eic);
	}
	
	public void addAll(List<EIC> eicList) {
		ensureLists();
		this.eicList.addAll(eicList);
	}

	List<EIC> getEICList() {
		ensureLists();
		return eicList;
	}

	/** sets exactly one pattern.
	 * 
	 * @param pattern
	 */
	public void setPattern(Pattern pattern) {
		patternList = new ArrayList<Pattern>();
		patternList.add(pattern);
	}

	/** adds additional pattern to list.
	 * 
	 * no check for uniqueness.
	 * @param pattern
	 */
	public void addPattern(Pattern pattern) {
		ensurePatternList();
		patternList.add(pattern);
	}

	private void ensurePatternList() {
		if (patternList == null) {
			patternList = new ArrayList<Pattern>();
		}
	}

	public void addXPath(String xPath) {
		ensureXpathList();
		xPathList.add(xPath);
	}

	protected void ensureXpathList() {
		if (xPathList == null) {
			xPathList = new ArrayList<String>();
		}
	}
	
	protected DocumentFragment searchWithXpathsAndPatterns() {
		resultFragment = this.searchWithXPath(sourceElement);
	    resultFragment = resultFragment.searchElementsWithPatterns();
		return resultFragment;
	}
	
	protected DocumentFragment applyXPath(Element rawElement) {
		DocumentFragment resultFragment = this.createTemplateFragment();
		if (xPathList.size() == 0) {
			EIC eic = new EIC(rawElement);
			eic.setResultValue(rawElement.getValue());
			resultFragment.add(eic);
		} else {
			for (String xPath : xPathList) {
				DocumentFragment newFragment = applyXpath(rawElement, xPath);
				resultFragment.addAll(newFragment.getEICList());
			}
		}
		return resultFragment;
	}
	
	/**
	 * Here we describe a new hadrosauroid dinosaur, <italic>Yunganglong
	 * datongensis</italic>
	 * 
	 * @param element
	 * @return
	 */
	public DocumentFragment applyXpath(Element targetElement, String xpath) {
		DocumentFragment resultFragment = this.createTemplateFragment();
		List<Element> resultElements = XMLUtil.getQueryElements(targetElement, xpath);
		for (Element resultElement : resultElements) {
			EIC eic = new EIC(resultElement);
			resultFragment.add(eic);
		}
		return resultFragment;
	}
	
	/** Override in subclasses if special search strategy required.
	 * 
	 * @param sourceElement
	 * @return
	 */
	public SimpleResultList searchXPathPatternAndCollectResults(SourceElement sourceElement) {
		this.sourceElement = sourceElement;
		this.searchWithXpathsAndPatterns();
		resultList = this.createResultListFromResultFragment();
		return resultList;
	}
	
	private SimpleResultList createResultListFromResultFragment() {
		resultList = new SimpleResultList(sourceElement);
		for (EIC eic : resultFragment.getEICList()) {
			SimpleResultWrapper simpleResult = new SimpleResultWrapper(eic);
			resultList.add(simpleResult);
		}
		return resultList;
	}

	private DocumentFragment searchElementsWithPatterns() {
		DocumentFragment resultFragment = this.createTemplateFragment();
		for (EIC eic : this.getEICList()) {
			for (Pattern pattern : patternList) {
				DocumentFragment newFragment = eic.findStrings(pattern);
				List<EIC> eicList = newFragment.getEICList();
				resultFragment.addAll(eicList);
			}
		}
		LOG.debug("RESULTS: "+resultFragment.getEICList().size());
		return resultFragment;
	}

	private DocumentFragment searchWithXPath(SourceElement sourceElement) {
		Element rawElement = sourceElement.getElement();
		DocumentFragment resultFragment = applyXPath(rawElement);
		return resultFragment;
	}

	public void setAbstractSearcher(AbstractSearcher abstractSearcher) {
		this.abstractSearcher = abstractSearcher;
	}

	public List<Pattern> getPatternList() {
		ensurePatternList();
		return patternList;
	}

	public SimpleResultList getResultList() {
		return resultList;
	}


	/**
	private List<EIC> eicList;
	protected List<Pattern> patternList;
	private List<String> xPathList;
	private int maxChar = 100; // maximum allowed by some publishers
	private AbstractSearcher abstractSearcher;
	private SourceElement sourceElement;
	private DocumentFragment resultFragment;
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		ensurePatternList();
		sb.append("Patterns: "+patternList.size()+"\n");
		for (Pattern pattern : patternList) {
			sb.append(pattern.toString() + "\n");
		}
		ensureXpathList();
		sb.append("XPaths: "+xPathList.size()+"\n");
		for (String xpath : xPathList) {
			sb.append(xpath + "\n");
		}
		sb.append("EICList: "+eicList.size()+"\n");
		for (EIC eic : eicList) {
			sb.append(eic.toString() + "\n");
		}
		sb.append("SourceElement: "+(sourceElement == null ? "NULL" : sourceElement.toString())+"\n");
		sb.append("ResultFragment: "+(resultFragment == null ? "NULL" : resultFragment.toString())+"\n");
		return sb.toString();
	}

	public DocumentFragment getResultFragment() {
		return resultFragment;
	}

}
