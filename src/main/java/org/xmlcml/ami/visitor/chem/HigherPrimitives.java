package org.xmlcml.ami.visitor.chem;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.graphics.svg.SVGLine;

/** 
 * Holds primitives that are being built.
 * 
 * @author pm286
 */
public class HigherPrimitives {

	private List<SVGLine> lines;
	private List<DoubleBond> doubleBonds;
	private List<Junction> junctions;
	private List<Joinable> joinables;
	private List<HatchedBond> hatchedBonds;
	private List<Charge> charges;

	private void ensureLineList() {
		if (lines == null) {
			lines = new ArrayList<SVGLine>();
		}
	}

	public void addSingleLines(List<SVGLine> lines) {
		ensureLineList();
		if (lines != null) {
			this.lines.addAll(lines);
		}
	}

	public List<DoubleBond> getDoubleBondList() {
		return doubleBonds;
	}

	public List<HatchedBond> getHatchedBondList() {
		return hatchedBonds;
	}

	public List<Junction> getJunctionList() {
		return junctions;
	}

	public List<Joinable> getJoinableList() {
		return joinables;
	}

	public List<SVGLine> getLineList() {
		return lines;
	}

	public List<Charge> getLineChargeList() {
		return charges;
	}

	public void addJoinableList(List<Joinable> joinables) {
		ensureJoinableList();
		this.joinables.addAll(joinables);
	}

	private void ensureJoinableList() {
		if (joinables == null) {
			joinables = new ArrayList<Joinable>();
		}
	}
	
	public void setDoubleBondList(List<DoubleBond> doubleBonds) {
		this.doubleBonds = doubleBonds;
	}

	public void setHatchedBondList(List<HatchedBond> hatchedBonds) {
		this.hatchedBonds = hatchedBonds;
	}
	
	public void setJunctionList(List<Junction> junctions) {
		this.junctions = junctions;
	}
	
	public void setChargeList(List<Charge> charges) {
		this.charges = charges;
	}

}