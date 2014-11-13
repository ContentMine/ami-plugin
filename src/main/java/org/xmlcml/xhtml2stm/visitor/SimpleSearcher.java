package org.xmlcml.xhtml2stm.visitor;

import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.SimpleResultList;

public class SimpleSearcher extends AbstractSearcher {

	protected SimpleSearcher(AbstractVisitor visitor) {
		super(visitor);
	}

	@Override
	protected AbstractListElement createListElement(SimpleResultList resultList) {
		AbstractListElement listElement = new SimpleListElement(resultList);
		return listElement;
	}

	
}
