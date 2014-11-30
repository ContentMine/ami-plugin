package org.xmlcml.ami.visitor.chem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Units;
import org.xmlcml.graphics.svg.SVGLine;

public class DoubleBondManager {

	private static Logger LOG = Logger.getLogger(DoubleBondManager.class);
	
	private final static Double EPS = 0.000001;//Rounding errors; TODO add default constants

	private static final double PARALLEL_THRESHOLD_IN_RADIANS = 0.3;
	static final double DEFAULT_MAXIMUM_RELATIVE_SEPARATION = 0.5;//0.4;//0.35;//This is tricky for very short tramlines
	private static final double DEFAULT_MINIMUM_RELATIVE_SEPARATION = 0.08;
	private static final double DEFAULT_MIMIMUM_LENGTH_RATIO = 0.5;
	static final double DEFAULT_MAXIMUM_ABSOLUTE_SEPARATION = 4.3;
	private static final double DEFAULT_SHRINK_FACTOR_FOR_OVERLAP_CHECK = 0.5;

	private List<DoubleBond> doubleBondList;
	
	private Angle angleEps = new Angle(PARALLEL_THRESHOLD_IN_RADIANS, Units.RADIANS);
	private double maxDoubleBondRelativeSeparation = DEFAULT_MAXIMUM_RELATIVE_SEPARATION;
	private double minDoubleBondRelativeSeparation = DEFAULT_MINIMUM_RELATIVE_SEPARATION;
	private double minRelativeLineLength = DEFAULT_MIMIMUM_LENGTH_RATIO;
	private double maxDoubleBondAbsoluteSeparation = DEFAULT_MAXIMUM_ABSOLUTE_SEPARATION;
	private double overlapProportion = DEFAULT_SHRINK_FACTOR_FOR_OVERLAP_CHECK;
	
	private Set<SVGLine> usedLineSet;

	public DoubleBondManager() {
		
	}
	
	public void add(DoubleBond line) {
		ensureDoubleBondListAndUsedLineSet();
		doubleBondList.add(line);
	}

	private void ensureDoubleBondListAndUsedLineSet() {
		if (doubleBondList == null) {
			doubleBondList = new ArrayList<DoubleBond>();
	 		if (usedLineSet == null) {
		 		usedLineSet = new HashSet<SVGLine>();
		 	}
		}
	}
	
	public DoubleBond createDoubleBond(SVGLine lineI, SVGLine lineJ) {
		DoubleBond doubleBond = null;
		Double length1 = lineI.getXY(0).getDistance(lineI.getXY(1));//TODO getLength();
		Double length2 = lineJ.getXY(0).getDistance(lineJ.getXY(1));//TODO getLength();
		Double longer = (length1 > length2 ? length1 : length2);
		Double shorter = (length1 > length2 ? length2 : length1);
		if (lineI == null || lineJ == null) {
			return doubleBond;
		}
		if (shorter / longer > minRelativeLineLength) {
			if (lineI.isParallelOrAntiParallelTo(lineJ, angleEps)) {
				Double dist1 = lineI.calculateUnsignedDistanceBetweenLines(lineJ, new Angle(360, Units.DEGREES));//angleEps);//TODO fix Euclid
				SVGLine lineIAgain = (SVGLine) lineI.copy();
				lineIAgain.setXY(lineI.getXY(1), 0);
				lineIAgain.setXY(lineI.getXY(0), 1);
				Double dist2 = lineIAgain.calculateUnsignedDistanceBetweenLines(lineJ, new Angle(360, Units.DEGREES));//angleEps);
				LOG.trace(dist1 + " " + dist2);
				if (dist1 < maxDoubleBondAbsoluteSeparation && dist2 < maxDoubleBondAbsoluteSeparation && dist1 < longer * maxDoubleBondRelativeSeparation && dist1 > longer * minDoubleBondRelativeSeparation && dist2 < longer * maxDoubleBondRelativeSeparation && dist2 > longer * minDoubleBondRelativeSeparation) {
					double reductionFirstPointLambda = 0.5 - overlapProportion / 2;
					double reductionSecondPointLambda = 0.5 + overlapProportion / 2;
					SVGLine lineI2 = new SVGLine(lineI.getEuclidLine().createPointOnLine(reductionFirstPointLambda * length1), lineI.getEuclidLine().createPointOnLine(reductionSecondPointLambda * length1));
					SVGLine lineJ2 = new SVGLine(lineJ.getEuclidLine().createPointOnLine(reductionFirstPointLambda * length2), lineJ.getEuclidLine().createPointOnLine(reductionSecondPointLambda * length2));
					if (lineI2.overlapsWithLine(lineJ2, EPS) || lineJ2.overlapsWithLine(lineI2, EPS)) {
						doubleBond = new DoubleBond(lineI, lineJ);
					}
				}
			}
		}
		return doubleBond;
	}

	public List<DoubleBond> createDoubleBondList(List<SVGLine> lineList, long timeout) throws TimeoutException {
		long startTime = System.currentTimeMillis();
		if (doubleBondList == null) {
			ensureDoubleBondListAndUsedLineSet();
			for (int i = 0; i < lineList.size() - 1; i++) {
				SVGLine lineI = lineList.get(i);
				for (int j = i + 1; j < lineList.size(); j++) {
					if (System.currentTimeMillis() - startTime >= timeout) {
						throw new TimeoutException("Took too long to look for double bonds");
					}
					SVGLine lineJ = lineList.get(j);
					DoubleBond doubleBond = createDoubleBond(lineI, lineJ);
					if (doubleBond != null) {
						LOG.trace("Double bond "+lineI.getId()+" "+lineJ.getId());
	 					doubleBond.setID("doublebond."+lineI.getId()+"."+lineJ.getId());
	 					doubleBondList.add(doubleBond);
	 					usedLineSet.add(lineI);
	 					usedLineSet.add(lineJ);
					}
				}
			}
		}
		return doubleBondList;
	}
	
	public List<DoubleBond> createDoubleBondListWithoutReusingLines(List<SVGLine> lineList, long timeout) throws TimeoutException {
		long startTime = System.currentTimeMillis();
		if (doubleBondList == null) {
			ensureDoubleBondListAndUsedLineSet();
			for (int i = 0; i < lineList.size() - 1; i++) {
				SVGLine lineI = lineList.get(i);
				for (int j = i + 1; j < lineList.size(); j++) {
					if (System.currentTimeMillis() - startTime >= timeout) {
						throw new TimeoutException("Took too long to look for double bonds");
					}
					SVGLine lineJ = lineList.get(j);
					if (!usedLineSet.contains(lineI) && !usedLineSet.contains(lineJ)) {
						DoubleBond doubleBond = createDoubleBond(lineI, lineJ);
						if (doubleBond != null) {
							LOG.trace("Double bond "+lineI.getId()+" "+lineJ.getId());
		 					doubleBond.setID("doublebond."+lineI.getId()+"."+lineJ.getId());
		 					doubleBondList.add(doubleBond);
		 					usedLineSet.add(lineI);
		 					usedLineSet.add(lineJ);
						}
					}
				}
			}
		}
		return doubleBondList;
	}
	
	public Angle getAngleEps() {
		return angleEps;
	}

	public void setAngleEps(Angle angleEps) {
		this.angleEps = angleEps;
	}

	public Double getMaxDoubleBondSeparationFactor() {
		return maxDoubleBondRelativeSeparation;
	}

	public void setMaxDoubleBondSeparationFactor(Double maxDoubleBondSeparationFactor) {
		this.maxDoubleBondRelativeSeparation = maxDoubleBondSeparationFactor;
	}
	
	public Double getMinDoubleBondSeparationFactor() {
		return minDoubleBondRelativeSeparation;
	}

	public void setMinDoubleBondSeparationFactor(Double minDoubleBondSeparationFactor) {
		this.minDoubleBondRelativeSeparation = minDoubleBondSeparationFactor;
	}

	public Double getMinRelativeLineLength() {
		return minRelativeLineLength;
	}

	public void setMinRelativeLineLength(Double minRelativeLineLength) {
		this.minRelativeLineLength = minRelativeLineLength;
	}

	public List<DoubleBond> getDoubleBondList() {
		return doubleBondList;
	}

	public List<SVGLine> removeUsedDoubleBondPrimitives(List<SVGLine> lineList) {
 		for (int i = lineList.size() - 1; i >= 0; i--) {
 			if (usedLineSet.contains(lineList.get(i))) {
 				lineList.remove(i);
 			}
 		}
 		return lineList;
 	}
	
}