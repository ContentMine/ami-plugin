package org.xmlcml.ami.visitor.chem;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImageLoader;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.javaocr.scanner.accuracy.AccuracyListenerInterface;
import net.sourceforge.javaocr.scanner.accuracy.OCRIdentification;
import nu.xom.Nodes;

import org.apache.commons.math.complex.Complex;
import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.svg.SVGContainer;
import org.xmlcml.ami.visitor.chem.Joinable.JoinPoint;
import org.xmlcml.ami.visitor.chem.JoinableText.AreInSameStringDetector;
import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Angle.Units;
import org.xmlcml.euclid.Line2;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGConstants;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPolygon;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGTSpan;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.svgbuilder.geom.SimpleBuilder;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.UnionFind;
import com.google.common.util.concurrent.UncheckedTimeoutException;

/**
 * Builds higher-level primitives from SVGPaths, SVGLines, etc. to create SVG objects 
 * such as TramLine and (later) Arrow.
 * 
 * <p>SimpleBuilder's main function is to:
 * <ul>
 * <li>Read a raw SVG object and make lists of SVGPath and SVGText (and possibly higher levels ones
 * if present).</li>
 * <li>Turn SVGPaths into SVGLines , etc..</li>
 * <li>Identify Junctions (line-line, line-text, and probably more).</li>
 * <li>Join lines where they meet into higher level objects (TramLines, SVGRect, crosses, arrows, etc.).</li>
 * <li>Create topologies (e.g. connection of lines and Junctions).</li>
 * </ul>
 * 
 * SimpleBuilder uses the services of the org.xmlcml.graphics.svg.path package and may later use
 * org.xmlcml.graphics.svg.symbol.
 * </p>
 * 
 * <p>Input may either be explicit SVG primitives (e.g. &lt;svg:rect&gt;, &lt;svg:line&gt;) or 
 * implicit ones (&lt;svg:path&gt;) that can be interpreted as the above. The input may be either or
 * both - we can't control it. The implicit get converted to explicit and then merged with the 
 * explicit:
 * <pre>
 *    paths-> implicitLineList + rawLinelist -> explicitLineList 
 * </pre>
 * </p>
 * 
 * <h3>Strategy</h3>
 * <p>createHigherLevelPrimitives() carries out the complete chain from svgRoot to the final
 * primitives. Each step tests to see whether the result of the previous is null.
 * If so it creates a non-null list and fills it if possible. </p>
 * 
 * UPDATE: 2013-10-23 Renamed to "SimpleGeometryManager" as it doesn't deal with Words (which
 * require TextStructurer). It's possible the whole higher-level primitive stuff should be removed to another
 * project.
 * 
 * @author pm286
 */
public class ChemistryBuilder extends SimpleBuilder {

	private static final double HATCH_DETECTION_DEFAULT_MAXIMUM_ANGLE_FOR_PARALLEL = 0.15;
	private static final double HATCH_DETECTION_DEFAULT_MAXIMUM_LINE_LENGTH = 5;
	private static final double HATCH_DETECTION_DEFAULT_MAXIMUM_SPACING = 5.5;
	private static final double HATCH_DETECTION_DEFAULT_MINIMUM_SPACING = 0.5;
	private static final double HATCH_AND_PLUS_CHARGE_DETECTION_DEFAULT_LINE_OVERLAP_EPSILON = 1e-8;
	static final double WORD_DETECTION_DEFAULT_TEXT_Y_COORDINATE_TOLERANCE = 0.25;
	static final double WORD_DETECTION_DEFAULT_MAXIMUM_CHARACTER_X_RANGE_OVERLAP_WHEN_ADJACENT = 0.1;//0.045; TODO the larger value is needed for some subscripts (a subscript can be placed very close to the character to its left)
	private static final double HATCH_DETECTION_DEFAULT_MAXIMUM_TINY_LINE_LENGTH = 0.8;
	private static final double HATCH_DETECTION_DEFAULT_MAXIMUM_ANGLE_FOR_PARALLEL_IF_ONE_LINE_IS_TINY = 0.9;
	private static final double TEXT_CLEANING_DEFAULT_MINIMUM_SPACING_AROUND_NUMBER_TO_MAKE_IT_MOLECULE_LABEL = 17;
	static final double DEFAULT_MINIMUM_SPACE_WIDTH_IN_EN_SPACES = 0.3;
	static final double DEFAULT_MAXIMUM_SPACE_WIDTH_IN_EN_SPACES = 1.2;//0.6;
	private static final double DEFAULT_PLUS_CHARGE_ANGLE_TOLERANCE = 0.1;
	static final double DEFAULT_FLAT_LINE_EPSILON = 0.5;
	private static final double HATCH_DETECTION_DEFAULT_MAXIMUM_LENGTH_CHANGE_FOR_QUADRILATERAL_HATCHES = 0.01;
	static final double WORD_DETECTION_DEFAULT_MINIMUM_Y_RANGE_OVERLAP_FOR_SUPERSCRIPTS_AND_SUBSCRIPTS = 3;
	static final double WIGGLY_BOND_DETECTION_DEFAULT_INCORRECT_DIRECTION_TOLERANCE = 0.15;
	private static final double ATOM_LABEL_DETECTION_DEFAULT_MAXIMUM_SEQUENCE_GAP_RELATIVE_TO_LARGEST = 0.4;
	static final double WIGGLY_BOND_DETECTION_DEFAULT_RELATIVE_GAP_TOLERANCE = 0.2;
	static final double DEFAULT_WIGGLY_BOND_JOINT_POINT_RADIUS = 1.5;
	private static final double TEXT_FROM_IMAGE_DEFAULT_RADIUS_EXPANSION = 1.5;
	private static final int OCR_DEFAULT_BLACK_THRESHOLD = 128;
	private static final int OCR_MINIMUM_IMAGE_WIDTH = 14;
	private static final double OCR_DEFAULT_MAXIMUM_IMAGE_ELEMENT_WIDTH = 12;
	static final double DEFAULT_FONT_SIZE_TOLERANCE = 0.25;
	private static final double DEFAULT_MAXIMUM_DISTANCE_BETWEEN_EXTENDED_LINE_JOINABLE_AND_TEXT_CENTRE = 3;
	private static final double DEFAULT_TEXT_X_OVERLAP_EPSILON = 0.001;
	private static final int DEFAULT_MAXIMUM_OCR_ERROR = 15;

	private final static Logger LOG = Logger.getLogger(ChemistryBuilder.class);

	protected HigherPrimitives higherPrimitives;

	protected SVGContainer input;

	private double hatchLineMaximumLength = HATCH_DETECTION_DEFAULT_MAXIMUM_LINE_LENGTH;
	private double hatchLinesMaximumSpacing = HATCH_DETECTION_DEFAULT_MAXIMUM_SPACING;
	private double hatchLinesMinimumSpacing = HATCH_DETECTION_DEFAULT_MINIMUM_SPACING;
	private double maximumAngleForParallel = HATCH_DETECTION_DEFAULT_MAXIMUM_ANGLE_FOR_PARALLEL;
	private double lineOverlapEpsilon = HATCH_AND_PLUS_CHARGE_DETECTION_DEFAULT_LINE_OVERLAP_EPSILON;
	private double textCoordinateTolerance = WORD_DETECTION_DEFAULT_TEXT_Y_COORDINATE_TOLERANCE;
	private double maximumCharacterXRangeOverlapWhenAdjacent = WORD_DETECTION_DEFAULT_MAXIMUM_CHARACTER_X_RANGE_OVERLAP_WHEN_ADJACENT;
	private double tinyHatchLineMaximumLength = HATCH_DETECTION_DEFAULT_MAXIMUM_TINY_LINE_LENGTH;
	private double maximumAngleForParallelIfOneLineIsTiny = HATCH_DETECTION_DEFAULT_MAXIMUM_ANGLE_FOR_PARALLEL_IF_ONE_LINE_IS_TINY;
	private double minimumLabelSpacing = TEXT_CLEANING_DEFAULT_MINIMUM_SPACING_AROUND_NUMBER_TO_MAKE_IT_MOLECULE_LABEL;
	private double maximumSpaceWidthInEnSpaces = DEFAULT_MAXIMUM_SPACE_WIDTH_IN_EN_SPACES;
	private double plusChargeAngleTolerance = DEFAULT_PLUS_CHARGE_ANGLE_TOLERANCE;
	private double flatLineEpsilon = DEFAULT_FLAT_LINE_EPSILON;
	private double lengthTolerance = HATCH_DETECTION_DEFAULT_MAXIMUM_LENGTH_CHANGE_FOR_QUADRILATERAL_HATCHES;
	private double superscriptAndSubscriptOverlap = WORD_DETECTION_DEFAULT_MINIMUM_Y_RANGE_OVERLAP_FOR_SUPERSCRIPTS_AND_SUBSCRIPTS;
	private double maximumLabelSequenceGap = ATOM_LABEL_DETECTION_DEFAULT_MAXIMUM_SEQUENCE_GAP_RELATIVE_TO_LARGEST;
	private Double textFromImageRadiusExpansion = TEXT_FROM_IMAGE_DEFAULT_RADIUS_EXPANSION;
	private int blackThreshold = OCR_DEFAULT_BLACK_THRESHOLD;
	private int mimimumImageWidthForOCR = OCR_MINIMUM_IMAGE_WIDTH;
	private double maximumImageElementWidthForOCR = OCR_DEFAULT_MAXIMUM_IMAGE_ELEMENT_WIDTH;
	private double allowedFontSizeVariation = DEFAULT_FONT_SIZE_TOLERANCE;
	private double maxDistanceBetweenLineAndPoint = DEFAULT_MAXIMUM_DISTANCE_BETWEEN_EXTENDED_LINE_JOINABLE_AND_TEXT_CENTRE;
	private double textOverlapEpsilon = DEFAULT_TEXT_X_OVERLAP_EPSILON;
	private static final int maximumOCRError = DEFAULT_MAXIMUM_OCR_ERROR;

	private int numChannels = 4;

	//HashBiMap<HatchedBond, SVGLine> mutuallyExclusivePairs;
	//Map<SVGLine, SingleBond> whichLineIsWhichSingleBond;
	private List<JoinableText> atomLabelTexts;
	private Map<Real2Range, Integer> atomLabelPositionsAndNumbers;
	
	List<WedgeBond> wedgeBonds = new ArrayList<WedgeBond>();

	private int defaultFontSize = 20;
	private FontRenderContext defaultFRC = new FontRenderContext(new AffineTransform(), false, true);

	static class MutuallyExclusiveShortLineTriple {
	
		HatchedBond hatchedBond;
		Charge minus;

		SVGLine line;
		SingleBond singleBond;
		
		public MutuallyExclusiveShortLineTriple(HatchedBond hatchedBond, Charge minus, SVGLine line) {
			this.hatchedBond = hatchedBond;
			this.minus = minus;
			this.line = line;
		}
	
	}

	static class MutuallyExclusiveShortLinePairTriple {
		
		HatchedBond hatchedBond;

		SVGLine line1;
		SVGLine line2;
		DoubleBond doubleBond;
		SingleBond singleBond1;
		SingleBond singleBond2;
		
		public MutuallyExclusiveShortLinePairTriple(HatchedBond hatchedBond, SVGLine line1, SVGLine line2) {
			this.hatchedBond = hatchedBond;
			this.line1 = line1;
			this.line2 = line2;
		}
		
	}
	
	static class MutuallyExclusiveLinePairPair {

		SVGLine line1;
		SVGLine line2;
		DoubleBond doubleBond;
		SingleBond singleBond1;
		SingleBond singleBond2;
		
		public MutuallyExclusiveLinePairPair(DoubleBond doubleBond) {
			this.line1 = doubleBond.getLine(0);
			this.line2 = doubleBond.getLine(1);
			this.doubleBond = doubleBond;
		}
		
	}

	List<MutuallyExclusiveShortLineTriple> mutuallyExclusiveShortLineTriples;
	List<MutuallyExclusiveShortLinePairTriple> mutuallyExclusiveShortLinePairTriples;
	List<MutuallyExclusiveLinePairPair> mutuallyExclusiveLinePairPairs;

	public ChemistryBuilder(SVGContainer svgRoot, long timeout) {
		super((SVGElement) svgRoot.getElement(), timeout);
		input = svgRoot;
	}

	public ChemistryBuilder(SVGContainer svgRoot) {
		super((SVGElement) svgRoot.getElement());
		input = svgRoot;
	}
	
	public ChemistryBuilder(SVGElement svgRoot, long timeout) {
		super(svgRoot, timeout);
	}

	public ChemistryBuilder(SVGElement svgRoot) {
		super(svgRoot);
	}
	
	public SVGContainer getInputContainer() {
		return input;
	}
	
	/**
	 * Complete processing chain for low-level SVG into high-level SVG and non-SVG primitives such as double bonds.
	 * <p>
	 * Creates junctions.
	 * <p>
	 * Runs createDerivedPrimitives().
	 * 
	 * @throws TimeoutException 
	 */
	public void createHigherPrimitives() {
		if (higherPrimitives == null) {
			startTiming();
			createDerivedPrimitives();
			replaceTextImagesWithText();
			splitMultiCharacterTexts();
			higherPrimitives = new HigherPrimitives();
			higherPrimitives.addSingleLines(derivedPrimitives.getLineList());
			handleShortLines();
			createDoubleBondList();
			//createWords();
			createJunctions();
		}
	}

	private void splitMultiCharacterTexts() {
		Iterator<SVGText> it = derivedPrimitives.getTextList().iterator();
		List<SVGText> newTexts = new ArrayList<SVGText>();
		while (it.hasNext()) {
			SVGText text = it.next();
			String string = text.getText();
			List<SVGTSpan> spanList = new ArrayList<SVGTSpan>();
			if (string == null) {
				Nodes spans = text.query("svg:tspan", SVGSVG.SVG_XPATH);
				for (int i = 0; i < spans.size(); i++) {
					spanList.add((SVGTSpan) spans.get(i));
				}
			} else {
				spanList.add(new SVGTSpan(text));
			}
			it.remove();
			double previousX = text.getX();
			double previousY = text.getY();
			for (SVGTSpan span : spanList) {
				if (span.getX() != 0.0) {
					previousX = span.getX();
				}
				if (span.getY() != 0.0) {
					previousY = span.getY();
				}
				GlyphVector glyphVector = span.getGlyphVector();
				String spanText = span.getText();
				for (int i = 0; i < spanText.length(); i++) {
					String substring = spanText.substring(i, i + 1);
					SVGText newText = new SVGText(new Real2(0, 0), substring);
					newTexts.add(newText);
					newText.copyAttributesFrom(span);
					double dX = 0;
					if (span.getAttributeValue("dx") != null) {
						dX = Double.parseDouble(span.getAttributeValue("dx"));
						newText.removeAttribute(newText.getAttribute("dx"));
					}
					double dY = 0;
					if (span.getAttributeValue("dy") != null) {
						dY = Double.parseDouble(span.getAttributeValue("dy"));
						newText.removeAttribute(newText.getAttribute("dy"));
					}
					newText.setX(previousX + dX + glyphVector.getGlyphPosition(i).getX());
					newText.setY(previousY + dY + glyphVector.getGlyphPosition(i).getY());
				}
				previousX += glyphVector.getGlyphPosition(glyphVector.getNumGlyphs()).getX();
			}
		}
		derivedPrimitives.getTextList().addAll(newTexts);
	}

	public BufferedImage flipHorizontally(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(w, h, img.getColorModel().getTransparency());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(img, 0, 0, w, h, 0, h, w, 0, null);
		g.dispose();
		return dimg;
	}

	private File getImageFileFromSVGImage(SVGImage image) {
		String filename = image.getAttributeValue("href", SVGConstants.XLINK_NS);
		File testFile = new File(filename);
		return (testFile.isAbsolute() ? testFile : new File(input.getFile().getParentFile().getAbsolutePath() + "/" + filename));
	}
	
	private void replaceTextImagesWithText() {
		if (rawPrimitives.getImageList().size() == 0) {
			return;
		}
		OCRScanner scanner = makeOCRScanner();
		Set<Complex> done = new HashSet<Complex>();
		List<SVGText> ambiguousTexts = new ArrayList<SVGText>();
		List<SVGImage> imagesForAmbiguousTexts = new ArrayList<SVGImage>();
		List<Double> scaleFactorsForAmbiguousTexts = new ArrayList<Double>();
		HashMultiset<Double> nonAmbiguousFontSizes = HashMultiset.create();
		
		for (SVGImage image : rawPrimitives.getImageList()) {
			try {
				checkTime("Took too long to convert images to text");
				image.applyTransformAttributeAndRemove();
				if (image.getWidth() > maximumImageElementWidthForOCR) {
					continue;
				}
				File file = getImageFileFromSVGImage(image);
				BufferedImage bufferedImage = flipHorizontally(ImageIO.read(file));
				String s = "";
				/*Tesseract tess = Tesseract.getInstance();
				try {
					s = tess.doOCR(im);
				} catch (TesseractException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				
				if (!done.add(new Complex(image.getX(), image.getY())) || bufferedImage.getWidth() < mimimumImageWidthForOCR) {
					derivedPrimitives.getImageList().remove(image);
					continue;
				}

				final double[] accuracy = new double[1];
				scanner.acceptAccuracyListener(new AccuracyListenerInterface() {
					@Override
					public void processCharOrSpace(OCRIdentification identAccuracy) {
						if (identAccuracy.getCharIdx(0) != '\n') {
							accuracy[0] = identAccuracy.getIdentErrorIdx(0);
						}
					}
				});
				s = scanner.scanSingleLine(bufferedImage, 0, 0, 0, 0, null);
				
				if (s.length() >= 1 && accuracy[0] < maximumOCRError) {
					s = s.substring(0, 1);
					int whiteRowsAtTop = countWhiteRowsAtTop(bufferedImage);
					int whiteRowsAtBottom = countWhiteRowsAtBottom(bufferedImage);

					Font defaultFont = new Font(scanner.getLastFoundFontName(), 0, defaultFontSize);
					GlyphVector glyphVector = defaultFont.createGlyphVector(defaultFRC, s);
					Rectangle2D bounds = glyphVector.getVisualBounds();
					double scaleFactorByHeight = calculateHeightBasedScaleFactor(image, bufferedImage, whiteRowsAtTop, whiteRowsAtBottom, bounds);
					double fontSize = scaleFactorByHeight * defaultFontSize;
					double xCoord = -bounds.getMinX() * scaleFactorByHeight + image.getX();
					double yCoord = -bounds.getMinY() * scaleFactorByHeight + image.getY() + image.getHeight();
					
					SVGText text = new SVGText(new Real2(0, 0), s);
					text.setFontSize(fontSize);
					text.setFontFamily(scanner.getLastFoundFontName().replace(" Italic",""));
					if (scanner.getLastFoundFontName().contains("Italic")) {
						text.setFontStyle("italic");
					}
					text.setSVGXFontWidth((1000 * scaleFactorByHeight * glyphVector.getLogicalBounds().getWidth()) / fontSize);//defaultFont.getLineMetrics(s, defaultFRC).getHeight()));
					text.copyAttributesFrom(image);
					text.setXY(new Real2(xCoord, yCoord));
					try {
						text.removeAttribute(text.getAttribute("style"));
					} catch (NullPointerException e) {

					}
							
					image.getParent().replaceChild(image, text);
					derivedPrimitives.getTextList().add(text);
					if (s.matches("[cosuvwxzCOSUVWXZ]")) {
						ambiguousTexts.add(text);
						imagesForAmbiguousTexts.add(image);
						scaleFactorsForAmbiguousTexts.add(scaleFactorByHeight);
					} else {
						nonAmbiguousFontSizes.add(fontSize);
					}
					derivedPrimitives.getImageList().remove(image);
				}
			} catch (IOException e) {
				System.err.println("Error handling image within SVG file - it's probably embedded in base 64, but it should be linked to and stored separately");
				e.printStackTrace();
			} catch (Exception e) {
				//TODO handle other images
			}

		}
		
		handleAmbiguousTexts(ambiguousTexts, imagesForAmbiguousTexts, scaleFactorsForAmbiguousTexts, nonAmbiguousFontSizes);
	}

	private OCRScanner makeOCRScanner() {
		TrainingImageLoader l = new TrainingImageLoader();
		HashMap<Character, ArrayList<TrainingImage>> dest = new HashMap<Character, ArrayList<TrainingImage>>();
		try {
			//l.loadASCIIFont("Helvetica", dest);
			//l.loadASCIIFont("Helvetica Italic", dest);
			l.loadASCIIFont("Times New Roman", dest);
			l.loadASCIIFont("Times New Roman Italic", dest);
			l.loadASCIIFont("Sans", dest);
			l.loadASCIIFont("Sans Italic", dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OCRScanner scanner = new OCRScanner();
		scanner.addTrainingImages(dest);
		return scanner;
	}

	private void handleAmbiguousTexts(List<SVGText> ambiguousTexts, List<SVGImage> imagesForAmbiguousTexts, List<Double> scaleFactorsForAmbiguousTexts, HashMultiset<Double> nonAmbiguousFontSizes) {
		//defaultFont.getLineMetrics("", defaultFRC).getHeight()
		int i = 0;
		for (SVGText svgText : ambiguousTexts) {
			String text = svgText.getText();
			double currentFontSize = svgText.getFontSize();
			Font defaultFont = new Font(svgText.getFontFamily(), 0, defaultFontSize);
			double potentialFontSize = getOtherPossibleFontSize(defaultFont, defaultFRC, text, currentFontSize);
			int currentFontSizeMatches = 0;
			int potentialFontSizeMatches = 0;
			for (com.google.common.collect.Multiset.Entry<Double> size : nonAmbiguousFontSizes.entrySet()) {
				if (Math.abs(size.getElement() - currentFontSize) < 0.1) {
					currentFontSizeMatches++;
				}
				if (Math.abs(size.getElement() - potentialFontSize) < 0.1) {
					potentialFontSizeMatches++;
				}
			}
			if (potentialFontSizeMatches > currentFontSizeMatches) {
				svgText.setFontSize(potentialFontSize);
				String newText = (potentialFontSize > currentFontSize ? text.toLowerCase() : text.toUpperCase());
				svgText.setText(newText);
				GlyphVector glyphVector = defaultFont.createGlyphVector(defaultFRC, newText);
				Rectangle2D bounds = glyphVector.getVisualBounds();
				double scaleFactorByHeight = scaleFactorsForAmbiguousTexts.get(i) * potentialFontSize / currentFontSize;
				double xCoord = -bounds.getMinX() * scaleFactorByHeight + imagesForAmbiguousTexts.get(i).getX();
				double yCoord = -bounds.getMinY() * scaleFactorByHeight + imagesForAmbiguousTexts.get(i).getY() + imagesForAmbiguousTexts.get(i).getHeight();
				svgText.setXY(new Real2(xCoord, yCoord));
				svgText.setSVGXFontWidth((1000 * scaleFactorByHeight * glyphVector.getLogicalBounds().getWidth()) / potentialFontSize);
			}
			i++;
		}
		//SVGSVG.wrapAndWriteAsSVG(el, new File(svgContainer.getFile().getAbsolutePath() + "new.svg"));
	}

	private double calculateHeightBasedScaleFactor(SVGImage image, BufferedImage bufferedImage, int whiteRowsAtTop, int whiteRowsAtBottom, Rectangle2D bounds) {
		double scaleFactorByHeight = -((image.getHeight() / bufferedImage.getHeight()) * (bufferedImage.getHeight() - whiteRowsAtBottom - whiteRowsAtTop)) / bounds.getHeight();
		return scaleFactorByHeight;
	}

	private int countWhiteRowsAtBottom(BufferedImage bufferedImage) {
		int[] pixels = bufferedImage.getRaster().getPixels(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), (int[]) null);
		int whiteRowsAtBottom = 0;
		outer: for (int i = pixels.length - bufferedImage.getWidth() * numChannels ; i >= 0; i -= bufferedImage.getWidth() * numChannels) {
			for (int j = i; j < i + bufferedImage.getWidth() * numChannels; j += numChannels) {
				if (pixels[j] < blackThreshold  && pixels[j + numChannels - 1] != 0) {
					break outer;
				}
			}
			whiteRowsAtBottom++;
		}
		return whiteRowsAtBottom;
	}

	private int countWhiteRowsAtTop(BufferedImage bufferedImage) {
		int[] pixels = bufferedImage.getRaster().getPixels(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), (int[]) null);
		int whiteRowsAtTop = 0;
		outer: for (int i = 0; i < pixels.length; i += bufferedImage.getWidth() * numChannels) {
			for (int j = i; j < i + bufferedImage.getWidth() * numChannels; j += numChannels) {
				if (pixels[j] < blackThreshold  && pixels[j + numChannels - 1] != 0) {
					break outer;
				}
			}
			whiteRowsAtTop++;
		}
		return whiteRowsAtTop;
	}

	private double getOtherPossibleFontSize(Font defaultFont, FontRenderContext defaultFRC, String text, double currentFontSize) {
		GlyphVector glyphVectorLower = defaultFont.createGlyphVector(defaultFRC, text.toLowerCase());
		GlyphVector glyphVectorUpper = defaultFont.createGlyphVector(defaultFRC, text.toUpperCase());
		double heightLower = glyphVectorLower.getVisualBounds().getHeight();
		double heightUpper = glyphVectorUpper.getVisualBounds().getHeight();
		double potentialFontSize = (text.toLowerCase().equals(text) ? (heightLower / heightUpper) : (heightUpper / heightLower)) * currentFontSize;
		return potentialFontSize;
	}
	
	/*private void convertImagesOfTextToText() {
		for (SVGImage image : rawPrimitives.getImageList()) {
			String path = image.getAttributeValue("href", "xlink");
		}
	}*/

	/*private void createWords() {
		TextStructurer t = new TextStructurer(derivedPrimitives.getTextList());
		//List<RawWords> lines = t.createRawWordsList();
		List<ScriptLine> lines = t.getScriptedLineList();
		//j.get(0).getScriptWordList().get(0).createSuscriptTextLineList();
		List<JoinableScriptWord> words = new ArrayList<JoinableScriptWord>();
		higherPrimitives.setWordsList(words);
		for (ScriptLine line : lines) {
			for (ScriptWord word : line.getScriptWordList()) {
				words.add(new JoinableScriptWord(word));
			}
		}
	}*/

	private void handleShortLines() {
		List<HatchedBond> hatchList = new ArrayList<HatchedBond>();
		higherPrimitives.setHatchedBondList(hatchList);
		List<SVGLine> smallLines = new ArrayList<SVGLine>();
		for (SVGLine l : derivedPrimitives.getLineList()) {
			if (l.getXY(0).getDistance(l.getXY(1)) < hatchLineMaximumLength && l.getXY(0).getDistance(l.getXY(1)) > 0) {//TODO l.getLength() < hatchLineMaximumLength) {
				smallLines.add(l);
			}
		}
		higherPrimitives.setChargeList(new ArrayList<Charge>());
		if (smallLines.size() == 0) {
			mutuallyExclusiveShortLineTriples = new ArrayList<MutuallyExclusiveShortLineTriple>();
			mutuallyExclusiveShortLinePairTriples = new ArrayList<MutuallyExclusiveShortLinePairTriple>();
			return;
		}
		UnionFind<SVGLine> hatchedBonds = UnionFind.create(smallLines);
		for (int i = 0; i < smallLines.size(); i++) {
			SVGLine firstLine = smallLines.get(i);
			for (int j = i + 1; j < smallLines.size(); j++) {
				checkTime("Took too long to handle short lines");
				SVGLine secondLine = smallLines.get(j);
				Double dist = firstLine.calculateUnsignedDistanceBetweenLines(secondLine, new Angle((firstLine.getLength() < tinyHatchLineMaximumLength || secondLine.getLength() < tinyHatchLineMaximumLength ? maximumAngleForParallelIfOneLineIsTiny : maximumAngleForParallel), Units.RADIANS));
				if (dist != null && dist < hatchLinesMaximumSpacing && dist > hatchLinesMinimumSpacing && (firstLine.overlapsWithLine(secondLine, lineOverlapEpsilon) || secondLine.overlapsWithLine(firstLine, lineOverlapEpsilon))) {
					try {
						hatchedBonds.union(firstLine, secondLine);
					} catch (IllegalArgumentException e) {
						
					}
				}
				if ((firstLine.isHorizontal(flatLineEpsilon) || secondLine.isHorizontal(flatLineEpsilon)) && firstLine.overlapsWithLine(secondLine, lineOverlapEpsilon) && secondLine.overlapsWithLine(firstLine, lineOverlapEpsilon) && firstLine.getEuclidLine().isPerpendicularTo(secondLine.getEuclidLine(), new Angle(plusChargeAngleTolerance, Units.DEGREES))) {
					hatchedBonds.remove(firstLine);
					hatchedBonds.remove(secondLine);
					higherPrimitives.getLineList().remove(firstLine);
					higherPrimitives.getLineList().remove(secondLine);
					higherPrimitives.getLineChargeList().add(new Charge(firstLine, secondLine));
				}
			}
		}
		handleShortLines(hatchedBonds);
	}

	private void handleShortLines(UnionFind<SVGLine> disjointSets) {
		mutuallyExclusiveShortLineTriples = new ArrayList<MutuallyExclusiveShortLineTriple>();
		mutuallyExclusiveShortLinePairTriples = new ArrayList<MutuallyExclusiveShortLinePairTriple>();
		List<HatchedBond> hatchList = higherPrimitives.getHatchedBondList();
		set: for (Set<SVGLine> set : disjointSets.snapshot()) {
			ArrayList<SVGLine> lines1 = new ArrayList<SVGLine>(set);
			ArrayList<SVGLine> lines2 = new ArrayList<SVGLine>(set);
			Collections.sort(lines1, new Comparator<SVGLine>(){
				public int compare(SVGLine i, SVGLine j) {
					return (Real.isEqual(i.getXY(0).getX(), j.getXY(0).getX(), 0.1) ? Double.compare(i.getXY(0).getY(), j.getXY(0).getY()) : Double.compare(i.getXY(0).getX(), j.getXY(0).getX()));
				}});
			Collections.sort(lines2, new Comparator<SVGLine>(){
				public int compare(SVGLine i, SVGLine j) {
					return (Real.isEqual(i.getXY(0).getY(), j.getXY(0).getY(), 0.1) ? Double.compare(i.getXY(0).getX(), j.getXY(0).getX()) : Double.compare(i.getXY(0).getY(), j.getXY(0).getY()));
				}});
			ArrayList<SVGLine> lines3 = (ArrayList<SVGLine>) lines1.clone();
			Collections.reverse(lines3);
			ArrayList<SVGLine> lines4 = (ArrayList<SVGLine>) lines1.clone();
			ArrayList<SVGLine> lines5 = (ArrayList<SVGLine>) lines1.clone();
			Collections.sort(lines4, new Comparator<SVGLine>(){
				public int compare(SVGLine i, SVGLine j) {
					return (Real.isEqual(i.getXY(1).getX(), j.getXY(1).getX(), 0.1) ? Double.compare(i.getXY(1).getY(), j.getXY(1).getY()) : Double.compare(i.getXY(1).getX(), j.getXY(1).getX()));
				}});
			Collections.sort(lines5, new Comparator<SVGLine>(){
				public int compare(SVGLine i, SVGLine j) {
					return (Real.isEqual(i.getXY(1).getY(), j.getXY(1).getY(), 0.1) ? Double.compare(i.getXY(1).getX(), j.getXY(1).getX()) : Double.compare(i.getXY(1).getY(), j.getXY(1).getY()));
				}});
			ArrayList<SVGLine> lines6 = (ArrayList<SVGLine>) lines4.clone();
			Collections.reverse(lines6);
			ArrayList<SVGLine> lines7 = (ArrayList<SVGLine>) lines1.clone();
			ArrayList<SVGLine> lines8 = (ArrayList<SVGLine>) lines1.clone();
			Collections.sort(lines7, new Comparator<SVGLine>(){
				public int compare(SVGLine i, SVGLine j) {
					return (Real.isEqual(i.getMidPoint().getX(), j.getMidPoint().getX(), 0.1) ? Double.compare(i.getMidPoint().getY(), j.getMidPoint().getY()) : Double.compare(i.getMidPoint().getX(), j.getMidPoint().getX()));
				}});
			Collections.sort(lines8, new Comparator<SVGLine>(){
				public int compare(SVGLine i, SVGLine j) {
					return (Real.isEqual(i.getMidPoint().getY(), j.getMidPoint().getY(), 0.1) ? Double.compare(i.getMidPoint().getX(), j.getMidPoint().getX()) : Double.compare(i.getMidPoint().getY(), j.getMidPoint().getY()));
				}});
			ArrayList<SVGLine> lines9 = (ArrayList<SVGLine>) lines7.clone();
			Collections.reverse(lines9);
			boolean firstEndPointsAndSecondEndPointsOrdered = ((lines1.equals(lines2) || lines3.equals(lines2)) && (lines4.equals(lines5) || lines6.equals(lines5)));
			boolean firstEndPointsAndMidPointsOrdered = ((lines1.equals(lines2) || lines3.equals(lines2)) && (lines7.equals(lines8) || lines9.equals(lines8)));
			boolean secondEndPointsAndMidPointsOrdered = ((lines4.equals(lines5) || lines6.equals(lines5)) && (lines7.equals(lines8) || lines9.equals(lines8)));
			if (firstEndPointsAndSecondEndPointsOrdered || firstEndPointsAndMidPointsOrdered || secondEndPointsAndMidPointsOrdered) {
				ArrayList<SVGLine> lines;
				if (firstEndPointsAndSecondEndPointsOrdered || firstEndPointsAndMidPointsOrdered) {
					lines = lines1;
				} else {
					lines = lines4;
				}
				try {
					double change = lines.get(1).getLength() - lines.get(0).getLength();
					double direction = Math.signum(change);
					double firstLength = lines.get(0).getLength();
					for (int i = 2; i < lines.size() && change > lengthTolerance; i++) {
						if (Math.signum(lines.get(i).getLength() - firstLength) != direction) {
							continue set;
						}
					}
				} catch (IndexOutOfBoundsException e) {
					
				}
				HatchedBond hatchedBond = new HatchedBond(lines);
				hatchList.add(hatchedBond);
				if (lines.size() > 2) {
					higherPrimitives.getLineList().removeAll(lines);
				} else if (lines.size() == 2) {
					mutuallyExclusiveShortLinePairTriples.add(new MutuallyExclusiveShortLinePairTriple(hatchedBond, lines.get(0), lines.get(1)));
				} else {
					Charge charge = null;
					if (lines.get(0).isHorizontal(flatLineEpsilon) && !lines.get(0).isVertical(flatLineEpsilon)) {
						charge = new Charge(lines);
						higherPrimitives.getLineChargeList().add(charge);
					}
					mutuallyExclusiveShortLineTriples.add(new MutuallyExclusiveShortLineTriple(hatchedBond, charge, lines.get(0)));
				}
			}
		}
	}
	
	private void createJunctions() {
		createJoinableList();
		List<Joinable> joinables = higherPrimitives.getJoinableList();

		List<JoinPoint> joinPoints = extractAtomLabelsAndGetRemainingJoinPoints(joinables);
		
		UnionFind<JoinPoint> joinPointsGroupedIntoJunctions = UnionFind.create(joinPoints);
		//int deleted = 0;
		attemptToJoinListOfJoinables(joinables, joinPointsGroupedIntoJunctions);
		
		try {
			handleAmbiguities(joinPointsGroupedIntoJunctions);
		} catch (IllegalArgumentException e) {
			joinPoints.clear();
			LOG.debug("Processing failed as the diagram was too complex");
		}
		
		List<Junction> junctions = new ArrayList<Junction>();
			
		if (joinPoints.size() != 0) {
			for (Set<JoinPoint> junctionJoinPoints : joinPointsGroupedIntoJunctions.snapshot()) {
				//if (junctionJoinPoints.size() != 1) {
					/*Set<Joinable> junctionJoinables = new HashSet<Joinable>();
					Set<JoinPoint> newJunctionJoinPoints = new HashSet <JoinPoint>();
					for (JoinPoint point : junctionJoinPoints) {
						if (junctionJoinables.add(point.getJoinable())) {
							newJunctionJoinPoints.add(point);
						} else {
							newJunctionJoinPoints.removeAll(point.getJoinable().getJoinPoints());
							removeJoinable(point.getJoinable());
							//junctionJoinables.remove(point.getJoinable());
						}
					}
					for (Joinable j1 : junctionJoinables) {
						int numberParallel = 0;
						for (Joinable j2 : junctionJoinables) {
							if (Joinable.areParallel(j1, j2)) {
								numberParallel++;
								if (numberParallel == 3) {
									for (JoinPoint p : newJunctionJoinPoints) {
										junctions.add(new Junction(p));
									}
									continue junction;
								}
							}
						}
					}
					junctions.add(new Junction(newJunctionJoinPoints));*/
				//}
				junctions.add(new Junction(junctionJoinPoints));
			}
		}
		higherPrimitives.setJunctionList(junctions);
				
				/*JoinPoint commonPoint = joinablei.getIntersectionPoint(joinablej);
				if (commonPoint != null) {
					Junction junction = new Junction(joinablei, joinablej, commonPoint);
					rawJunctionList.add(junction);
					String junctAttVal = "junct"+"."+rawJunctionList.size();
					junction.addAttribute(new Attribute(SVGElement.ID, junctAttVal));
					if (junction.getCoordinates() == null && commonPoint.getPoint() != null) {
						junction.setCoordinates(commonPoint.getPoint());
					}
					LOG.debug("junct: "+junction.getId()+" between " + joinablei.getClass() + " and " + joinablej.getClass() + " with coords "+junction.getCoordinates()+" "+commonPoint.getPoint());
				}
			}
		}*/
		
		/*createRawJunctionList();
		List<Junction> junctionList = new ArrayList<Junction>(higherPrimitives.getRawJunctionList());
		for (int i = junctionList.size() - 1; i > 0; i--) {
			Junction labile = junctionList.get(i);
			for (int j = 0; j < i; j++) {
				Junction fixed = junctionList.get(j);
				if (fixed.containsCommonPoints(labile)) {
					labile.transferDetailsTo(fixed);
					junctionList.remove(i);
					break;
				}
			}
		}
		higherPrimitives.setMergedJunctionList(junctionList);*/
	}

	private void attemptToJoinListOfJoinables(List<Joinable> joinables, UnionFind<JoinPoint> joinPointsGroupedIntoJunctions) {
		for (int i = 0; i < joinables.size() - 1; i++) {
			Joinable joinableI = joinables.get(i);
			for (int j = i + 1; j < joinables.size(); j++) {
				checkTime("Took too long to determine what is joined to what");
				Joinable joinableJ = joinables.get(j);
				
				//System.out.println(joinableI + "\n" + joinableJ);
				
				attemptToJoinJoinables(joinPointsGroupedIntoJunctions, joinableI, joinableJ);
			}
		}
	}

	private void attemptToJoinJoinables(UnionFind<JoinPoint> joinPointsGroupedIntoJunctions, Joinable joinableI, Joinable joinableJ) {
		Set<JoinPoint> overlapSet = joinableI.overlapWith(joinableJ);
		if (overlapSet != null) {
			List<JoinPoint> overlapList = new ArrayList<JoinPoint>(overlapSet);
			if (!joinPointsGroupedIntoJunctions.contains(overlapList.get(0)) || !joinPointsGroupedIntoJunctions.contains(overlapList.get(1)) || (overlapList.size() > 2 && !joinPointsGroupedIntoJunctions.contains(overlapList.get(2))) || (overlapList.size() > 3 && !joinPointsGroupedIntoJunctions.contains(overlapList.get(3)))) {
				return;
			}
			if (mutuallyExclusive(joinableI, joinableJ)) {
				return;
			}
			if (joinableI instanceof JoinableText && joinableJ instanceof JoinableText) {
				if (JoinableText.doTextsJoin((JoinableText) joinableI, (JoinableText) joinableJ, textCoordinateTolerance, maximumCharacterXRangeOverlapWhenAdjacent, allowedFontSizeVariation)) {
				//joinPointsGroupedIntoJunctions.union(overlap.get(0), overlap.get(1));
					joinPointsGroupedIntoJunctions.unionAll(overlapList);
				}
			} else if ((joinableI instanceof JoinableText && joinableJ.getJoinPoints().size() == 2) || (joinableJ instanceof JoinableText && joinableI.getJoinPoints().size() == 2)) {
				Joinable lineJoinable = (joinableI instanceof JoinableText ? joinableJ : joinableI);
				JoinPoint lineJoinEnd = (overlapList.get(0).getJoinable() instanceof JoinableText ? overlapList.get(1) : overlapList.get(0));
				JoinPoint lineOtherEnd = (lineJoinable.getJoinPoints().get(0) == lineJoinEnd ? lineJoinable.getJoinPoints().get(1) : lineJoinable.getJoinPoints().get(0));
				Line2 line = new Line2(lineOtherEnd.getPoint(), lineJoinEnd.getPoint());
				JoinPoint text = (overlapList.get(0).getJoinable() instanceof JoinableText ? overlapList.get(0) : overlapList.get(1));
				Line2 testLine = new Line2(lineJoinEnd.getPoint(), text.getPoint());
				if (isNumber((SVGText) text.getJoinable().getSVGElement()) || line.isParallelTo(testLine, new Angle(40, Units.DEGREES))) {
					joinPointsGroupedIntoJunctions.unionAll(overlapList);
				}
			} else {
				joinPointsGroupedIntoJunctions.unionAll(overlapList);
			}
			
			/*if (joinableI instanceof JoinableText && joinableJ instanceof JoinableText) {
				if (doTextsJoin(joinableI, joinableJ)) { 
					joinPointsGroupedIntoJunctions.union(overlap.get(0), overlap.get(1));
				}
			} else {
				joinPointsGroupedIntoJunctions.union(overlap.get(0), overlap.get(1));*/
				/*if (joinableI instanceof HatchedBond) {
					joinables.remove(whichLineIsWhichSingleBond.get(mutuallyExclusivePairs.get(joinableI)));
				}
				if (joinableJ instanceof HatchedBond) {
					joinables.remove(whichLineIsWhichSingleBond.get(mutuallyExclusivePairs.get(joinableJ)));
				}
				if (joinableI instanceof SingleBond) {
					joinables.remove(mutuallyExclusivePairs.inverse().get(joinableI));
				}
				if (joinableJ instanceof SingleBond) {
					joinables.remove(mutuallyExclusivePairs.inverse().get(joinableJ));
				}*/
			//}
			/*if (joinablei instanceof JoinableScriptWord && joinablej instanceof JoinableScriptWord) {
				if (!((JoinableScriptWord) joinablei).getScriptWord().toUnderscoreAndCaretString().equals("H") && !((JoinableScriptWord) joinablej).getScriptWord().toUnderscoreAndCaretString().equals("H")) {
					continue;
				}
			}*/
		}
	}

	private void removeJoinable(Joinable joinable) {
		higherPrimitives.getJoinableList().remove(joinable);
		higherPrimitives.getDoubleBondList().remove(joinable);
		higherPrimitives.getHatchedBondList().remove(joinable);
		higherPrimitives.getLineChargeList().remove(joinable);
	}

	private void handleAmbiguities(UnionFind<JoinPoint> joinPointsGroupedIntoJunctions) {
		for (MutuallyExclusiveShortLineTriple triple : mutuallyExclusiveShortLineTriples) {
			JoinPoint singleBondFirst = triple.singleBond.getJoinPoints().get(0);
			JoinPoint singleBondSecond = triple.singleBond.getJoinPoints().get(1);
			JoinPoint hatchedBondFirst = triple.hatchedBond.getJoinPoints().get(0);
			JoinPoint hatchedBondSecond = triple.hatchedBond.getJoinPoints().get(1);
			JoinPoint minus = (triple.minus == null ? null : triple.minus.getJoinPoints().get(0));
			if (joinPointsGroupedIntoJunctions.getSizeOfPartition(singleBondFirst) == 1 && joinPointsGroupedIntoJunctions.getSizeOfPartition(singleBondSecond) == 1 && joinPointsGroupedIntoJunctions.getSizeOfPartition(hatchedBondFirst) > 1 && joinPointsGroupedIntoJunctions.getSizeOfPartition(hatchedBondSecond) > 1) {
				if (minus != null) {
					Set<JoinPoint> points = joinPointsGroupedIntoJunctions.getObjectsInPartitionOf(minus);
					joinPointsGroupedIntoJunctions.explode(points);
					Set<Joinable> joinables = new HashSet<Joinable>();
					for (JoinPoint p : points) {
						if (p.getJoinable() != triple.minus) {
							joinables.add(p.getJoinable());
						}
					}
					attemptToJoinListOfJoinables(new ArrayList<Joinable>(joinables), joinPointsGroupedIntoJunctions);
				}
				joinPointsGroupedIntoJunctions.remove(singleBondFirst);
				joinPointsGroupedIntoJunctions.remove(singleBondSecond);
				joinPointsGroupedIntoJunctions.remove(minus);
				removeJoinable(triple.singleBond);
				higherPrimitives.getLineList().remove(triple.line);
				removeJoinable(triple.minus);
			} else if (joinPointsGroupedIntoJunctions.getSizeOfPartition(singleBondFirst) == 1 && joinPointsGroupedIntoJunctions.getSizeOfPartition(singleBondSecond) == 1 && (joinPointsGroupedIntoJunctions.getSizeOfPartition(hatchedBondFirst) == 1 || joinPointsGroupedIntoJunctions.getSizeOfPartition(hatchedBondSecond) == 1) && minus != null) {
				joinPointsGroupedIntoJunctions.remove(singleBondFirst);
				joinPointsGroupedIntoJunctions.remove(singleBondSecond);
				joinPointsGroupedIntoJunctions.remove(hatchedBondFirst);
				joinPointsGroupedIntoJunctions.remove(hatchedBondSecond);
				removeJoinable(triple.singleBond);
				higherPrimitives.getLineList().remove(triple.line);
				removeJoinable(triple.hatchedBond);
				removeJoinable(triple.hatchedBond);
			} else {
				if (minus != null) {
					Set<JoinPoint> points = joinPointsGroupedIntoJunctions.getObjectsInPartitionOf(minus);
					joinPointsGroupedIntoJunctions.explode(points);
					Set<Joinable> joinables = new HashSet<Joinable>();
					for (JoinPoint p : points) {
						if (p.getJoinable() != triple.minus) {
							joinables.add(p.getJoinable());
						}
					}
					attemptToJoinListOfJoinables(new ArrayList<Joinable>(joinables), joinPointsGroupedIntoJunctions);
				}
				joinPointsGroupedIntoJunctions.remove(hatchedBondFirst);
				joinPointsGroupedIntoJunctions.remove(hatchedBondSecond);
				joinPointsGroupedIntoJunctions.remove(minus);
				removeJoinable(triple.hatchedBond);
				removeJoinable(triple.minus);
				removeJoinable(triple.hatchedBond);
				removeJoinable(triple.minus);
			}
		}
		
		for (MutuallyExclusiveShortLinePairTriple triple : mutuallyExclusiveShortLinePairTriples) {
			joinPointsGroupedIntoJunctions.remove(triple.singleBond1.getJoinPoints().get(0));
			joinPointsGroupedIntoJunctions.remove(triple.singleBond1.getJoinPoints().get(1));
			joinPointsGroupedIntoJunctions.remove(triple.singleBond2.getJoinPoints().get(0));
			joinPointsGroupedIntoJunctions.remove(triple.singleBond2.getJoinPoints().get(1));
			removeJoinable(triple.singleBond1);
			removeJoinable(triple.singleBond2);
			higherPrimitives.getLineList().remove(triple.line1);
			higherPrimitives.getLineList().remove(triple.line2);
			if (triple.doubleBond == null) {
				continue;
			}
			JoinPoint doubleBondFirst = triple.doubleBond.getJoinPoints().get(0);
			JoinPoint doubleBondSecond = triple.doubleBond.getJoinPoints().get(1);
			JoinPoint hatchedBondFirst = triple.hatchedBond.getJoinPoints().get(0);
			JoinPoint hatchedBondSecond = triple.hatchedBond.getJoinPoints().get(1);
			if ((joinPointsGroupedIntoJunctions.getSizeOfPartition(hatchedBondFirst) > 1 || joinPointsGroupedIntoJunctions.getSizeOfPartition(hatchedBondSecond) > 1) && joinPointsGroupedIntoJunctions.getSizeOfPartition(doubleBondFirst) == 1 && joinPointsGroupedIntoJunctions.getSizeOfPartition(doubleBondSecond) == 1) {
				joinPointsGroupedIntoJunctions.remove(doubleBondFirst);
				joinPointsGroupedIntoJunctions.remove(doubleBondSecond);
				removeJoinable(triple.doubleBond);
			} else {
				joinPointsGroupedIntoJunctions.remove(hatchedBondFirst);
				joinPointsGroupedIntoJunctions.remove(hatchedBondSecond);
				removeJoinable(triple.hatchedBond);
			}
		}
		
		pair: for (MutuallyExclusiveLinePairPair pair : mutuallyExclusiveLinePairPairs) {
			JoinPoint doubleBondFirst = pair.doubleBond.getJoinPoints().get(0);
			JoinPoint doubleBondSecond = pair.doubleBond.getJoinPoints().get(1);
			boolean sewn = joinPointsGroupedIntoJunctions.get(doubleBondFirst).equals(joinPointsGroupedIntoJunctions.get(pair.singleBond1.getJoinPoints().get(0)));
			sewn |= joinPointsGroupedIntoJunctions.get(doubleBondFirst).equals(joinPointsGroupedIntoJunctions.get(pair.singleBond1.getJoinPoints().get(1)));
			sewn |= joinPointsGroupedIntoJunctions.get(doubleBondFirst).equals(joinPointsGroupedIntoJunctions.get(pair.singleBond2.getJoinPoints().get(0)));
			sewn |= joinPointsGroupedIntoJunctions.get(doubleBondFirst).equals(joinPointsGroupedIntoJunctions.get(pair.singleBond2.getJoinPoints().get(1)));
			sewn |= joinPointsGroupedIntoJunctions.get(doubleBondSecond).equals(joinPointsGroupedIntoJunctions.get(pair.singleBond1.getJoinPoints().get(0)));
			sewn |= joinPointsGroupedIntoJunctions.get(doubleBondSecond).equals(joinPointsGroupedIntoJunctions.get(pair.singleBond1.getJoinPoints().get(1)));
			sewn |= joinPointsGroupedIntoJunctions.get(doubleBondSecond).equals(joinPointsGroupedIntoJunctions.get(pair.singleBond2.getJoinPoints().get(0)));
			sewn |= joinPointsGroupedIntoJunctions.get(doubleBondSecond).equals(joinPointsGroupedIntoJunctions.get(pair.singleBond2.getJoinPoints().get(1)));
			if (sewn) {
				Set<JoinPoint> points = joinPointsGroupedIntoJunctions.getObjectsInPartitionOf(doubleBondFirst);
				for (JoinPoint j1 : points) {
					int numberParallel = 0;
					for (JoinPoint j2 : points) {
						if (j1 != j2 && Joinable.areParallel(j1.getJoinable(), j2.getJoinable())) {
							numberParallel++;
							if (numberParallel == 4) {
								joinPointsGroupedIntoJunctions.explode(points);
								joinPointsGroupedIntoJunctions.remove(doubleBondFirst);
								joinPointsGroupedIntoJunctions.remove(doubleBondSecond);
								removeJoinable(pair.doubleBond);
								Set<Joinable> joinables = new HashSet<Joinable>();
								for (JoinPoint p : points) {
									if (p.getJoinable() != pair.doubleBond) {
										joinables.add(p.getJoinable());
									}
								}
								attemptToJoinListOfJoinables(new ArrayList<Joinable>(joinables), joinPointsGroupedIntoJunctions);
								/*for (JoinPoint p : newJunctionJoinPoints) {
									junctions.add(new Junction(p));
								}*/
								continue pair;
							}
						}
					}
				}
				points = joinPointsGroupedIntoJunctions.getObjectsInPartitionOf(doubleBondSecond);
				for (JoinPoint j1 : points) {
					int numberParallel = 0;
					for (JoinPoint j2 : points) {
						if (j1 != j2 && Joinable.areParallel(j1.getJoinable(), j2.getJoinable())) {
							numberParallel++;
							if (numberParallel == 4) {
								joinPointsGroupedIntoJunctions.explode(points);
								joinPointsGroupedIntoJunctions.remove(doubleBondFirst);
								joinPointsGroupedIntoJunctions.remove(doubleBondSecond);
								removeJoinable(pair.doubleBond);
								Set<Joinable> joinables = new HashSet<Joinable>();
								for (JoinPoint p : points) {
									if (p.getJoinable() != pair.doubleBond) {
										joinables.add(p.getJoinable());
									}
								}
								attemptToJoinListOfJoinables(new ArrayList<Joinable>(joinables), joinPointsGroupedIntoJunctions);
								/*for (JoinPoint p : newJunctionJoinPoints) {
									junctions.add(new Junction(p));
								}*/
								continue pair;
							}
						}
					}
				}
				joinPointsGroupedIntoJunctions.remove(pair.singleBond1.getJoinPoints().get(0));
				joinPointsGroupedIntoJunctions.remove(pair.singleBond1.getJoinPoints().get(1));
				joinPointsGroupedIntoJunctions.remove(pair.singleBond2.getJoinPoints().get(0));
				joinPointsGroupedIntoJunctions.remove(pair.singleBond2.getJoinPoints().get(1));
				higherPrimitives.getLineList().remove(pair.line1);
				higherPrimitives.getLineList().remove(pair.line2);
				removeJoinable(pair.singleBond1);
				removeJoinable(pair.singleBond2);
			} else {
				joinPointsGroupedIntoJunctions.remove(pair.singleBond1.getJoinPoints().get(0));
				joinPointsGroupedIntoJunctions.remove(pair.singleBond1.getJoinPoints().get(1));
				joinPointsGroupedIntoJunctions.remove(pair.singleBond2.getJoinPoints().get(0));
				joinPointsGroupedIntoJunctions.remove(pair.singleBond2.getJoinPoints().get(1));
				higherPrimitives.getLineList().remove(pair.line1);
				higherPrimitives.getLineList().remove(pair.line2);
				removeJoinable(pair.singleBond1);
				removeJoinable(pair.singleBond2);
			}
		}
	}

	private boolean mutuallyExclusive(Joinable joinableI, Joinable joinableJ) {
		for (MutuallyExclusiveShortLineTriple triple : mutuallyExclusiveShortLineTriples) {
			if (joinableI == triple.hatchedBond && joinableJ == triple.minus || joinableJ == triple.hatchedBond && joinableI == triple.minus) {
				return true;
			}
			if (joinableI == triple.hatchedBond && joinableJ == triple.singleBond || joinableJ == triple.hatchedBond && joinableI == triple.singleBond) {
				return true;
			}
			if (joinableI == triple.singleBond && joinableJ == triple.minus || joinableJ == triple.singleBond && joinableI == triple.minus) {
				return true;
			}
		}
		
		for (MutuallyExclusiveShortLinePairTriple triple : mutuallyExclusiveShortLinePairTriples) {
			if (joinableI == triple.hatchedBond && joinableJ == triple.doubleBond || joinableJ == triple.hatchedBond && joinableI == triple.doubleBond) {
				return true;
			}
			if (joinableI == triple.hatchedBond && joinableJ == triple.singleBond1 || joinableJ == triple.hatchedBond && joinableI == triple.singleBond1) {
				return true;
			}
			if (joinableI == triple.singleBond1 && joinableJ == triple.doubleBond || joinableJ == triple.singleBond1 && joinableI == triple.doubleBond) {
				return true;
			}
			if (joinableI == triple.hatchedBond && joinableJ == triple.singleBond2 || joinableJ == triple.hatchedBond && joinableI == triple.singleBond2) {
				return true;
			}
			if (joinableI == triple.doubleBond && joinableJ == triple.singleBond2 || joinableJ == triple.doubleBond && joinableI == triple.singleBond2) {
				return true;
			}
			if (joinableI == triple.singleBond1 && joinableJ == triple.singleBond2 || joinableJ == triple.singleBond1 && joinableI == triple.singleBond2) {
				return true;
			}
		}
		
		/*for (MutuallyExclusiveLinePairPair pair : mutuallyExclusiveLinePairPairs) {
			if (joinableI == pair.singleBond2 && joinableJ == pair.doubleBond || joinableJ == pair.singleBond2 && joinableI == pair.doubleBond) {
				return true;
			}
			if (joinableI == pair.singleBond2 && joinableJ == pair.singleBond1 || joinableJ == pair.singleBond2 && joinableI == pair.singleBond1) {
				return true;
			}
			if (joinableI == pair.singleBond1 && joinableJ == pair.doubleBond || joinableJ == pair.singleBond1 && joinableI == pair.doubleBond) {
				return true;
			}
		}*/
		
		return false;
	}

	private List<JoinPoint> extractAtomLabelsAndGetRemainingJoinPoints(List<Joinable> joinables) {

		List<JoinPoint> remainingJoinPoints = new ArrayList<JoinPoint>();
		Map<Double, List<JoinableText>> listsOfTextsByFontSize = new LinkedHashMap<Double, List<JoinableText>>();
		for (Joinable j : joinables) {
			if (j instanceof JoinableText) {// && isLabel(((JoinableText) j).getSVGElement())) {// && !"1".equals(((JoinableText) j).getSVGElement().getText()) && !"2".equals(((JoinableText) j).getSVGElement().getText()) && !"3".equals(((JoinableText) j).getSVGElement().getText()) && !"4".equals(((JoinableText) j).getSVGElement().getText())) {
				List<JoinableText> joinablesForSize = listsOfTextsByFontSize.get(((JoinableText) j).getSVGElement().getFontSize());
				if (joinablesForSize == null) {
					joinablesForSize = new ArrayList<JoinableText>();
					listsOfTextsByFontSize.put(((JoinableText) j).getSVGElement().getFontSize(), joinablesForSize);
				}
				joinablesForSize.add((JoinableText) j);
			} else {
				remainingJoinPoints.addAll(j.getJoinPoints());
			}
		}
		
		double fontSizeOfLabels = Double.MAX_VALUE;
		list: for (Entry<Double, List<JoinableText>> list : listsOfTextsByFontSize.entrySet()) {
			AreInSameStringDetector sameString = new AreInSameStringDetector(list.getValue(), textCoordinateTolerance, textOverlapEpsilon);
			ImmutableCollection<Set<Joinable>> groups = sameString.texts.snapshot();
			//List<Integer> labelNumbers = new ArrayList<Integer>();
			Map<Real2Range, Integer> labelNumbers = new LinkedHashMap<Real2Range, Integer>();
			group: for (Set<Joinable> group : groups) {
				List<Joinable> potentialLabelTexts = new ArrayList<Joinable>(group);
				Joinable.sortJoinablesByX(potentialLabelTexts);
				String number = "";
				Real2Range bounds = new Real2Range();
				for (Joinable potentialLabelText : potentialLabelTexts) {
					if (!isLabel((SVGText) potentialLabelText.getSVGElement())) {
						for (Joinable t : group) {
							remainingJoinPoints.addAll(t.getJoinPoints());
						}
						continue group;
					}
					bounds.add(potentialLabelText.getJoinPoints().get(0).getPoint());
					if (isNumber((SVGText) potentialLabelText.getSVGElement())) {
						number += ((SVGText) potentialLabelText.getSVGElement()).getText();
					}
				}
				try {
					labelNumbers.put(bounds, Integer.parseInt(number));
				} catch (NumberFormatException e) {
					
				}
			}
			List<Integer> labelNumbersToBeSorted = new ArrayList<Integer>(labelNumbers.values());
			Collections.sort(labelNumbersToBeSorted);
			int previousPreviousLabel = 0;
			int previousLabel = 0;
			for (Integer i : labelNumbersToBeSorted) {
				if (i - previousLabel > labelNumbersToBeSorted.get(labelNumbersToBeSorted.size() - 1) * maximumLabelSequenceGap || (i == previousPreviousLabel && i != 2 && i != 3 && i != 4)) {
					for (JoinableText t : list.getValue()) {
						remainingJoinPoints.addAll(t.getJoinPoints());
					}
					continue list;
				}
				previousPreviousLabel = previousLabel;
				previousLabel = i;
			}
			if (list.getKey() < fontSizeOfLabels && labelNumbers.size() > 1) {
				if (atomLabelTexts != null) {
					for (JoinableText t : atomLabelTexts) {
						remainingJoinPoints.addAll(t.getJoinPoints());
					}
				}
				atomLabelTexts = list.getValue();
				atomLabelPositionsAndNumbers = labelNumbers;
				fontSizeOfLabels = list.getKey();
			} else {
				for (JoinableText t : list.getValue()) {
					remainingJoinPoints.addAll(t.getJoinPoints());
				}
			}
		}
		
		if (atomLabelTexts == null) {
			atomLabelTexts = new ArrayList<JoinableText>();
			atomLabelPositionsAndNumbers = new HashMap<Real2Range, Integer>();
		}

		return remainingJoinPoints;
	}
	
	private boolean isLetter(SVGText svgElement) {
		return (svgElement.getText() == null ? false : svgElement.getText().matches("[A-Za-z]"));
	}
	
	private boolean isNumber(SVGText svgElement) {
		return (svgElement.getText() == null ? false : svgElement.getText().matches("[0-9]"));
	}
	
	private boolean isLabel(SVGText svgElement) {
		return (svgElement.getText() == null ? false : svgElement.getText().matches("[0-9'a]"));
	}

	protected void createJoinableList() {
		List<Joinable> joinableList = createJoinableList(higherPrimitives.getLineList());
		joinableList.addAll(createJoinableList(derivedPrimitives.getPolygonList()));
		joinableList.addAll(createJoinableList(derivedPrimitives.getPathList()));
		joinableList.addAll(higherPrimitives.getDoubleBondList());
		joinableList.addAll(higherPrimitives.getHatchedBondList());
		joinableList.addAll(higherPrimitives.getLineChargeList());
		//joinableList.addAll(higherPrimitives.getWordList());
		joinableList.addAll(createJoinableList(derivedPrimitives.getTextList()));
		//joinableList.addAll(createJoinableList(derivedPrimitives.getImageList()));
		higherPrimitives.addJoinableList(joinableList);
	}
	
	public List<Joinable> createJoinableList(List<? extends SVGElement> elementList) {
		List<Joinable> joinableList = new ArrayList<Joinable>();
		for (SVGElement element : elementList) {
			Joinable joinable = createJoinable(element);
			if (joinable != null) {
				joinableList.add(joinable);
			}
		}
		return joinableList;
	}

	private Joinable createJoinable(SVGElement element) {
		Joinable joinable = null;
		if (element instanceof SVGLine) {
			joinable = new SingleBond((SVGLine) element);
			for (MutuallyExclusiveShortLineTriple triple : mutuallyExclusiveShortLineTriples) {
				if (triple.line == element) {
					triple.singleBond = (SingleBond) joinable;
				}
			}
			for (MutuallyExclusiveShortLinePairTriple triple : mutuallyExclusiveShortLinePairTriples) {
				if (triple.line1 == element) {
					triple.singleBond1 = (SingleBond) joinable;
				}
				if (triple.line2 == element) {
					triple.singleBond2 = (SingleBond) joinable;
				}
			}
			for (MutuallyExclusiveLinePairPair pair : mutuallyExclusiveLinePairPairs) {
				if (pair.line1 == element) {
					pair.singleBond1 = (SingleBond) joinable;
				}
				if (pair.line2 == element) {
					pair.singleBond2 = (SingleBond) joinable;
				}
			}
		} else if (element instanceof SVGText) {
			if (("+".equals(((SVGText) element).getText()) || "-".equals(((SVGText) element).getText())) && !JoinableText.anyTextsInSameString((SVGText) element, derivedPrimitives.getTextList(), textCoordinateTolerance, maximumSpaceWidthInEnSpaces) && !JoinableText.anyTextsToRightInSameString((SVGText) element, derivedPrimitives.getTextList(), textCoordinateTolerance, maximumSpaceWidthInEnSpaces, superscriptAndSubscriptOverlap)) {
				joinable = new Charge((SVGText) element);
			} else {
				joinable = new JoinableText((SVGText) element);
			}
 		} else if (element instanceof SVGPolygon && ((SVGPolygon) element).createLineList(true).size() == 3) {
 			double shortest = Double.MAX_VALUE;
 			for (SVGLine line : ((SVGPolygon) element).getLineList()) {
 				if (line.getLength() < shortest) {
 					shortest = line.getLength();
 				}
 			}
 			if (shortest > 0.5) {
 				joinable = new WedgeBond((SVGPolygon) element);
 				wedgeBonds.add((WedgeBond) joinable);
 			}
 		} else if (element instanceof SVGPolygon && ((SVGPolygon) element).createLineList(true).size() == 4) {
 			for (int i = 0; i < ((SVGPolygon) element).getReal2Array().size(); i++) {
 				Real2Array withoutPoint = new Real2Array(((SVGPolygon) element).getReal2Array());
 				Real2 deleted = withoutPoint.get(i);
 				withoutPoint.deleteElement(i);
 				SVGPolygon newPoly = new SVGPolygon(withoutPoint);
 				if (polygonContainsPoint(newPoly, deleted, 0)) {//withoutPoint.getRange2().includes(((SVGPolygon) element).getReal2Array().get(i))) {
 					((SVGPolygon) element).setReal2Array(withoutPoint);
 					joinable = new WedgeBond((SVGPolygon) element);
 	 				wedgeBonds.add((WedgeBond) joinable);
 					break;
 				}
 			}
 		} else if (element instanceof SVGPath) {
 			try {
 				joinable = new WigglyBond((SVGPath) element);
 			} catch (IllegalArgumentException e) {
 				
 			}
 		} else if (element instanceof SVGImage) {
 			element.applyTransformAttributeAndRemove();
 			joinable = new JoinableText(new SVGText(new Real2(0, 0), SVGUtil.getSVGXAttribute(element, "text")));
 			double halfTextHeightProportion = Double.parseDouble(SVGUtil.getSVGXAttribute(element, "textHeightProportion")) / 2;
 			((JoinableText) joinable).setJoinPoint(joinable.new JoinPoint(new Real2(element.getX() + element.getWidth() / 2, element.getY() + ((1 - halfTextHeightProportion) * element.getHeight())), halfTextHeightProportion * -element.getHeight() * textFromImageRadiusExpansion));
 		}
		if (joinable == null) {
 			LOG.trace("Unknown joinable: " + element);
 		}
		return joinable;
	}

	private void createDoubleBondList() {
		DoubleBondManager doubleBondManager = new DoubleBondManager();
		List<DoubleBond> doubleBondList;
		try {
			doubleBondList = doubleBondManager.createDoubleBondListWithoutReusingLines(higherPrimitives.getLineList(), startTime + timeout - System.currentTimeMillis());
		} catch (TimeoutException e) {
			throw new UncheckedTimeoutException(e.getMessage());
		}
		//doubleBondManager.removeUsedDoubleBondPrimitives(higherPrimitives.getLineList());
		higherPrimitives.setDoubleBondList(doubleBondList);
		mutuallyExclusiveLinePairPairs = new ArrayList<ChemistryBuilder.MutuallyExclusiveLinePairPair>();
		bond: for (DoubleBond bond : doubleBondList) {
			for (MutuallyExclusiveShortLinePairTriple pair : mutuallyExclusiveShortLinePairTriples) {
				if ((pair.line1 == bond.getLine(0) && pair.line2 == bond.getLine(1)) || (pair.line1 == bond.getLine(1) && pair.line2 == bond.getLine(0))) {
					pair.doubleBond = bond;
					continue bond;
				}
			}
			mutuallyExclusiveLinePairPairs.add(new MutuallyExclusiveLinePairPair(bond));
			//higherPrimitives.getLineList().add(bond.getLine(0));
			//higherPrimitives.getLineList().add(bond.getLine(1));
		}
	}

	/*private void createRawJunctionList() {
		createJoinableList();
		List<Joinable> joinableList = higherPrimitives.getJoinableList();
		List<Junction> rawJunctionList = new ArrayList<Junction>();
		for (int i = 0; i < joinableList.size() - 1; i++) {
			Joinable joinablei = joinableList.get(i);
			for (int j = i + 1; j < joinableList.size(); j++) {
				Joinable joinablej = joinableList.get(j);
				JoinPoint commonPoint = joinablei.getIntersectionPoint(joinablej);
				if (commonPoint != null) {
					Junction junction = new Junction(joinablei, joinablej, commonPoint);
					rawJunctionList.add(junction);
					String junctAttVal = "junct"+"."+rawJunctionList.size();
					junction.addAttribute(new Attribute(SVGElement.ID, junctAttVal));
					if (junction.getCoordinates() == null && commonPoint.getPoint() != null) {
						junction.setCoordinates(commonPoint.getPoint());
					}
					LOG.debug("junct: "+junction.getId()+" between " + joinablei.getClass() + " and " + joinablej.getClass() + " with coords "+junction.getCoordinates()+" "+commonPoint.getPoint());
				}
			}
		}
		higherPrimitives.setRawJunctionList(rawJunctionList);
	}*/

	public SVGElement getSVGRoot() {
		return svgRoot;
	}

	public HigherPrimitives getHigherPrimitives() {
		return higherPrimitives;
	}
	
	public Map<Real2Range, Integer> getAtomLabels() {
		return atomLabelPositionsAndNumbers;
	}

	void draw() {
		draw(new File("target/chem/andy.svg"));
	}
	
	public void draw(File file) {
		SVGG out = drawPrimitivesJoinPointsAndJunctions();
		SVGSVG.wrapAndWriteAsSVG(out, file);
	}

	SVGG drawPrimitivesJoinPointsAndJunctions() {
		SVGG out = new SVGG();
		SVGG circles = new SVGG();
		out.appendChild(circles);
		if (higherPrimitives.getJunctionList() != null) {
			for (Junction j : higherPrimitives.getJunctionList()) {
				Real2 coords = (j.getCoordinates() == null ? new Real2(0, 0) : j.getCoordinates());
				SVGCircle c = new SVGCircle(coords, 1.2);
				c.setFill("#555555");
				c.setOpacity(0.7);
				c.setStrokeWidth(0.0);
				circles.appendChild(c);
				SVGText t = new SVGText(coords.plus(new Real2(1.5, Math.random() * 6)), j.getID());
				circles.appendChild(t);
				for (JoinPoint point : j.getJoinPoints()) {
					SVGLine line = new SVGLine(coords, point.getPoint());
					line.setStrokeWidth(0.05);
					circles.appendChild(line);
				}
			}
		}
		for (SVGText t : getDerivedPrimitives().getTextList()) {
			SVGText o = (SVGText) t.copy();
			out.appendChild(o);
		}
		for (SVGLine l : getDerivedPrimitives().getLineList()) {
			SVGLine o = (SVGLine) l.copy();
			o.setStrokeWidth(0.4);
			out.appendChild(o);
		}
		for (SVGPolygon p : getDerivedPrimitives().getPolygonList()) {
			SVGPolygon o = (SVGPolygon) p.copy();
			o.setStrokeWidth(0.4);
			out.appendChild(o);
		}
		for (SVGPath p : getDerivedPrimitives().getPathList()) {
			SVGPath o = (SVGPath) p.copy();
			o.setStrokeWidth(0.4);
			out.appendChild(o);
		}
		for (Charge t : getHigherPrimitives().getLineChargeList()) {
			if (t.getSVGElement() != null) {
				SVGElement e = (SVGElement) t.getSVGElement().copy();
				out.appendChild(e);
			}
		}
		/*for (SVGImage t : simpleBuilder.getDerivedPrimitives().getImageList()) {
			SVGText e = new SVGText
			out.appendChild(e);
		}*/
		if (getHigherPrimitives().getJoinableList() != null) {
			for (Joinable j : getHigherPrimitives().getJoinableList()) {
				for (JoinPoint p : j.getJoinPoints()) {
					Real2 coords = (p.getPoint() == null ? new Real2(0, 0) : p.getPoint());
					SVGCircle c = new SVGCircle(coords, p.getRadius());
					if (j instanceof SingleBond) {
						c.setFill("#9999FF");
					} else if (j instanceof DoubleBond) {
						c.setFill("#99FF99");
					} else if (j instanceof HatchedBond) {
						c.setFill("#FF9999");
					} else if (j instanceof WedgeBond) {
						c.setFill("#99FFFF");
					} else if (j instanceof Charge) {
						c.setFill("#FFFF99");
					} else if (j instanceof JoinableText) {
						c.setFill("#FF99FF");
					} else if (j instanceof WigglyBond) {
						c.setFill("#999999");
					}
					c.setOpacity(0.7);
					c.setStrokeWidth(0.0);
					circles.appendChild(c);
					//SVGText t = new SVGText(coords.plus(new Real2(1.5, Math.random() * 6)), j.getId());
					//out.appendChild(t);
				}
			}
		}
		return out;
	}
	
}