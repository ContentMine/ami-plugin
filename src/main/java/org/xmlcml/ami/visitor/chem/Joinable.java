package org.xmlcml.ami.visitor.chem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Units;
import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGText;

/**
 * Holds SVGElements such as SVGLine, TramLine or SVGText.
 * <p>
 * A Joinable exposes the element, its id, a Joiner (which interacts
 * with Joiners on other elements), a backbone (the abstract line representing
 * the Joinable) and/or a point. Normally a Joinable has either a backbone (e.g. TramLine)
 * or a point (JoinableText).
 * <p>
 * Will be expanded to include other objects later.
 *
 * @author pm286
 */
public abstract class Joinable {
	
	private static final double DEFAULT_TOLERANCE_FOR_PARALLEL_JOINABLES = 0.1;
	private static final double DEFAULT_JOIN_POINT_RADIUS = 1.05;
	
	private static Angle parallelTolerance = new Angle(DEFAULT_TOLERANCE_FOR_PARALLEL_JOINABLES, Units.RADIANS);
	private List<JoinPoint> joinPoints = new ArrayList<JoinPoint>();
	private String id;

	/** 
	 * @return the delegated element.
	 */
	abstract SVGElement getSVGElement();

	/** 
	 * Gets the id.
	 * <p>
	 * Normally of the delegated element.
	 * 
	 * @return
	 */
	public String getID() {
		return id;
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public abstract double getPriority();
	
	/** 
	 * Exposes the joiner.
	 * <p>
	 * Testing connectedness is done through the joiners for each Joinable.
	 * 
	 * @return the joiner
	 */
	public List<JoinPoint> getJoinPoints() {
		return joinPoints;
	}
	
	public Set<JoinPoint> overlapWith(Joinable other) {
		List<JoinPoint> pointsForThis = getJoinPoints();
		List<JoinPoint> pointsForOther = other.getJoinPoints();
		Set<JoinPoint> results = new LinkedHashSet<JoinPoint>();
		for (JoinPoint p : pointsForThis) {
			for (JoinPoint q : pointsForOther) {
				if (p.isCloseTo(q)) {
					results.add(p);
					results.add(q);
				}
			}
		}
		return (results.size() > 0 && (results.size() < 3 || (results.size() < pointsForOther.size() + pointsForThis.size())) ? results : null);
	}

	/** 
	 * Gets intersection with another joinable.
	 * 
	 * @param joinable
	 * @return intersection or null
	 */
	//Real2 intersectionWith(Joinable joinable);

	/** 
	 * One of the polymorphic intersections (must be one implemented for each type of Joinable)
	 * 
	 * @param shape
	 * @return
	 */
    //JoinPoint getIntersectionPoint(Joinable shape);
    
	/** 
	 * One of the polymorphic intersections (must be one implemented for each type of Joinable)
	 * 
	 * @param line
	 * @return
	 */
    //JoinPoint getIntersectionPoint(SingleBond line);
    
	/** 
	 * One of the polymorphic intersections (must be one implemented for each type of Joinable)
	 * 
	 * @param text
	 * @return
	 */
    //JoinPoint getIntersectionPoint(JoinableText text);
    
	/** 
	 * One of the polymorphic intersections (must be one implemented for each type of Joinable)
	 * 
	 * @param tramLine
	 * @return
	 */
    //JoinPoint getIntersectionPoint(DoubleBond tramLine);
    
	/** 
	 * One of the polymorphic intersections (must be one implemented for each type of Joinable)
	 * 
	 * @param triangle
	 * @return
	 */
    //JoinPoint getIntersectionPoint(WedgeBond triangle);
	
	/** 
	 * One of the polymorphic intersections (must be one implemented for each type of Joinable)
	 * 
	 * @param triangle
	 * @return
	 */
    //JoinPoint getIntersectionPoint(HatchedBond triangle);

    /**
     * Determines which Joinable has its JoinPoint used in Junctions.
     * 
     * @return the priority
     */
	//double getPriority();

	//void addJunction(Junction junction);

	//List<Junction> getJunctionList();
	
	static boolean doTextsJoin(Joinable joinableI, Joinable joinableJ, double textCoordinateTolerance, double maximumCharacterXRangeOverlapWhenAdjacent, double allowedFontSizeVariation) {
		boolean result = false;
		SVGText textI = (SVGText) joinableI.getSVGElement();
		SVGText textJ = (SVGText) joinableJ.getSVGElement();
		if (Math.abs(textI.getFontSize() - textJ.getFontSize()) < allowedFontSizeVariation) {
			if (Math.abs(textI.getY() - textJ.getY()) < textCoordinateTolerance) {
				if (!textI.getText().equals("H") || !textJ.getText().equals("H")) {
					result = true;
				} else {
					return false;
				}
			} else {
				if ((textI.getText() != null && textI.getText().equals("H")) || (textJ.getText() != null && textJ.getText().equals("H"))) {
					if (Math.abs(textI.getX() - textJ.getX()) < textCoordinateTolerance) {
						result = true;
					}
				}
			}
		} else {
			if (textI.getEnSpaceCount(textJ) > -maximumCharacterXRangeOverlapWhenAdjacent || textJ.getEnSpaceCount(textI) > -maximumCharacterXRangeOverlapWhenAdjacent) {
				result = true;
			}
		}
		return result;
	}

	/** 
	 * Point where a graphic primitive may overlap or join to another.
	 * 
	 * @author pm286
	 */
	public class JoinPoint {

		//private final static Logger LOG = Logger.getLogger(JoinPoint.class);
		
		private Real2 point;
		private Double radius = DEFAULT_JOIN_POINT_RADIUS;
		
		public JoinPoint(Real2 point, Double radius) {
			this.point = point;
			this.radius = radius;
		}
		
		public JoinPoint(Real2 point) {
			this.point = point;
		}
		
		boolean isCloseTo(JoinPoint p) {
			return (p.getRadius() + getRadius() > getDistanceTo(p));
		}

		public double getDistanceTo(JoinPoint otherPoint) {
			return point.getDistance(otherPoint.getPoint());
		}

		public Real2 getPoint() {
			return point;
		}

		public double getRadius() {
			return radius;
		}

		public Joinable getJoinable() {
			return Joinable.this;
		}
		
		public void setRadius(double radius) {
			this.radius = radius;
		}

		/*public double getPriority() {
			return joinable.getPriority();
		}

		public String getId() {
			return joinable.getId();
		}*/
		
		public String toString() {
			StringBuilder sb = new StringBuilder("(");
			sb.append(Joinable.this.getID());
			sb.append(";");
			sb.append(radius);
			sb.append(";");
			sb.append(point.toString());
			sb.append(")");
			return sb.toString();
		}
	}

	static void sortJoinablesByX(List<? extends Joinable> joinables) {
		Collections.sort(joinables, new Comparator<Joinable>(){
			@Override
			public int compare(Joinable o1, Joinable o2) {
				return o1.getSVGElement().getX().compareTo(o2.getSVGElement().getX());
			}
		});
	}

	public static boolean areParallel(Joinable j1, Joinable j2) {
		try {
			Line2 line1 = new Line2(j1.getJoinPoints().get(0).getPoint(), j1.getJoinPoints().get(1).getPoint());
			Line2 line2 = new Line2(j2.getJoinPoints().get(0).getPoint(), j2.getJoinPoints().get(1).getPoint());
			return line1.isParallelOrAntiParallelTo(line2, parallelTolerance );
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}
	
}
