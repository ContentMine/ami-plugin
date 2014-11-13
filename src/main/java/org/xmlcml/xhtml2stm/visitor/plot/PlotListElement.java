package org.xmlcml.xhtml2stm.visitor.plot;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResultWrapper;
import org.xmlcml.xhtml2stm.visitor.VisitorSimpleResultElement;
import org.xmlcml.xhtml2stm.visitor.species.SpeciesListElement;

public class PlotListElement extends AbstractListElement {

	private final static Logger LOG = Logger.getLogger(PlotListElement.class);
	public final static String TAG = "plotList";

	public PlotListElement() {
		super(TAG);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResultWrapper simpleResult) {
		// mayeb change this later
		return new VisitorSimpleResultElement(simpleResult);
	}

}
