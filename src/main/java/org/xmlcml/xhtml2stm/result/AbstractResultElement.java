package org.xmlcml.xhtml2stm.result;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.Type;
import org.xmlcml.xhtml2stm.visitor.ElementInContext;

/** an XMLElement capable of holding a variety of results.
 * 
 * @author pm286
 *
 */
public abstract class AbstractResultElement extends AbstractXHTML2STMElement {

	private static final String KEY = "k";
	private static final String POST = "post";
	private static final String PRE = "pre";
	private static final String COUNT = "count";
	private static final String XPATH = "xpath";

	private static final Logger LOG = Logger.getLogger(AbstractResultElement.class);
	
	private String title;
	private Type type;
	private SimpleResult simpleResult;
	
	public AbstractResultElement(String tag) {
		super(tag);
	}	

	protected abstract AbstractResultElement createElement(SimpleResult simpleResult);
	
	public void setType(Type type) {
		this.type = type;
	}

	public void setCount(Integer value) {
		this.addAttribute(new Attribute(COUNT, String.valueOf(value)));
	}

	protected void addEntry(SimpleResult simpleResult) {
		this.setSimpleResult(simpleResult);
		ElementInContext eic = simpleResult.getElementInContext();
		this.addAttribute(new Attribute(XPATH, eic.getXPathOfResultElement()));
		if (eic != null) {
			addChild(PRE, ElementInContext.getConcatenatedStrings(eic.getOrCreatePrecedingSiblingNodeStrings()));
		}
		addChild(KEY, simpleResult.getKeyword());
		if (eic != null) {
			addChild(POST, ElementInContext.getConcatenatedStrings(eic.getOrCreateFollowingSiblingNodeStrings()));
		}
	}

	private void addChild(String name, String string) {
		Element element = new SimpleResultElement(name);
		element.appendChild(string);
		this.appendChild(element);
	}

	protected void setSimpleResult(SimpleResult simpleResult) {
		this.simpleResult = simpleResult;
	}
	
	
}
