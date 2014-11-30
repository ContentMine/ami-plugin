package org.xmlcml.ami.visitor.chem;

public abstract class JoinableWithBackbone extends Joinable {//implements Joinable {

	/*private String id;
	
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}
	
	public Real2 intersectionWith(Joinable otherJoinable) {
		Real2 intersectionPoint = null;
		if (otherJoinable != null) {
			SVGLine otherBackbone = otherJoinable.getBackbone();
			Real2 otherPoint = otherJoinable.getPoint();
			if (getBackbone() != null && otherBackbone != null) {
				intersectionPoint = getBackbone().getIntersection(otherBackbone);
				if (Double.isNaN(intersectionPoint.getX()) || Double.isNaN(intersectionPoint.getY())) {
					if (getBackbone().isParallelTo(otherBackbone, new Angle(1, Units.RADIANS))) {
						double dist1 = getBackbone().getEuclidLine().getXY(0).getDistance(otherBackbone.getEuclidLine().getXY(1));
						double dist2 = getBackbone().getEuclidLine().getXY(1).getDistance(otherBackbone.getEuclidLine().getXY(0));
						if (dist1 < dist2) {
							intersectionPoint = getBackbone().getEuclidLine().getXY(0).getMidPoint(otherBackbone.getEuclidLine().getXY(1));
						} else {
							intersectionPoint = getBackbone().getEuclidLine().getXY(1).getMidPoint(otherBackbone.getEuclidLine().getXY(0));
						}
					} else {
						double dist1 = getBackbone().getEuclidLine().getXY(0).getDistance(otherBackbone.getEuclidLine().getXY(0));
						double dist2 = getBackbone().getEuclidLine().getXY(1).getDistance(otherBackbone.getEuclidLine().getXY(1));
						if (dist1 < dist2) {
							intersectionPoint = getBackbone().getEuclidLine().getXY(0).getMidPoint(otherBackbone.getEuclidLine().getXY(0));
						} else {
							intersectionPoint = getBackbone().getEuclidLine().getXY(1).getMidPoint(otherBackbone.getEuclidLine().getXY(1));
						}
					}
				}
				if (getBackbone().getEuclidLine().getXY(0).getDistance(intersectionPoint) > getBackbone().getLength() * getRelativeDistance() && getBackbone().getEuclidLine().getXY(1).getDistance(intersectionPoint) > getBackbone().getLength() * getRelativeDistance()) {
					return null;
				}
				if (otherBackbone.getEuclidLine().getXY(0).getDistance(intersectionPoint) > otherBackbone.getLength() * getRelativeDistance() && otherBackbone.getEuclidLine().getXY(1).getDistance(intersectionPoint) > otherBackbone.getLength() * getRelativeDistance()) {
					return null;
				}
			} else if (getPoint() != null) {
				intersectionPoint = (otherPoint == null) ? 
						getPoint() : getPoint().getMidPoint(otherPoint);
			} else {
				intersectionPoint = otherPoint;
			}
		}
		return intersectionPoint;
	}

	public abstract Double getRelativeDistance();*/

}