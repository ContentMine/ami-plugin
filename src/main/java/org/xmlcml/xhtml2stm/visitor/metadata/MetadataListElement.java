package org.xmlcml.xhtml2stm.visitor.metadata;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResultList;
import org.xmlcml.xhtml2stm.result.SimpleResultWrapper;
import org.xmlcml.xhtml2stm.visitor.VisitorSimpleResultElement;
import org.xmlcml.xhtml2stm.visitor.species.SpeciesListElement;

public class MetadataListElement extends AbstractListElement {

	private final static Logger LOG = Logger.getLogger(MetadataListElement.class);
	public final static String TAG = "metadataList";

	public MetadataListElement() {
		super(TAG);
	}

	public MetadataListElement(SimpleResultList resultList) {
		this();
		this.addResultList(resultList, null);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResultWrapper simpleResult) {
		return new VisitorSimpleResultElement(simpleResult);
	}

}
