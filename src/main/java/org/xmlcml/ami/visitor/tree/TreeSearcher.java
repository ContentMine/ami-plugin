package org.xmlcml.ami.visitor.tree;

import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.SimpleListElement;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.SimpleResultList;

public class TreeSearcher extends AbstractSearcher {

	protected TreeSearcher(AbstractVisitor visitor) {
		super(visitor);
	}

	@Override
	protected AbstractListElement createListElement(SimpleResultList resultList) {
		// maybe change later
		AbstractListElement listElement = new SimpleListElement(resultList);
		return listElement;
	}

}
