package org.xmlcml.ami.visitor.chem;


/** 
 * Joins graphic components by chemical rules.
 * 
 * @author pm286
 */
public class JoinManager {

	/*private final static Logger LOG = Logger.getLogger(JoinManager.class);
	
	private List<JoinPoint> joinPoints;
	private List<Junction> junctionList;

	public JoinManager() {
		
	}
	
	List<JoinPoint> getCommonJoinPointList(JoinManager otherJoiner) {
		List<JoinPoint> commonJoinPointList = new ArrayList<JoinPoint>();
		ensureJoinPoints();
		if (this.equals(otherJoiner)) {
			throw new RuntimeException("Cannot join to self");
		}
		List<JoinPoint> otherJoinPoints = otherJoiner.getJoinPoints();
		for (int i = 0; i < joinPoints.size(); i++) {
			JoinPoint joinPoint = joinPoints.get(i);
			for (int j = 0; j < otherJoinPoints.size(); j++) {
				JoinPoint otherJoinPoint = otherJoinPoints.get(j);
				double distanceToOther = joinPoint.getDistanceTo(otherJoinPoint);
				LOG.trace("join distance "+distanceToOther);
				if (joinPoint.getRadius() + otherJoinPoint.getRadius() > distanceToOther) {
					JoinPoint commonJoinPoint = null;
					if (joinPoint.getPriority() > otherJoinPoint.getPriority()) {
						commonJoinPoint = joinPoint;
					} else {
						commonJoinPoint = otherJoinPoint;
					}
					if (commonJoinPoint != null) {
						commonJoinPointList.add(commonJoinPoint);
					}
				}
			}
		}
		return commonJoinPointList;
	}
		
	private void ensureJoinPoints() {
		if (this.joinPoints == null) {
			joinPoints = new ArrayList<JoinPoint>();
		}
	}
	
	public List<JoinPoint> getJoinPoints() {
		return joinPoints;
	}
	
	void add(JoinPoint point) {
		ensureJoinPoints();
		joinPoints.add(point);
	}
	
	*//**
	 * @param joinable TODO
	 * @return
	 *//*
	JoinPoint getCommonPoint(Joinable joinable) {
		JoinPoint point = null;
		if (joinable != null) {
			List<JoinPoint> joinPointList = getCommonJoinPointList(joinable.getJoinPoints());
			point = joinPointList.size() > 0 ? joinPointList.get(0) : null;
		}
		return point;
	}

	public static List<Joinable> makeJoinableList(List<? extends SVGElement> elementList) {
		List<Joinable> joinableList = new ArrayList<Joinable>();
		for (SVGElement element : elementList) {
			Joinable joinable = createJoinable(element);
			if (joinable != null) {
				joinableList.add(joinable);
			}
		}
		return joinableList;
	}

	private static Joinable createJoinable(SVGElement element) {
		Joinable joinable = null;
		if (element instanceof SVGLine) {
			joinable = new SingleBond((SVGLine) element);
		} else if (element instanceof SVGText) {
			joinable = new JoinableText((SVGText) element);
 		} else if (element instanceof SVGPolygon && ((SVGPolygon) element).getLineList().size() == 3) {
			joinable = new WedgeBond((SVGPolygon) element);
 		} else {
 			LOG.debug("Unknown joinable: "+element);
 		}
		return joinable;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("joinPoints: (");
		for (JoinPoint joinPoint : joinPoints) {
			sb.append(joinPoint);
		}
		sb.append(")");
		return sb.toString();
	}

	public void add(Junction junction) {
		ensureJunctionList();
		if (!junctionList.contains(junction)) {
			junctionList.add(junction);
		}
	}

	private void ensureJunctionList() {
		if (junctionList == null) {
			junctionList = new ArrayList<Junction>();
		}
	}

	public List<Junction> getJunctionList() {
		return junctionList;
	}*/

}