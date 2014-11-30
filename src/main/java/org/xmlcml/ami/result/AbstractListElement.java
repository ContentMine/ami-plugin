package org.xmlcml.ami.result;

import org.apache.log4j.Logger;
import org.xmlcml.ami.AbstractAMIElement;
import org.xmlcml.ami.Type;

public abstract class AbstractListElement extends AbstractAMIElement {

	private final static Logger LOG = Logger.getLogger(AbstractListElement.class);
	
	protected AbstractListElement(String tag) {
		super(tag);
	}
	
	public void addResultList(SimpleResultList resultList, Type type) {
		for (SimpleResultWrapper result : resultList) {
			AbstractResultElement resultElement = this.createElement(result);
			resultElement.setType(type);
			this.appendChild(resultElement);
		}
	}

	/** must create its own subclassed ResultElement.
	 * 
	 * @param entry
	 * @return
	 */
	protected abstract AbstractResultElement createElement(SimpleResultWrapper simpleResult);

}
