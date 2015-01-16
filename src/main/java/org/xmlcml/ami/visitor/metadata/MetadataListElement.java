package org.xmlcml.ami.visitor.metadata;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;
import org.xmlcml.ami.visitor.VisitorSimpleResultElement;

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
