package org.xmlcml.ami.visitor.chem;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPolygon;

public class WedgeBond extends Joinable {
	
	private final static Logger LOG = Logger.getLogger(WedgeBond.class);

	private static final double TRIANGLE_PRORITY = 3.0;

	private SVGPolygon svgPolygon;
	
	private static final double DEFAULT_RELATIVE_DISTANCE_TO_INTERSECTION = 0.5;
	
	private double relativeDistance = DEFAULT_RELATIVE_DISTANCE_TO_INTERSECTION;

	public WedgeBond(SVGPolygon svgPolygon) {
		if (svgPolygon.createLineList(true).size() != 3) {
			throw new IllegalArgumentException();
		}
		this.svgPolygon = svgPolygon;
		addJoinPoints();
	}

	public double getPriority() {
		return TRIANGLE_PRORITY;
	}
	
	public void addJoinPoints() {
		double shortestLineLength = Double.MAX_VALUE;
		SVGLine shortestLine = null;
		Real2 point = null;
		/*List<Real2> allPoints = new ArrayList<Real2>();
		for (SVGMarker l : svgPolygon.getPointList()) {
			allPoints.add(((SVGCircle) l.getChild(0)).getXY());
		}*/
		for (SVGLine l : svgPolygon.getLineList()) {
			if (l.getLength() < shortestLineLength) {
				shortestLineLength = l.getLength();
				shortestLine = l;
				for (Real2 p : svgPolygon.getReal2Array()) {
					if (!p.isEqualTo(l.getXY(0), 1e-10) && !p.isEqualTo(l.getXY(1), 1e-10)) {
						point = p;
					}
				}
			}
		}
		getJoinPoints().add(new JoinPoint(point));
		getJoinPoints().add(new JoinPoint(Real2.getCentroid(Arrays.asList(shortestLine.getXY(0), shortestLine.getXY(1)))));
	}

	/*public JoinPoint getIntersectionPoint(Joinable joinable) {
		return joinable.getIntersectionPoint(this);
	}

	public JoinPoint getIntersectionPoint(SingleBond line) {
		return joinManager.getCommonPoint(line);
	}

	public JoinPoint getIntersectionPoint(JoinableText text) {
		return joinManager.getCommonPoint(text);
	}

	public JoinPoint getIntersectionPoint(DoubleBond tramLine) {
		return joinManager.getCommonPoint(tramLine);
	}

	public JoinPoint getIntersectionPoint(WedgeBond triangle) {
		return joinManager.getCommonPoint(triangle);
	}

	public JoinPoint getIntersectionPoint(HatchedBond triangle) {
		return joinManager.getCommonPoint(triangle);
	}

	public JoinManager getJoinPointList() {
		return joinManager;
	}*/

	public String getID() {
		return svgPolygon.getId();
	}

	public SVGPolygon getSVGElement() {
		return svgPolygon;
	}

	/*public SVGLine getBackbone() {
		return new SVGLine(joinManager.getJoinPoints().get(0).getPoint(), joinManager.getJoinPoints().get(1).getPoint());
	}*/

	/** 
	 * @return null as triangles aren't represented by a single point
	 */
	/*public Real2 getPoint() {
		return null;
	}*/

	/*public void addJunction(Junction junction) {
		joinManager.add(junction);
	}
	
	public List<Junction> getJunctionList() {
		return (joinManager == null ? new ArrayList<Junction>() : joinManager.getJunctionList());
	}*/
	
	public String toString() {
		return svgPolygon.toXML() + "\n ... " + Arrays.toString(getJoinPoints().toArray());
	}

	/*@Override
	public Double getRelativeDistance() {
		return relativeDistance;
	}*/
	
}
