package org.xmlcml.ami.visitor.metadata;

import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.visitable.AbstractVisitable;
import org.xmlcml.ami.visitable.image.ImageVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;

public class MetadataSearcher extends AbstractSearcher {

	protected MetadataSearcher(AbstractVisitor visitor) {
		super(visitor);
	}

	protected void search(AbstractVisitable htmlVisitable) {
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
