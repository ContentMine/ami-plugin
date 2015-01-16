package org.xmlcml.ami.visitor.chem;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGPolyline;
import org.xmlcml.graphics.svg.SVGRect;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGText;

/** 
 * Contains lists of SVG primitives in an SVG object.
 * 
 * Basically a DataTransferObject, which is built during interpretation.
 * 
 * @author pm286
 */
public class SVGPrimitives {

	private List<SVGCircle> circleList;
	private List<SVGLine> lineList;
	private List<SVGPath> pathList;
	private List<SVGPolygon> polygonList;
	private List<SVGPolyline> polylineList;
	private List<SVGRect> rectList;
	private List<SVGShape> shapeList; // does not include Path or Line
	private List<SVGText> textList;
	private List<SVGShape> unclassifiedShapeList;
	
	public SVGPrimitives() {
		circleList = new ArrayList<SVGCircle>();
		lineList = new ArrayList<SVGLine>();
		pathList = new ArrayList<SVGPath>();
		polygonList = new ArrayList<SVGPolygon>();
		polylineList = new ArrayList<SVGPolyline>();
		rectList = new ArrayList<SVGRect>();
		shapeList = new ArrayList<SVGShape>();
		textList = new ArrayList<SVGText>();
		unclassifiedShapeList = new ArrayList<SVGShape>();
	}
	
	public SVGPrimitives(SVGPrimitives other) {
		circleList = new ArrayList<SVGCircle>(other.getCircleList());
		lineList = new ArrayList<SVGLine>(other.getLineList());
		pathList = new ArrayList<SVGPath>(other.getPathList());
		polygonList = new ArrayList<SVGPolygon>(other.getPolygonList());
		polylineList = new ArrayList<SVGPolyline>(other.getPolylineList());
		rectList = new ArrayList<SVGRect>(other.getRectList());
		shapeList = new ArrayList<SVGShape>(other.getShapeList());
		textList = new ArrayList<SVGText>(other.getTextList());
		unclassifiedShapeList = new ArrayList<SVGShape>(other.getUnclassifiedShapeList());
	}

	/*List<SVGPolygon> polygonList = new ArrayList<SVGPolygon>();
	List<SVGPolyline> polylineList = new ArrayList<SVGPolyline>();
	List<SVGLine> lineList = new ArrayList<SVGLine>();
	List<SVGShape> shapeList1 = new ArrayList<SVGShape>();
	SVGSVG svg = new SVGSVG();
	for (SVGShape shape : shapeList) {
		if (shape instanceof SVGPolyline) {
			polylineList.add((SVGPolyline)shape);
			shape.setFill("none");
			//shape.setStroke("1.0");
			//svg.appendChild(shape);
		} else if (shape instanceof SVGLine) {
			lineList.add((SVGLine)shape);
			shape.setFill("blue");
			svg.appendChild(shape);
		} else if (shape instanceof SVGPolygon) {
			SVGPolygon polygon = (SVGPolygon)shape;
			SVGCircle circle =  path2ShapeConverter.convertToCircle(polygon);
			polygonList.add((SVGPolygon)shape);
			shape.setFill("green");
			svg.appendChild(shape);
		} else {
			shapeList1.add(shape);
		}
	}*/

	public void addShapesToSubclassedLists(List<SVGShape> shapeList) {
		for (SVGShape shape : shapeList) {
			addShapeToSubclassedLists(shape);
		}
	}

	void addShapeToSubclassedLists(SVGShape shape) {
		if (shape instanceof SVGCircle) {
			add((SVGCircle) shape);
		} else if (shape instanceof SVGLine) {
			add((SVGLine) shape);
		} else if (shape instanceof SVGPath) {
			add((SVGPath) shape);
		} else if (shape instanceof SVGPolygon) {
			add((SVGPolygon) shape);
		} else if (shape instanceof SVGPolyline) {
			add((SVGPolyline) shape);
		} else if (shape instanceof SVGRect) {
			add((SVGRect) shape);
		} else {
			add(shape);
		}
	}
		
	/**
	 * @param shape
	 */
	public void addUnclassified(SVGShape shape) {
		unclassifiedShapeList.add(shape);
	}

	public List<SVGShape> getUnclassifiedShapeList() {
		return unclassifiedShapeList;
	}

	//Circles

	public List<SVGCircle> getCircleList() {
		return circleList;
	}

	public void add(SVGCircle circle) {
		circleList.add(circle);
	}

	public void addCircles(List<SVGCircle> circleList) {
		this.circleList.addAll(circleList);
	}
	
	//Lines

	public List<SVGLine> getLineList() {
		return lineList;
	}

	public void add(SVGLine line) {
		lineList.add(line);
	}

	public void addLines(List<SVGLine> lineList) {
		this.lineList.addAll(lineList);
	}

	//Paths

	public List<SVGPath> getPathList() {
		return pathList;
	}

	public void add(SVGPath path) {
		pathList.add(path);
	}

	public void addPaths(List<SVGPath> pathList) {
		this.pathList.addAll(pathList);
	}

	//Polygons

	public List<SVGPolygon> getPolygonList() {
		return polygonList;
	}

	public void add(SVGPolygon polygon) {
		polygonList.add(polygon);
	}

	public void addPolygons(List<SVGPolygon> polygonList) {
		this.polygonList.addAll(polygonList);
	}

	//Polylines
	
	public List<SVGPolyline> getPolylineList() {
		return polylineList;
	}

	public void add(SVGPolyline polyline) {
		polylineList.add(polyline);
	}

	public void addPolylines(List<SVGPolyline> polylineList) {
		this.polylineList.addAll(polylineList);
	}
	
	//Rect

	public List<SVGRect> getRectList() {
		return rectList;
	}

	public void add(SVGRect rect) {
		rectList.add(rect);
	}

	public void addRects(List<SVGRect> rectList) {
		this.rectList.addAll(rectList);
	}

	//Shapes

	public List<SVGShape> getShapeList() {
		return shapeList;
	}

	/** 
	 * Only add to shapeList if we know it's not subclassed
	 * 
	 * @param shape
	 */
	void add(SVGShape shape) {
		shapeList.add(shape);
	}

	void addShapes(List<SVGShape> shapeList) {
		this.shapeList.addAll(shapeList);
	}

	//Text

	public List<SVGText> getTextList() {
		return textList;
	}

	public void add(SVGText text) {
		textList.add(text);
	}

	public void addTexts(List<SVGText> textList) {
		this.textList.addAll(textList);
	}

}
