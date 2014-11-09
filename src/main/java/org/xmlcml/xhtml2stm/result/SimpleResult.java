package org.xmlcml.xhtml2stm.result;

import nu.xom.Element;

import org.xmlcml.xhtml2stm.Type;
import org.xmlcml.xhtml2stm.visitor.ElementInContext;
import org.xmlcml.xhtml2stm.visitor.SimpleResultElement;

/** holds primitive result/s from search.
 * 
 * @author pm286
 *
 */
public class SimpleResult {

	private String resultString;
	private ElementInContext elementInContext;
	private Type type;
	
	public SimpleResult() {
		
	}
	
	public SimpleResult(String resultString) {
		this.setResultString(resultString);
	}

	public SimpleResult(ElementInContext eic) {
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
		Element element = new SimpleResultElement();
		return element;
	}
	
	@Override
	public String toString() {
		return resultString != null ? resultString : elementInContext.toString();
	}

}
