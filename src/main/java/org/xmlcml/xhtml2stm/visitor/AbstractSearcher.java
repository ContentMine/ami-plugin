package org.xmlcml.xhtml2stm.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.Type;
import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.ResultList;
import org.xmlcml.xhtml2stm.result.ResultsElement;
import org.xmlcml.xhtml2stm.result.SimpleResult;
import org.xmlcml.xhtml2stm.visitable.SourceElement;
import org.xmlcml.xhtml2stm.visitable.VisitableContainer;
import org.xmlcml.xhtml2stm.visitable.html.HtmlContainer;
import org.xmlcml.xhtml2stm.visitable.image.ImageContainer;
import org.xmlcml.xhtml2stm.visitable.svg.SVGContainer;
import org.xmlcml.xhtml2stm.visitable.xml.XMLContainer;
import org.xmlcml.xml.XMLUtil;

/** tool for searching containers.
 * 
 * @author pm286
 *
 */
public abstract class AbstractSearcher {
	
	private final static Logger LOG = Logger.getLogger(AbstractSearcher.class);

	protected List<Pattern> patternList;
	private List<String> xPathList;
	private Type type;
	protected AbstractVisitor visitor;
	protected ResultsElement resultsElement;
	protected SourceElement sourceElement;
	protected ResultList resultList;
	private int maxChar = 100; // maximum allowed by some publishers

	private AbstractSearcher() {
		ensureResultsElement();
	}
	
	protected AbstractSearcher(AbstractVisitor visitor) {
		this();
		this.visitor = visitor;
	}
	
	/** creates a default searcher.
	 * 
	 * carries out very simple/limited i/o operations.
	 * 
	 * search will probably return unaltered input or null.
	 * 
	 * Normally use 
	 * @return
	 */
	public static AbstractSearcher createDefaultSearcher(AbstractVisitor visitor) {
		AbstractSearcher searcher = new SimpleSearcher(visitor);
		return searcher;
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

	protected void ensurePatternList() {
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

	public void setType(Type type) {
		this.type = type;
	}

	public void setMaxChar(int maxChar) {
		this.maxChar = maxChar;
	}
	
	public int getMaxChar() {
		return maxChar;
	}
	
	// =============== SEARCH ============

	public void search(VisitableContainer container) {
		resultList = this.searchXPathPatternAndCollectResults(container);
		AbstractListElement listElement = createListElement(resultList);
		sourceElement.appendChild(listElement);
	}

	protected abstract AbstractListElement createListElement(ResultList resultList);
	
	protected void ensureResultsElement() {
		if (resultsElement == null) {
			resultsElement = new ResultsElement();
		}
	}
	
	// =============SEARCH======================

	protected List<ElementInContext> createEICListWithXpathsAndPatterns(SourceElement sourceElement) {
		ensurePatternList();
		ensureXpathList();
		List<ElementInContext> eicList = applyXPathToGenerateElements(sourceElement);
		List<ElementInContext> resultList = searchElementsWithRegexes(eicList);
		return resultList;
	}

	private List<ElementInContext> searchElementsWithRegexes(List<ElementInContext> eicList) {
		LOG.debug("patternList : "+patternList.size());
		List<ElementInContext> eicResultList = new ArrayList<ElementInContext>();
		for (ElementInContext eic : eicList) {
			for (Pattern pattern : patternList) {
				List<ElementInContext> eicResultList0 = findStrings(eic, pattern);
				eicResultList.addAll(eicResultList0);
			}
		}
		return eicResultList;
	}

	private List<ElementInContext> applyXPathToGenerateElements(SourceElement sourceElement) {
		Element rawElement = sourceElement.getElement();
		List<ElementInContext> elementList = applyXPathToGenerateElements(rawElement);
		return elementList;
	}

	protected List<ElementInContext> applyXPathToGenerateElements(Element rawElement) {
		List<ElementInContext> elementList = new ArrayList<ElementInContext>();
		if (xPathList.size() == 0) {
			ElementInContext eic = new ElementInContext(rawElement, maxChar);
			eic.setResultValue(rawElement.getValue());
			elementList.add(eic);
		} else {
			for (String xPath : xPathList) {
				LOG.trace("xpath: "+xPath);
				List<ElementInContext> xPathResults = applyXpath(rawElement, xPath);
				elementList.addAll(xPathResults);
			}
		}
		return elementList;
	}
	
	private static List<ElementInContext> findStrings(ElementInContext eic, Pattern pattern) {
		List<ElementInContext> resultList = new ArrayList<ElementInContext>();
		String value = eic.getResultValue();
		Matcher matcher = pattern.matcher(value);
		while (matcher.find()) {
			ElementInContext newEic = ElementInContext.createNewElementInContext(eic, value, matcher.start(), matcher.end());
			resultList.add(newEic);
		}
		return resultList;
	}

	/**
	 * Here we describe a new hadrosauroid dinosaur, <italic>Yunganglong
	 * datongensis</italic>
	 * 
	 * @param element
	 * @return
	 */
	public List<ElementInContext> applyXpath(Element targetElement, String xpath) {
		List<Element> resultElements = XMLUtil.getQueryElements(targetElement, xpath);
		List<ElementInContext> eicList = new ArrayList<ElementInContext>();
		for (Element resultElement : resultElements) {
			ElementInContext eic = new ElementInContext(resultElement, maxChar);
			eicList.add(eic);
		}
		return eicList;
	}
	
	public ResultList searchXPathPatternAndCollectResults(VisitableContainer container) {
		ensureResultsElement();
		sourceElement = new SourceElement(container);
		resultsElement.appendChild(sourceElement);
		resultList = searchXPathPatternAndCollectResults(sourceElement);
		return resultList;
	}

	/** Override in subclasses if special search strategy required.
	 * 
	 * @param sourceElement
	 * @return
	 */
	protected ResultList searchXPathPatternAndCollectResults(SourceElement sourceElement) {
		List<ElementInContext> eicList = this.createEICListWithXpathsAndPatterns(sourceElement);
		resultList = createResultListFromElementsInContext(eicList);
		transformResultList();
		return resultList;
	}
	
	private ResultList createResultListFromElementsInContext(List<ElementInContext> eicList) {
		ResultList resultList = new ResultList();
		for (ElementInContext eic : eicList) {
			SimpleResult simpleResult = new SimpleResult(eic);
			resultList.add(simpleResult);
		}
		return resultList;
	}

	/** allows Searcher to carry out more operations after initial creation.
	 * 
	 */
	protected void transformResultList() {
		// No-op unless overridden
	}

	public ResultsElement getResultsElement() {
		ensureResultsElement();
		return resultsElement;
	}
	
	

	// SEARCH functionality
	
	protected void search(HtmlContainer htmlContainer) {
		throw new RuntimeException("Must overide search(HtmlContainer)");
	}

	protected void search(XMLContainer xmlContainer) {
		throw new RuntimeException("Must overide search(XMLContainer)");
	}

	protected void search(SVGContainer svgContainer) {
		throw new RuntimeException("Must overide search(SVGContainer)");
	}

	protected void search(ImageContainer imageContainer) {
		throw new RuntimeException("Must overide image(SVGContainer)");
	}

//	protected ResultsElement createAndFillResultsElement() {
//		ensureResultsElement();
//		resultsElement.appendChild(createListElement(resultList));
//		return resultsElement;
//	}


}
