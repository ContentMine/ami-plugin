package org.xmlcml.xhtml2stm.result;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.Type;
import org.xmlcml.xhtml2stm.visitor.ElementInContext;
import org.xmlcml.xhtml2stm.visitor.VisitorSimpleResultElement;

/** holds primitive result/s from search.
 * 
 * @author pm286
 *
 */
public class SimpleResultWrapper {

	private final static Logger LOG = Logger.getLogger(SimpleResultWrapper.class);
	
	private static final String RESULT = "result";
	
	private String resultString;
	private ElementInContext elementInContext;
	private Element resultElement;
	private Type type;
	
	public SimpleResultWrapper() {
		
	}
	
	public SimpleResultWrapper(String resultString) {
		this.setResultString(resultString);
	}

	public SimpleResultWrapper(ElementInContext eic) {
		this.setElementInContext(eic);
	}
	
	private void setElementInContext(ElementInContext eic) {
		this.elementInContext = eic;
	}

	public ElementInContext getElementInContext() {
		if (elementInContext == null) {
			if (resultString != null) {
				elementInContext = new ElementInContext(resultString);
			}
		}
		return elementInContext;
	}

	public void setResultString(String resultString) {
		this.resultString = resultString;
	}
	
	public String getKeyword() {
		return (elementInContext != null) ? elementInContext.getResultValue() : resultString;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Element createResultElement() {
		Element element = new VisitorSimpleResultElement();
		return element;
	}
	
	@Override
	public String toString() {
		return resultString != null ? resultString : elementInContext.toString();
	}

	public Element createElement() {
		Element result = new Element(RESULT);
		if (elementInContext != null) {
			result.appendChild(elementInContext.createElement());
		} else if (resultString != null) {
			result.appendChild(resultString);
		} else if (resultElement != null) {
			result.appendChild(resultElement.copy());
		} else {
			result = null;
		}
		return result;
	}

	public void setResultElement(Element element) {
		this.resultElement = element;
		
	}

}