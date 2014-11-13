package org.xmlcml.xhtml2stm.visitor.metadata;

import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.SimpleResultList;
import org.xmlcml.xhtml2stm.visitable.html.HtmlVisitable;
import org.xmlcml.xhtml2stm.visitable.image.ImageVisitable;
import org.xmlcml.xhtml2stm.visitor.AbstractSearcher;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;
import org.xmlcml.xhtml2stm.visitor.chem.ChemListElement;

public class MetadataSearcher extends AbstractSearcher {

	protected MetadataSearcher(AbstractVisitor visitor) {
		super(visitor);
	}

	protected void search(HtmlVisitable htmlVisitable) {
		htmlVisitable.getMetadata();
	}

	protected void search(ImageVisitable imageVisitable) {
		imageVisitable.getMetadata();
	}
	
	@Override
	protected AbstractListElement createListElement(SimpleResultList resultList) {
		AbstractListElement listElement = new MetadataListElement(resultList);
		return listElement;
	}

}
