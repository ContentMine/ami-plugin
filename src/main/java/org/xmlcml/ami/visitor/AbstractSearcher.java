package org.xmlcml.ami.visitor;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.ami.Type;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.SimpleResultElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.visitable.SourceElement;
import org.xmlcml.ami.visitable.VisitableContainer;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.ami.visitable.image.ImageContainer;
import org.xmlcml.ami.visitable.pdf.PDFContainer;
import org.xmlcml.ami.visitable.svg.SVGContainer;
import org.xmlcml.ami.visitable.txt.TextContainer;
import org.xmlcml.ami.visitable.xml.XMLContainer;

/** tool for searching containers.
 * 
 * @author pm286
 *
 */
public abstract class AbstractSearcher {
	
	private final static Logger LOG = Logger.getLogger(AbstractSearcher.class);

	/** used in subclasses such as SpeciesSearcher and SequenceSearcher */
	private Type type;
	protected AbstractVisitor visitor;
	protected DocumentFragment documentFragment;
	protected SimpleResultList resultList;
	protected SimpleResultElement resultElement;
	protected SourceElement sourceElement;
	
	int maxChar;  // remove this

	private AbstractSearcher() {
		ensureResultsElement();
		ensureDocumentFragment();
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
		ensureDocumentFragment();
		documentFragment.setPattern(pattern);
	}

	/** adds additional pattern to list.
	 * 
	 * no check for uniqueness.
	 * @param pattern
	 */
	public void addPattern(Pattern pattern) {
		ensureDocumentFragment();
		documentFragment.addPattern(pattern);
	}

	/** adds additional pattern to list.
	 * 
	 * no check for uniqueness.
	 * @param pattern
	 */
	public void addXPath(String xpath) {
		ensureDocumentFragment();
		documentFragment.addXPath(xpath);
	}

	protected void ensureDocumentFragment() {
		if (documentFragment == null) {
			this.documentFragment = new DocumentFragment();
			this.documentFragment.setAbstractSearcher(this);
		}
	}

	public void setType(Type type) {
		this.type = type;
	}

	// =============== SEARCH ============


	/** this subclassing is necessary to implement double dispatch.
	 * 
	 * It was in the visitor but has largely devolved to the searcher.
	 * 
	 */
	public void search(VisitableContainer container) {
		ensureResultsElement();
		sourceElement = new SourceElement(container);
		resultElement.appendChild(sourceElement);
		if (container instanceof HtmlContainer) {
			search((HtmlContainer) container);
		} else if (container instanceof PDFContainer) {
			search((PDFContainer) container);
		} else if (container instanceof TextContainer) {
			search((TextContainer) container);
		} else if (container instanceof XMLContainer) {
			search((XMLContainer) container);
		} else {
			LOG.debug("RegexSearcher cannot search class: "+container.getClass());
		}
		if (resultList == null && documentFragment != null) {
			this.resultList = this.documentFragment.getResultList();
		}
	}

	public void defaultSearch(VisitableContainer container) {
		sourceElement = new SourceElement(container);
		resultList = this.searchXPathPatternAndCollectResults(sourceElement);
		AbstractListElement listElement = createListElement(resultList);
		sourceElement.appendChild(listElement);
	}

	public SimpleResultList searchXPathPatternAndCollectResults(SourceElement sourceElement) {
		ensureResultsElement();
		resultElement.appendChild(sourceElement);
		ensureDocumentFragment();
		resultList = documentFragment.searchXPathPatternAndCollectResults(sourceElement);
		transformResultList();
		return resultList;
	}


	protected abstract AbstractListElement createListElement(SimpleResultList resultList);
	
	protected void ensureResultsElement() {
		if (resultElement == null) {
			resultElement = new SimpleResultElement();
		}
	}
	
	// =============SEARCH======================


	/** allows Searcher to carry out more operations after initial creation.
	 * 
	 */
	protected void transformResultList() {
		// No-op unless overridden
	}

	public SimpleResultElement getResultsElement() {
		ensureResultsElement();
		return resultElement;
	}
	
	

	// SEARCH functionality
	
	protected void search(HtmlContainer htmlContainer) {
		LOG.error("Must overide search(HtmlContainer), using defaultSearch");
		defaultSearch(htmlContainer);
	}

	protected void search(ImageContainer imageContainer) {
		throw new RuntimeException("Must overide search(ImageContainer)");
	}
	
	protected void search(PDFContainer pdfContainer) {
		LOG.error("converting PDF to HTML, using defaultSearch");
		HtmlContainer htmlContainer = pdfContainer.getHtmlContainer();
		if (htmlContainer != null) {
			search(htmlContainer);
		} else {
			LOG.error("Cannot create HtmlContainer from PDF");
		}
	}

	protected void search(SVGContainer svgContainer) {
		throw new RuntimeException("Must overide search(SVGContainer)");
	}

	protected void search(TextContainer imageContainer) {
		throw new RuntimeException("Must overide search(TextContainer)");
	}
	
	protected void search(XMLContainer xmlContainer) {
		LOG.error("Must overide search(XMLContainer), using defaultSearch");
		defaultSearch(xmlContainer);
	}

	public SimpleResultList getResultsList() {
		return resultList;
	}


}
