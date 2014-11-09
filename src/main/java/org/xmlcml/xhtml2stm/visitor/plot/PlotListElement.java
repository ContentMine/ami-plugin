package org.xmlcml.xhtml2stm.visitor.plot;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractListElement;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResult;
import org.xmlcml.xhtml2stm.visitor.SimpleResultElement;
import org.xmlcml.xhtml2stm.visitor.species.SpeciesListElement;

public class PlotListElement extends AbstractListElement {

	private final static Logger LOG = Logger.getLogger(PlotListElement.class);
	public final static String TAG = "plotList";

	public PlotListElement() {
		super(TAG);
	}

	@Override
	protected AbstractResultElement createElement(SimpleResult simpleResult) {
		// mayeb change this later
		return new SimpleResultElement(simpleResult);
	}

}
