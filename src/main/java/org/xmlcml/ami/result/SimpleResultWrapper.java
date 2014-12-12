package org.xmlcml.ami.result;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.ami.Type;
import org.xmlcml.ami.visitor.EIC;
import org.xmlcml.ami.visitor.VisitorSimpleResultElement;

/** holds primitive result/s from search.
 * 
 * @author pm286
 *
 */
public class SimpleResultWrapper {

	private final static Logger LOG = Logger.getLogger(SimpleResultWrapper.class);
	
	private static final String RESULT = "result";
	
	private String resultString;
	private EIC elementInContext;
	private Element resultElement;
	private Type type;
	
	public SimpleResultWrapper() {
		
	}
	
	public SimpleResultWrapper(String resultString) {
		this.setResultString(resultString);
	}

	public SimpleResultWrapper(EIC eic) {
		this.setElementInContext(eic);
	}
	
	private void setElementInContext(EIC eic) {
		this.elementInContext = eic;
	}

	public EIC getElementInContext() {
		if (elementInContext == null) {
			if (resultString != null) {
				elementInContext = new EIC(resultString);
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
		StringBuilder sb = new StringBuilder();
		if (resultElement != null) {
			sb.append(resultElement.toString());
		} else if (elementInContext == null) {
			sb.append(elementInContext.toString());
		} else {
			sb.append(resultString);
		}
		return sb.toString();
	}

	public Element createElement() {
		Element result = new ResultElement();
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
