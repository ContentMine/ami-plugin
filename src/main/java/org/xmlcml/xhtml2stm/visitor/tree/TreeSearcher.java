package org.xmlcml.xhtml2stm.visitor.tree;

import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.SimpleResultList;
import org.xmlcml.xhtml2stm.visitor.AbstractSearcher;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;
import org.xmlcml.xhtml2stm.visitor.SimpleListElement;

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
