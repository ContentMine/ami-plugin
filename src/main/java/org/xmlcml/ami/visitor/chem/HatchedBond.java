package org.xmlcml.ami.visitor.chem;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Units;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;

public class HatchedBond extends Joinable {
	
	private final static Logger LOG = Logger.getLogger(HatchedBond.class);

	private static final double HATCHED_BOND_PRORITY = 2.0;

	private List<SVGLine> lines;
	
	private static final double DEFAULT_RELATIVE_DISTANCE_TO_INTERSECTION = 0.5;
	private static final double DEFAULT_RELATIVE_DISTANCE_FROM_SINGLE_LINE = 1;
	
	private double relativeDistance = DEFAULT_RELATIVE_DISTANCE_TO_INTERSECTION;
	private double relativeDistanceFromSingleLine = DEFAULT_RELATIVE_DISTANCE_FROM_SINGLE_LINE;

	private boolean unspecifiedDirection;

	public HatchedBond(List<SVGLine> lines) {
		this.lines = lines;
		addJoinPoints();
	}

	public double getPriority() {
		return HATCHED_BOND_PRORITY;
	}
	
	public boolean isUnspecifiedDirection() {
		return unspecifiedDirection;
	}
	
	public void addJoinPoints() {
		SVGLine lastLine = lines.get(lines.size() - 1);
		SVGLine shortestLine;
		SVGLine longestLine;
		if (lines.get(0).getLength() > lastLine.getLength()) {
			longestLine = lines.get(0);
			shortestLine = lastLine;
		} else {
			longestLine = lastLine;
			shortestLine = lines.get(0);
		}
		try {
			double scaleFactor = (longestLine.getMidPoint().getDistance(shortestLine.getMidPoint()) + lines.get(0).getMidPoint().getDistance(lines.get(1).getMidPoint())) / longestLine.getMidPoint().getDistance(shortestLine.getMidPoint());
			Real2 newPoint1 = new Real2(longestLine.getMidPoint().getX() + (shortestLine.getMidPoint().getX() - longestLine.getMidPoint().getX()) * scaleFactor, longestLine.getMidPoint().getY() + (shortestLine.getMidPoint().getY() - longestLine.getMidPoint().getY()) * scaleFactor);
			scaleFactor = (longestLine.getMidPoint().getDistance(shortestLine.getMidPoint()) + lines.get(0).getMidPoint().getDistance(lines.get(1).getMidPoint()) / 2) / longestLine.getMidPoint().getDistance(shortestLine.getMidPoint());
			Real2 newPoint2 = new Real2(shortestLine.getMidPoint().getX() + (longestLine.getMidPoint().getX() - shortestLine.getMidPoint().getX()) * scaleFactor, shortestLine.getMidPoint().getY() + (longestLine.getMidPoint().getY() - shortestLine.getMidPoint().getY()) * scaleFactor);
			getJoinPoints().add(new JoinPoint(newPoint1));
			getJoinPoints().add(new JoinPoint(newPoint2));
			unspecifiedDirection = false;
		} catch (IndexOutOfBoundsException e) {
			Real2 perpendicularVector = longestLine.getEuclidLine().getVector().getTransformed(new Transform2(new Angle(90, Units.DEGREES))).getUnitVector();
			getJoinPoints().add(new JoinPoint(longestLine.getMidPoint().plus(perpendicularVector.multiplyBy(longestLine.getLength() * relativeDistanceFromSingleLine))));
			getJoinPoints().add(new JoinPoint(longestLine.getMidPoint().subtract(perpendicularVector.multiplyBy(longestLine.getLength() * relativeDistanceFromSingleLine))));
			unspecifiedDirection = true;
		}
		//Vector2 perp = new Vector2(-smallestLine.getEuclidLine().getVector().getUnitVector().y, smallestLine.getEuclidLine().getVector().getUnitVector().x);
		//perp = (Vector2) perp.multiplyBy(lines.get(0).getMidPoint().getDistance(lines.get(1).getMidPoint()));
		//new SVGLine(longestLine.getMidPoint(), shortestLine.getMidPoint());
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

	public JoinPoint getIntersectionPoint(WedgeBond polygon) {
		return joinManager.getCommonPoint(polygon);
	}

	public JoinPoint getIntersectionPoint(HatchedBond polygon) {
		return joinManager.getCommonPoint(polygon);
	}

	public JoinManager getJoinPointList() {
		return joinManager;
	}*/

	public SVGElement getSVGElement() {
		return null;
		/*SVGG g = new SVGG();
		for (SVGLine l : lineList) {
			g.appendChild(l);
		}
		return g;*/
	}

	/*public SVGLine getBackbone() {
		try {
			return new SVGLine(joinManager.getJoinPoints().get(0).getPoint(), joinManager.getJoinPoints().get(1).getPoint());
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Real2 getPoint() {
		if (joinManager.getJoinPoints().size() == 1){
			return joinManager.getJoinPoints().get(0).getPoint();
		} else {
			return null;
		}
	}

	public void addJunction(Junction junction) {
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
		return "Hatched bond\n ... " + Arrays.toString(getJoinPoints().toArray());
	}
	
}