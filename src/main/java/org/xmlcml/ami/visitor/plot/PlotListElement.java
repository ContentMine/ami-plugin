package org.xmlcml.ami.visitor.plot;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitor.VisitorSimpleResultElement;
import org.xmlcml.ami.visitor.species.SpeciesListElement;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.AbstractResultElement;
import org.xmlcml.ami.result.SimpleResultWrapper;

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
