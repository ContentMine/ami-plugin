package org.xmlcml.ami.visitor;

import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.SimpleResultList;

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
