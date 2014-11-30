package org.xmlcml.ami.visitor.chem;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Units;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 
 * Two or more parallel lines with overlap.
 * <p>
 * Used for double bonds, grid lines, etc.
 * <p>
 * TODO should this really be for grid lines? What with HatchedPolygon now existing...
 * <p>
 * Originally extended SVGG so it could be used in place of SVGLines when needed but no longer does
 * <p>
 * @author pm286
 */
public class DoubleBond extends Joinable {

	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(DoubleBond.class);

	// large enough to cover any known tramlines
	private final static Angle ANGLE_EPS = new Angle(0.3, Units.RADIANS);

	private static final double DOUBLE_BOND_PRORITY = 4.0;

	private List<SVGLine> lineList;
	
	private static final double DEFAULT_RELATIVE_DISTANCE_TO_INTERSECTION = 0.5;
	
	private double relativeDistance = DEFAULT_RELATIVE_DISTANCE_TO_INTERSECTION;
	
	public DoubleBond(SVGLine linei, SVGLine linej) {
		add(linei);
		add(linej);
		addJoinPoints();
	}

	private void addJoinPoints() {
		Angle eps = new Angle(0.1, Units.RADIANS); // we already know they are aligned
		SVGLine line0 = lineList.get(0);
		SVGLine line1 = lineList.get(1);
		Real2 point00 = line0.getXY(0);
		Real2 point01 = line0.getXY(1);
		Real2 point10 = line1.getXY(0);
		Real2 point11 = line1.getXY(1);
		Real2 join0 = null;
		Real2 join1 = null;
		if (line0.isAntiParallelTo(line1, eps)) {
			join0 = point00.getMidPoint(point11);
			join1 = point01.getMidPoint(point10);
		} else {
			join0 = point00.getMidPoint(point10);
			join1 = point01.getMidPoint(point11);
		}
		JoinPoint point0 = new JoinPoint(join0);
		JoinPoint point1 = new JoinPoint(join1);
		point0.setRadius(Math.max(point0.getRadius(), line0.getMidPoint().getDistance(line1.getMidPoint()) / 2));
		point1.setRadius(Math.max(point1.getRadius(), line0.getMidPoint().getDistance(line1.getMidPoint()) / 2));
		getJoinPoints().add(point0);
		getJoinPoints().add(point1);
	}
	
	public double getPriority() {
		return DOUBLE_BOND_PRORITY;
	}
	
	public void add(SVGLine line) {
		//this.appendChild(line);
		ensureLineList();
		lineList.add(line);
	}
	
	public SVGLine getLine(int i) {
		ensureLineList();
		return (i < 0 || i >= lineList.size()) ? null : lineList.get(i);
	}

	private void ensureLineList() {
		if (lineList == null) {
			lineList = new ArrayList<SVGLine>();
			//lineList = SVGLine.extractSelfAndDescendantLines(this);
		}
	}

	/*public boolean canBeJoinedTo(Joinable otherJoinable) {
		boolean joinsTo = false;
		JoinPointList otherJoiner = otherJoinable.getJoinPointList();
		if (otherJoinable instanceof JoinableText) {
			joinsTo = true;
			//joinsTo = joiner.canBeJoinedTo(otherJoiner);
		} else if (otherJoinable instanceof JoinableLine) {
			joinsTo = true;
			//joinsTo = joiner.canBeJoinedTo(otherJoiner);
		} else if (otherJoinable instanceof TramLine) {
			//Do nothing
		}
		return joinsTo;
	}*/

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

	/*public void setFillAll(String fill) {
		ensureLineList();
		for (SVGLine line :lineList) {
			line.setFill(fill);
		}
	}

	public void setStrokeWidthAll(double d) {
		ensureLineList();
		for (SVGLine line :lineList) {
			line.setStrokeWidth(d);
		}
	}*/

	public SVGElement getSVGElement() {
		return null;
		//TODO change this if needed, likewise in HatchedBond etc.
		/*SVGG g = new SVGG();
		for (SVGLine l : lineList) {
			g.appendChild(l);
		}
		return g;*/
	}
	
	/*public SVGLine getBackbone() {
		if (backbone == null) {
			if (lineList != null && lineList.size() == 2) {
				backbone = lineList.get(0).getMeanLine(lineList.get(1), ANGLE_EPS);
			}
		}
		return backbone;
	}*/
	
	/** 
	 * @return null as tram lines aren't represented by a single point
	 */
	/*public Real2 getPoint() {
		return null;
	}*/

	/*public void addJunction(Junction junction) {
		joinManager.add(junction);
	}

	public List<Junction> getJunctionList() {
		return joinManager == null ? new ArrayList<Junction>() : joinManager.getJunctionList();
	}
	
	@Override
	public Double getRelativeDistance() {
		return relativeDistance;
	}*/
	
	public String toString() {
		return "Double bond\n ... " + Arrays.toString(getJoinPoints().toArray());
	}

}