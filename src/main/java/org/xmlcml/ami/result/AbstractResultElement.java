package org.xmlcml.ami.result;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.ami.AbstractAMIElement;
import org.xmlcml.ami.Type;
import org.xmlcml.ami.visitor.EIC;
import org.xmlcml.ami.util.AMIUtil;

/** an XMLElement capable of holding a variety of results.
 * 
 * @author pm286
 *
 */
public abstract class AbstractResultElement extends AbstractAMIElement {

	private static final String COUNT = "count";

	private static final Logger LOG = Logger.getLogger(AbstractResultElement.class);
	
	private String title;
	private Type type;
	private SimpleResultWrapper simpleResult;
	
	public AbstractResultElement(String tag) {
		super(tag);
	}	

	protected abstract AbstractResultElement createElement(SimpleResultWrapper simpleResult);
	
	public void setType(Type type) {
		this.type = type;
	}

	public void setCount(Integer value) {
		this.addAttribute(new Attribute(COUNT, String.valueOf(value)));
	}

	protected void addSimpleResultAsXML(SimpleResultWrapper simpleResult) {
		this.setSimpleResult(simpleResult);
		EIC eic = simpleResult.getElementInContext();
		addXpathAttribute(eic);
		addLineNumberAttribute(eic);
		addLineValueAttribute(eic);
		if (eic != null) {
			appendChild(eic.createPREElement());
		}
		addChild(EIC.VALUE_TAG, simpleResult.getKeyword());
		if (eic != null) {
			appendChild(eic.createPOSTElement());
		}
		throw new RuntimeException("avoid this routine");
	}

	private void addXpathAttribute(EIC eic) {
		String xpath = eic.getXPathOfResultElement();
		if (xpath != null) {
			this.addAttribute(new Attribute(AMIUtil.XPATH, xpath));
		}
	}

	public void addLineValueAttribute(EIC eic) {
		Integer number = eic.getLineNumber();
		if (number != null) {
			String lineValue = eic.getLineValue();
			if (lineValue != null) {
				this.addAttribute(new Attribute(AMIUtil.LINE_VALUE, lineValue));
			}
		}
	}

	public void addLineNumberAttribute(EIC eic) {
		Integer number = eic.getLineNumber();
		if (number != null) {
			this.addAttribute(new Attribute(AMIUtil.LINE_NUMBER, String.valueOf(number)));
		}
	}

	private void addChild(String name, String string) {
		Element element = new SimpleResultElement(name);
		element.appendChild(string);
		this.appendChild(element);
	}

	protected void setSimpleResult(SimpleResultWrapper simpleResult) {
		this.simpleResult = simpleResult;
	}
	
	
}
