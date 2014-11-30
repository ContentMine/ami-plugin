package org.xmlcml.ami.visitor.chem;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;

import java.util.Arrays;

public class SingleBond extends Joinable {
	
	private final static Logger LOG = Logger.getLogger(SingleBond.class);

	private static final double SINGLE_BOND_PRIORITY = 5.0;

	private SVGLine svgLine;

	//private static final double DEFAULT_RELATIVE_DISTANCE_TO_INTERSECTION = 0.5;
	
	//private double relativeDistance = DEFAULT_RELATIVE_DISTANCE_TO_INTERSECTION;

	public SingleBond(SVGLine svgLine) {
		this.svgLine = svgLine;
		addJoinPoints();
	}

	public double getPriority() {
		return SINGLE_BOND_PRIORITY;
	}
	
	public void addJoinPoints() {
		//if (svgLine.getLength() < minimumLengthForJoinPointsToBeAtEnds)
		JoinPoint j1 = new JoinPoint(svgLine.getXY(0));
		JoinPoint j2 = new JoinPoint(svgLine.getXY(1));
		getJoinPoints().add(j1);
		getJoinPoints().add(j2);
		j1.setRadius(Math.min(j1.getRadius(), svgLine.getLength() / 2));
		j2.setRadius(Math.min(j2.getRadius(), svgLine.getLength() / 2));
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
		return svgLine.getId();
	}

	public SVGElement getSVGElement() {
		return svgLine;
	}

	/*public SVGLine getBackbone() {
		return svgLine;
	}*/

	/** 
	 * @return null as lines aren't represented by a single point
	 */
	/*public Real2 getPoint() {
		return null;
	}

	public void addJunction(Junction junction) {
		joinManager.add(junction);
	}
	
	public List<Junction> getJunctionList() {
		return joinManager == null ? new ArrayList<Junction>() : joinManager.getJunctionList();
	}*/
	
	public String toString() {
		return svgLine.toXML() + "\n ... " + Arrays.toString(getJoinPoints().toArray());
	}

	/*@Override
	public Double getRelativeDistance() {
		return relativeDistance;
	}*/
	
}
