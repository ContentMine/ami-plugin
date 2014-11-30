package org.xmlcml.ami.visitor.plot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.SourceElement;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.ami.visitable.svg.SVGContainer;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.SimpleListElement;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.SimpleResultList;

public class PlotSearcher extends AbstractSearcher {

	private final static Logger LOG = Logger.getLogger(PlotSearcher.class);
	
	public PlotSearcher(PlotVisitor plotVisitor) {
		super(plotVisitor);
	}

	protected void search(SVGVisitable svgVisitable) {
		createPlots(svgVisitable);
	}

	@Override
	protected AbstractListElement createListElement(SimpleResultList resultList) {
		// maybe change later
		AbstractListElement listElement = new SimpleListElement(resultList);
		return listElement;
	}

	@Override 
	protected void search(SVGContainer svgContainer) {
//		searchXPathPatternAndCollectResults(new SourceElement(svgContainer));
		createPlots(svgContainer);
	}
	

	//Do we need functional programmimg?
	private void createPlots(SVGVisitable svgVisitable) {
		for (SVGContainer svgContainer : svgVisitable.getSVGContainerList()) {
			createPlots(svgContainer);
		}
	}

	private void createPlots(SVGContainer svgContainer) {
		SVGElement svgElement = (SVGElement) svgContainer.getElement();
		createPlot(svgElement);
		createPlot1(svgElement);
	}

	private Element createPlot(SVGElement svgElement) {
		List<SVGPolyline> polylineList = SVGPolyline.extractSelfAndDescendantPolylines(svgElement);
		int i = 0;
		for (SVGPolyline polyline : polylineList) {
			Real2Array real2Array = polyline.getReal2Array();
			RealArray xArray = real2Array.getXArray();
			RealArray yArray = real2Array.getYArray();
			RealArray filter = RealArray.getFilter(3, RealArray.Filter.GAUSSIAN_SECOND_DERIVATIVE);
			RealArray y2Array = yArray.applyFilter(filter);
			Real2Array xy2Array = new Real2Array(xArray, y2Array);
			try {
				outputCSV(i, xy2Array, 2);
				outputSVG(i, xy2Array, 2, new Transform2(new double[]{2.5, 0.0, 0.0, 0.0, 10.0, 100.0, 0.0, 0.0, 1.0}));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			i++;
			LOG.trace(yArray.size()+"/"+y2Array.size());
		}
		return null;
	}

	private Element createPlot1(SVGElement svgElement) {
		List<SVGPolyline> polylineList = SVGPolyline.extractSelfAndDescendantPolylines(svgElement);
		int i = 0;
		for (SVGPolyline polyline : polylineList) {
			Real2Array real2Array = polyline.getReal2Array();
			RealArray xArray = real2Array.getXArray();
			RealArray yArray = real2Array.getYArray();
			RealArray filter = RealArray.getFilter(20, RealArray.Filter.GAUSSIAN);
			RealArray y2Array = yArray.applyFilter(filter);
			Real2Array xy2Array = new Real2Array(xArray, y2Array);
			try {
				//outputCSV(i, xy2Array, 2);
				outputSVG(10+i, xy2Array, 2, new Transform2(new double[]{1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0}));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			i++;
			LOG.trace(yArray.size()+"/"+y2Array.size());
		}
		return null;
	}


	private void outputSVG(int serial, Real2Array xyArray, int offset, Transform2 t2) throws IOException {
		SVGG g = new SVGG();
		g.setTransform(t2);
		for (int i = offset; i < xyArray.size() - offset-1; i++) {
			Real2 xy = xyArray.get(i);
			Real2 xy1 = xyArray.get(i+1);
			SVGLine line = new SVGLine(xy, xy1);
			line.setStrokeWidth(0.2);
			g.appendChild(line);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/file"+serial+".svg"));
	}

	private void outputCSV(int serial, Real2Array xyArray, int offset) throws IOException {
		FileWriter fw = new FileWriter(new File("target/file"+serial+".csv")); 
		for (int i = offset; i < xyArray.size() - offset; i++) {
			Real2 xy = xyArray.get(i);
			fw.write(String.valueOf(xy.getX()));
			fw.write(",");
			fw.write(String.valueOf(xy.getY()));
			fw.write(",\n");
		}
		fw.close();
	}

}
