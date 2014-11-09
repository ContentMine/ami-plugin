package org.xmlcml.xhtml2stm.result;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.Type;

import com.google.common.collect.Multiset.Entry;

public abstract class AbstractListElement extends AbstractXHTML2STMElement {

	private final static Logger LOG = Logger.getLogger(AbstractListElement.class);
	
	protected AbstractListElement(String tag) {
		super(tag);
	}
	
	public void addResultList(ResultList resultList, Type type) {
		for (SimpleResult result : resultList) {
			AbstractResultElement resultElement = this.createElement(result);
			resultElement.setType(type);
			this.appendChild(resultElement);
		}
		LOG.debug("created resultList "+this.getChildElements().size());
	}

	/** must create its own subclassed ResultElement.
	 * 
	 * @param entry
	 * @return
	 */
	protected abstract AbstractResultElement createElement(SimpleResult simpleResult);

}
