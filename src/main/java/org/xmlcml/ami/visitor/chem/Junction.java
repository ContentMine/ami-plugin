package org.xmlcml.ami.visitor.chem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitor.chem.Joinable.JoinPoint;
import org.xmlcml.euclid.Real2;

/** 
 * A junction between two or more objects.
 * 
 * @author pm286
 */
public class Junction {//extends SVGCircle {

	private final static Logger LOG = Logger.getLogger(Junction.class);
	
	private List<Joinable> joinables;
	private Real2 coordinates;
	//private JoinableScriptWord word;
	private Collection<JoinPoint> joinPoints;
	
	private static double EPS = 2.0; // initial guess

	public Junction(Collection<JoinPoint> joinPoints) {
		this.joinPoints = joinPoints;
	}

	/** 
	 * Gets the JoinableScriptWord for the junction (everything takes preference over hydrogens)
	 * 
	 * @return
	 */
	/*public JoinableScriptWord getWord() {
		if (word == null) {
			double highestPriority = 0;
			ensureJoinableList();
			for (Joinable joinable : joinables) {
				//if (joinable instanceof JoinableScriptWord) {
					//if (!((JoinableScriptWord) joinable).getScriptWord().toUnderscoreAndCaretString().equals("H") || word == null) {
				if (joinable.getPriority() > highestPriority) {
					highestPriority = joinable.getPriority();
					word = (JoinableScriptWord) joinable;
				}
					//}
				//}
			}
		}
		return word;
	}*/

	public Junction(JoinPoint p) {
		this(Arrays.asList(new JoinPoint[]{p}));
	}

	/** 
	 * Gets the identifier of the junction
	 */
	public String getID() {
		//SVGText svgText = getSvgText();
		//return svgText == null ? null: svgText.getId();
		return null;//super.getID();
	}
	
	/*private void add(Joinable joinable) {
		ensureJoinableList();
		if (!joinableList.contains(joinable)) {
			joinableList.add(joinable);
		}
	}*/

	private void ensureJoinableList() {
		if (joinables == null) {
			joinables = new ArrayList<Joinable>();
			for (JoinPoint j : joinPoints) {
				joinables.add(j.getJoinable());
			}
		}
	}
	
	/*public boolean containsCommonPoints(Junction labile) {
		double dist = joinPoint.getDistanceTo(labile.joinPoint);
		if (dist < EPS) {
			LOG.trace(dist);
			return true;
		}
		return false;
	}

	public void transferDetailsTo(Junction fixed) {
		for (Joinable joinable : joinableList) {
			if (fixed.joinableList.contains(joinable)) {
			} else { 
				fixed.joinableList.add(joinable);
			}
			if (fixed.coordinates != null) {
				fixed.coordinates = fixed.coordinates.getMidPoint(coordinates);
			}
		}
	}
	
	public List<Joinable> getJoinableList() {
		return joinableList;
	}*/
	
	public Collection<JoinPoint> getJoinPoints() {
		return joinPoints;
	}
	
	public List<Joinable> getJoinables() {
		ensureJoinableList();
		return joinables;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("J: ");
		for (Joinable joinable : joinables) {
			sb.append(" ["+joinable.getSVGElement().getClass().getSimpleName()+": "+joinable.getID()+"] ");
		}
		return sb.toString();
	}

	public Real2 getCoordinates() {
		double highestPriority = 0;
		double distanceFromHighestPriorityJoinPointToNearestLowerPriorityJoinPoint = Double.MAX_VALUE;
		if (coordinates == null) {
			for (JoinPoint j : joinPoints) {
				double priority = j.getJoinable().getPriority();
				if (priority > highestPriority) {
					highestPriority = priority;
					coordinates = j.getPoint();
					distanceFromHighestPriorityJoinPointToNearestLowerPriorityJoinPoint = Double.MAX_VALUE;
					for (JoinPoint j2 : joinPoints) {
						if (j2.getJoinable().getPriority() < j.getJoinable().getPriority() && j.getDistanceTo(j2) < distanceFromHighestPriorityJoinPointToNearestLowerPriorityJoinPoint) {
							distanceFromHighestPriorityJoinPointToNearestLowerPriorityJoinPoint = j.getDistanceTo(j2);
						}
					}
				} else if (priority == highestPriority) {
					for (JoinPoint j2 : joinPoints) {
						if (j2.getJoinable().getPriority() < j.getJoinable().getPriority() && j.getDistanceTo(j2) < distanceFromHighestPriorityJoinPointToNearestLowerPriorityJoinPoint) {
							distanceFromHighestPriorityJoinPointToNearestLowerPriorityJoinPoint = j.getDistanceTo(j2);
							coordinates = j.getPoint();
						}
					}
				}
			}
		}
		return coordinates;
	}

	/*public void setCoordinates(Real2 coords) {
		this.coordinates = coords;
	}*/

	public Integer getCharge() {
		ensureJoinableList();
		Integer charge = null;
		for (Joinable j : joinables) {
			if (j instanceof Charge) {
				charge = ((Charge) j).getCharge();
			}
		}
		return charge;
	}

}