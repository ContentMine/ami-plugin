package org.xmlcml.ami.visitor.chem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.text.ScriptLine;
import org.xmlcml.svg2xml.text.ScriptWord;
import org.xmlcml.svg2xml.text.TextStructurer;

import com.google.common.collect.UnionFind;

public class JoinableText extends Joinable {

	private final static Logger LOG = Logger.getLogger(JoinableText.class);

	private static final double TEXT_PRIORITY = 6.0;
	static final double DEFAULT_Y_AXIS_SHIFT_FOR_CENTRE = 0.125;
	private static final double DEFAULT_RADIUS_EXPANSION = 0.85;

	private static double maximumOverlapInEnSpaces = ChemistryBuilder.WORD_DETECTION_DEFAULT_MAXIMUM_CHARACTER_X_RANGE_OVERLAP_WHEN_ADJACENT;
	private double yAxisShift = DEFAULT_Y_AXIS_SHIFT_FOR_CENTRE;
	private double radiusExpansion = DEFAULT_RADIUS_EXPANSION;//0.9;//.05;
	private static double minimumSpaceWidthInEnSpaces = ChemistryBuilder.DEFAULT_MINIMUM_SPACE_WIDTH_IN_EN_SPACES;
	private static double textCoordinateTolerance = ChemistryBuilder.WORD_DETECTION_DEFAULT_TEXT_Y_COORDINATE_TOLERANCE;
	private static double superscriptAndSubscriptOverlap = ChemistryBuilder.WORD_DETECTION_DEFAULT_MINIMUM_Y_RANGE_OVERLAP_FOR_SUPERSCRIPTS_AND_SUBSCRIPTS;
	private static double allowedFontSizeVariation = ChemistryBuilder.DEFAULT_FONT_SIZE_TOLERANCE;

	private SVGText svgText;

	public JoinableText(SVGText svgText) {
		this.svgText = svgText;
		addJoinPoints();
	}
	
	public void setJoinPoint(JoinPoint p) {
		getJoinPoints().add(p);
		p.setRadius(p.getRadius() * radiusExpansion);
	}

	private void addJoinPoints() {
		JoinPoint joinPoint;
		svgText.applyTransformAttributeAndRemove();
		Real2 coord = svgText.getCentrePointOfFirstCharacter();
		joinPoint = new JoinPoint(coord.plus(new Real2(0, svgText.getHeightOfFirstCharacter() * yAxisShift)), svgText.getRadiusOfFirstCharacter() * radiusExpansion);
		getJoinPoints().add(joinPoint);
	}

	/*public boolean canBeJoinedTo(Joinable joinable) {
		boolean joinsTo = false;
		if (joinable instanceof JoinableText) {
			// no-op for text-> text
		} else if (joinable instanceof SingleBond) {
			joinsTo = true;
		} else if (joinable instanceof DoubleBond) {
			joinsTo = true;
		}
		return joinsTo;
	}

	public JoinPoint getIntersectionPoint(Joinable joinable) {
		//return joinable.getJoinPointList().getCommonPoint(this);
		return joinable.getIntersectionPoint(this);
	}

	public JoinPoint getIntersectionPoint(SingleBond line) {
		return joinManager.getCommonPoint(line);
	}

	public JoinPoint getIntersectionPoint(JoinableText text) {
		//return joinPointList.getCommonPoint(text);
		//text cannnot join to itself
		return null;
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
	}

	public String getId() {
		return svgText.getId();
	}*/
	
	public SVGText getSVGElement() {
		return svgText;
	}

	/** 
	 * @return null (might change later)
	 */
	/*public SVGLine getBackbone() {
		return null;
	}*/

	/** 
	 * @return the centre of the first character
	 */
	/*public Real2 getPoint() {
		return (svgText == null ? null : svgText.getCentrePointOfFirstCharacter());
	}*/

	/** 
	 * @return this.getPoint()
	 */
	/*public Real2 intersectionWith(Joinable joinable) {
		return getPoint();
	}*/

	public double getPriority() {
		return TEXT_PRIORITY;
	}
	
	/*public void addJunction(Junction junction) {
		joinManager.add(junction);
	}
	
	public List<Junction> getJunctionList() {
		return joinManager == null ? new ArrayList<Junction>() : joinManager.getJunctionList();
	}*/
	
	public String toString() {
		return svgText.toXML() + "\n ... " + Arrays.toString(getJoinPoints().toArray());
	}
	
	/*boolean joinableTextOnSameLine(List<Joinable> joinables, double textCoordinateTolerance, boolean returnTrueIfNoOthers) {
		SVGText text = getSVGElement();
		int otherJoinableTexts = 0;
		for (Joinable joinable : joinables) {
			if (joinable instanceof JoinableText && joinable != this) {
				otherJoinableTexts++;
				if (Math.abs(((SVGText) joinable.getSVGElement()).getY() - text.getY()) < textCoordinateTolerance) {
					return true;
				}
			}
		}
		return (otherJoinableTexts == 0 && returnTrueIfNoOthers);
	}*/
	
	static boolean anyTextsInSameString(SVGText text, List<SVGText> others, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces, double subscriptAndSuperscriptOverlap) {
		for (SVGText other : others) {
			if (other != text) {
				if (areAdjacentInSameString(text, other, textCoordinateTolerance, maximumSpaceWidthInEnSpaces, subscriptAndSuperscriptOverlap)) {
					return true;
				}
			}
		}
		return false;
	}
	
	static boolean anyTextsInSameString(SVGText text, List<SVGText> others, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces) {
		for (SVGText other : others) {
			if (other != text) {
				if (areAdjacentInSameString(text, other, textCoordinateTolerance, maximumSpaceWidthInEnSpaces)) {
					return true;
				}
			}
		}
		return false;
	}
	
	static boolean anyTextsToRightInSameString(SVGText text, List<SVGText> others, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces, double subscriptAndSuperscriptOverlap) {
		for (SVGText other : others) {
			if (other != text) {
				if (secondAdjacentToRightOfFirstInSameString(text, other, textCoordinateTolerance, maximumSpaceWidthInEnSpaces, subscriptAndSuperscriptOverlap)) {
					return true;
				}
			}
		}
		return false;
	}
	
	static boolean anyTextsToRightInSameString(SVGText text, List<SVGText> others, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces) {
		for (SVGText other : others) {
			if (other != text) {
				if (secondAdjacentToRightOfFirstInSameString(text, other, textCoordinateTolerance, maximumSpaceWidthInEnSpaces)) {
					return true;
				}
			}
		}
		return false;
	}
	
	static boolean secondAdjacentToRightOfFirstInSameString(SVGText text, SVGText otherText, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces, double subscriptAndSuperscriptOverlap) {	
		return (text.getEnSpaceCount(otherText) != null && text.getEnSpaceCount(otherText) < maximumSpaceWidthInEnSpaces && text.getEnSpaceCount(otherText) > -maximumOverlapInEnSpaces && textsOnSameLine(text, otherText, textCoordinateTolerance, subscriptAndSuperscriptOverlap));
	}
	
	static boolean secondAdjacentToRightOfFirstInSameString(SVGText text, SVGText otherText, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces) {	
		return (text.getEnSpaceCount(otherText) != null && text.getEnSpaceCount(otherText) < maximumSpaceWidthInEnSpaces && text.getEnSpaceCount(otherText) > -maximumOverlapInEnSpaces && textsOnSameLine(text, otherText, textCoordinateTolerance));
	}
	
	static boolean areAdjacentInSameString(SVGText text, SVGText otherText, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces, double subscriptAndSuperscriptOverlap) {	
		return (otherText.getEnSpaceCount(text) != null && otherText.getEnSpaceCount(text) < maximumSpaceWidthInEnSpaces && text.getEnSpaceCount(otherText) != null && text.getEnSpaceCount(otherText) < maximumSpaceWidthInEnSpaces && textsOnSameLine(text, otherText, textCoordinateTolerance, subscriptAndSuperscriptOverlap));
	}
	
	static boolean areAdjacentInSameString(SVGText text, SVGText otherText, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces) {	
		return (otherText.getEnSpaceCount(text) != null && otherText.getEnSpaceCount(text) < maximumSpaceWidthInEnSpaces && text.getEnSpaceCount(otherText) != null && text.getEnSpaceCount(otherText) < maximumSpaceWidthInEnSpaces && textsOnSameLine(text, otherText, textCoordinateTolerance));
	}
	
	static boolean areAdjacentInSameString(JoinableText j, JoinableText otherJ, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces, double subscriptAndSuperscriptOverlap) {	
		return areAdjacentInSameString(j.getSVGElement(), otherJ.getSVGElement(), textCoordinateTolerance, maximumSpaceWidthInEnSpaces, subscriptAndSuperscriptOverlap);
	}
	
	static boolean areAdjacentInSameString(JoinableText j, JoinableText otherJ, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces) {	
		return areAdjacentInSameString(j.getSVGElement(), otherJ.getSVGElement(), textCoordinateTolerance, maximumSpaceWidthInEnSpaces);
	}

	static class AreInSameStringDetector {
		
		UnionFind<Joinable> texts;
		
		public AreInSameStringDetector(List<? extends Joinable> joinables, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces, double subscriptAndSuperscriptOverlap) {
			texts = UnionFind.create(joinables);
			for (Joinable j1 : joinables) {
				if (j1 instanceof JoinableText) {
					for (Joinable j2 : joinables) {
						if (j2 instanceof JoinableText && j1 != j2 && areAdjacentInSameString((JoinableText) j1, (JoinableText) j2, textCoordinateTolerance, maximumSpaceWidthInEnSpaces, subscriptAndSuperscriptOverlap)) {
							texts.union(j1, j2);
						}
					}
				}
			}
		}
		
		public AreInSameStringDetector(List<? extends Joinable> joinables, double textCoordinateTolerance, double maximumSpaceWidthInEnSpaces) {
			texts = UnionFind.create(joinables);
			for (Joinable j1 : joinables) {
				if (j1 instanceof JoinableText) {
					for (Joinable j2 : joinables) {
						if (j2 instanceof JoinableText && j1 != j2 && areAdjacentInSameString((JoinableText) j1, (JoinableText) j2, textCoordinateTolerance, maximumSpaceWidthInEnSpaces)) {
							texts.union(j1, j2);
						}
					}
				}
			}
		}
		
		boolean areInSameString(JoinableText j, JoinableText otherJ) {
			return (texts.get(j).equals(texts.get(otherJ)));
		}
		
	}

	static boolean anyTextsOnSameLine(SVGText text, List<SVGText> others, double textCoordinateTolerance, boolean returnTrueIfNoOthers, double subscriptAndSuperscriptOverlap) {
		int otherJoinableTexts = 0;
		for (SVGText other : others) {
			if (other != text) {
				otherJoinableTexts++;
				if (textsOnSameLine(text, other, textCoordinateTolerance, subscriptAndSuperscriptOverlap)) {
					return true;
				}
			}
		}
		return (otherJoinableTexts == 0 && returnTrueIfNoOthers);
	}
	
	static boolean anyTextsOnSameLine(SVGText text, List<SVGText> others, double textCoordinateTolerance, boolean returnTrueIfNoOthers) {
		int otherJoinableTexts = 0;
		for (SVGText other : others) {
			if (other != text) {
				otherJoinableTexts++;
				if (textsOnSameLine(text, other, textCoordinateTolerance)) {
					return true;
				}
			}
		}
		return (otherJoinableTexts == 0 && returnTrueIfNoOthers);
	}
	
	static boolean anyTextsOnSameLine(JoinableText text, List<? extends Joinable> others, double textCoordinateTolerance, boolean returnTrueIfNoOthers, double subscriptAndSuperscriptOverlap) {
		int otherJoinableTexts = 0;
		for (Joinable other : others) {
			if (other != text && other instanceof JoinableText) {
				otherJoinableTexts++;
				if (textsOnSameLine(text.getSVGElement(), (SVGText) other.getSVGElement(), textCoordinateTolerance, subscriptAndSuperscriptOverlap)) {
					return true;
				}
			}
		}
		return (otherJoinableTexts == 0 && returnTrueIfNoOthers);
	}
	
	static boolean anyTextsOnSameLine(JoinableText text, List<? extends Joinable> others, double textCoordinateTolerance, boolean returnTrueIfNoOthers) {
		int otherJoinableTexts = 0;
		for (Joinable other : others) {
			if (other != text && other instanceof JoinableText) {
				otherJoinableTexts++;
				if (textsOnSameLine(text.getSVGElement(), (SVGText) other.getSVGElement(), textCoordinateTolerance)) {
					return true;
				}
			}
		}
		return (otherJoinableTexts == 0 && returnTrueIfNoOthers);
	}

	static boolean textsOnSameLine(SVGText text, SVGText other, double textCoordinateTolerance, double subscriptAndSuperscriptOverlap) {
		return (!text.getFontSize().equals(other.getFontSize()) ? (text.getY() - text.getHeightOfFirstCharacter() - other.getY() < -subscriptAndSuperscriptOverlap && other.getY() - other.getHeightOfFirstCharacter() - text.getY() < -subscriptAndSuperscriptOverlap) : (Math.abs(other.getY() - text.getY()) < textCoordinateTolerance));
	}

	static boolean textsOnSameLine(SVGText text, SVGText other, double textCoordinateTolerance) {
		return (Math.abs(other.getY() - text.getY()) < textCoordinateTolerance);
	}
	
	/*static class LargestFontFinder {
		double largestFontSize = 0;
		double yOfLargestFontSize = 0;
		List<JoinableText> joinableTextsLookedAt = new ArrayList<JoinableText>();
		List<SVGText> svgTextsLookedAt = new ArrayList<SVGText>();
		
		public LargestFontFinder(List<Joinable> list, boolean ignoreLoneHydrogens) {
			for (Joinable j : list) {
				if (j instanceof JoinableText) {
					SVGText text = (SVGText) j.getSVGElement();
					if (text.getText() != null) {
						if (text.getText().equals("H") && ignoreLoneHydrogens) {
							if (JoinableText.anyTextsOnSameLine((JoinableText) j, list, textCoordinateTolerance, true)) {
								joinableTextsLookedAt.add((JoinableText) j);
								svgTextsLookedAt.add(((JoinableText) j).getSVGElement());
								if (text.getFontSize() > largestFontSize) { 
									largestFontSize = text.getFontSize();
									yOfLargestFontSize = text.getY();
								}
							} 
						} else {
							joinableTextsLookedAt.add((JoinableText) j);
							svgTextsLookedAt.add(((JoinableText) j).getSVGElement());
							if (text.getFontSize() > largestFontSize && !text.getText().replace((char) 160, (char) 32).replace(" ", "").equals("")) { 
								largestFontSize = text.getFontSize();
								yOfLargestFontSize = text.getY();
							}
						}
					}
				}
			}
		}
	}*/
	
	private static abstract class LargestFontFinder<T, U extends T> {
		double largestFontSize = 0;
		double yOfLargestFontSize = 0;
		List<U> textsLookedAt = new ArrayList<U>();
		List<SVGText> svgTextsLookedAt = new ArrayList<SVGText>(); 
		
		public LargestFontFinder(List<T> list, boolean ignoreLoneHydrogens) {
			for (T j : list) {
				try {
					@SuppressWarnings("unchecked")
					U u = (U) j;
					SVGText text = getText(u);
					if (text.getText() != null) {
						if (text.getText().equals("H") && ignoreLoneHydrogens) {
							if (anyTextsOnSameLine(u, list)) {
								textsLookedAt.add(u);
								svgTextsLookedAt.add(getText(u));
								if (text.getFontSize() > largestFontSize) { 
									largestFontSize = text.getFontSize();
									yOfLargestFontSize = text.getY();
								}
							} 
						} else {
							textsLookedAt.add(u);
							svgTextsLookedAt.add(getText(u));
							if (text.getFontSize() > largestFontSize && !text.getText().replace((char) 160, (char) 32).replace(" ", "").equals("")) { 
								largestFontSize = text.getFontSize();
								yOfLargestFontSize = text.getY();
							}
						}
					}
				} catch (ClassCastException e) {
					continue;
				}
			}
		}

		protected abstract boolean anyTextsOnSameLine(U j, List<T> list);

		protected abstract SVGText getText(U j);
		
	}
	
	static class LargestFontFinderForJoinables extends LargestFontFinder<Joinable, JoinableText> {

		public LargestFontFinderForJoinables(List<Joinable> list, boolean ignoreLoneHydrogens) {
			super(list, ignoreLoneHydrogens);
		}

		@Override
		protected boolean anyTextsOnSameLine(JoinableText j, List<Joinable> list) {
			return JoinableText.anyTextsOnSameLine(j, list, textCoordinateTolerance, true);
		}

		@Override
		protected SVGText getText(JoinableText j) {
			return j.getSVGElement();
		}
		
	}
	
	static class LargestFontFinderForSVGTexts extends LargestFontFinder<SVGText, SVGText> {

		public LargestFontFinderForSVGTexts(List<SVGText> list, boolean ignoreLoneHydrogens) {
			super(list, ignoreLoneHydrogens);
		}

		@Override
		protected boolean anyTextsOnSameLine(SVGText j, List<SVGText> list) {
			return JoinableText.anyTextsOnSameLine(j, list, textCoordinateTolerance, true);
		}

		@Override
		protected SVGText getText(SVGText j) {
			return j;
		}
		
	}
	
	public static String getSingleLineTextFromJoinableTexts(List<Joinable> list) {
		LargestFontFinderForJoinables finder = new LargestFontFinderForJoinables(list, true);
		Joinable.sortJoinablesByX(finder.textsLookedAt);
		List<JoinableText> texts = finder.textsLookedAt;
		String text = "";
		SVGText previous = null;
		for (JoinableText joinableText : texts) {
			String character = "";
			SVGText svgText = joinableText.getSVGElement();
			if (finder.largestFontSize - svgText.getFontSize() > allowedFontSizeVariation) {
				if (svgText.getY() < finder.yOfLargestFontSize) {
					character = "^";
				} else {
					character = "_";
				}
			}
			//text += (previous != null && previous.getEnSpaceCount(svgText) >= minimumSpaceWidthInEnSpaces ? " " : "") + character + svgText.getText().replace((char) 160, (char) 32) + character;
			text += (previous != null && !JoinableText.areAdjacentInSameString(previous, svgText, Double.MAX_VALUE, minimumSpaceWidthInEnSpaces, superscriptAndSubscriptOverlap) ? " " : "") + character + svgText.getText().replace((char) 160, (char) 32) + character;
			previous = joinableText.getSVGElement();
		}
		return (text == "" ? null : text);
		//JoinableScriptWord word = getWord();
		//return (word == null ? null : word.getScriptWord().toUnderscoreAndCaretString());
	}

	public static String getMultiLineTextFromJoinableTexts(List<Joinable> list) {
		LargestFontFinderForJoinables finder = new LargestFontFinderForJoinables(list, false);
		
		TextStructurer t = new TextStructurer(finder.svgTextsLookedAt);
		//List<RawWords> lines = t.createRawWordsList();
		List<ScriptLine> lines = t.getScriptedLineList();
		//j.get(0).getScriptWordList().get(0).createSuscriptTextLineList();
		/*List<JoinableScriptWord> words = new ArrayList<JoinableScriptWord>();
		higherPrimitives.setWordsList(words);
		for (ScriptLine line : lines) {
			for (ScriptWord word : line.getScriptWordList()) {
				words.add(new JoinableScriptWord(word));
			}
		}*/
		String result = "";
		boolean firstLine = true;
		for (ScriptLine line : lines) {
			if (!firstLine) {
				result += System.getProperty("line.separator");
			}
			boolean firstWord = true;
			for (ScriptWord word : line.getScriptWordList()) {
				result += (firstWord ? "" : " ") + word.toUnderscoreAndCaretString();
				firstWord = false;
			}
			firstLine = false;
		}
		
		return result;//"one\r\ntwo";
	}
	
}