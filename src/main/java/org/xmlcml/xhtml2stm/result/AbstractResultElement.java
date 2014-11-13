package org.xmlcml.xhtml2stm.result;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.Type;
import org.xmlcml.xhtml2stm.util.AMIUtil;
import org.xmlcml.xhtml2stm.visitor.ElementInContext;

/** an XMLElement capable of holding a variety of results.
 * 
 * @author pm286
 *
 */
public abstract class AbstractResultElement extends AbstractXHTML2STMElement {

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
		ElementInContext eic = simpleResult.getElementInContext();
		this.addAttribute(new Attribute(AMIUtil.XPATH, eic.getXPathOfResultElement()));
		if (eic != null) {
			appendChild(eic.createPREElement());
		}
		addChild(ElementInContext.VALUE_TAG, simpleResult.getKeyword());
		if (eic != null) {
			appendChild(eic.createPOSTElement());
		}
		throw new RuntimeException("avoid this routine");
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
