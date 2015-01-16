package org.xmlcml.ami.visitor.chem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGText;

public class Charge extends Joinable {

	private final static Logger LOG = Logger.getLogger(Charge.class);

	private static final double CHARGE_PRIORITY = 0.2;

	private List<SVGLine> lines;
	private SVGText text;

	private Double radiusExpansion = 3.2;

	public Charge(List<SVGLine> lines) {
		if (lines.size() != 1 && lines.size() != 2) {
			throw new IllegalArgumentException();
		}
		this.lines = lines;
		addJoinPointFromLines();
	}

	public Charge(SVGLine i) {
		lines = new ArrayList<SVGLine>();
		lines.add(i);
		addJoinPointFromLines();
	}

	public Charge(SVGLine i, SVGLine j) {
		lines = new ArrayList<SVGLine>();
		lines.add(i);
		lines.add(j);
		addJoinPointFromLines();
	}
	
	public Charge(SVGText text) {
		if ("+".equals(text.getText()) || "-".equals(text.getText())) {
			this.text = text;
		} else {
			throw new IllegalArgumentException();
		}
		addJoinPointFromText();
	}

	private void addJoinPointFromText() {
		JoinPoint joinPoint;
		joinPoint = new JoinPoint(text.getCentrePointOfFirstCharacter(), (text.getWidthOfFirstCharacter() / 2) * radiusExpansion);
		getJoinPoints().add(joinPoint);
	}

	private void addJoinPointFromLines() {
		Real2 coord = lines.get(0).getMidPoint();
		if (coord != null) {
			JoinPoint joinPoint = new JoinPoint(coord, (lines.get(0).getLength() / 2) * radiusExpansion);
			getJoinPoints().add(joinPoint);
		}
	}
	
	public SVGElement getSVGElement() {
		return text;
	}

	public double getPriority() {
		return CHARGE_PRIORITY;
	}
	
	public int getCharge() {
		return (text != null ? (text.getText().equals("+") ? 1 : -1) : (lines.size() == 1 ? -1 : 1));
	}
	
	public String toString() {
		return "Charge\n ... " + Arrays.toString(getJoinPoints().toArray());
	}
	
}