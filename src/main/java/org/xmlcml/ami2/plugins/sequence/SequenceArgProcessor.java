package org.xmlcml.ami2.plugins.sequence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.args.DefaultArgProcessor;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.euclid.IntRange;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.RealRange;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlP;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.html.HtmlStyle;
import org.xmlcml.xml.XMLUtil;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SequenceArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = Logger.getLogger(SequenceArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String SEQUENCE_ARG_PROCESSOR = "sequence";
	private static String RESOURCE_WORD_NAME_TOP = AMIArgProcessor.PLUGIN_RESOURCE + "/"+SEQUENCE_ARG_PROCESSOR;
	private static String ARGS_RESOURCE = RESOURCE_WORD_NAME_TOP+"/"+ARGS_XML;

	private static final String ALL_TEXT_PATH = "//text()";
	private static final String ALL_ELEMENTS_PATH = "//*[text()[not(normalize-space()='')]]";
	private static final String ALL_PARA_PATH = "//*[local-name()='p']";
	private static final String ALL_DIV_PATH = "//*[local-name()='div']";

	public enum SequenceType implements Type {
		MIXED,
		CARBOHYDRATE,
		DNA,
		PROTEIN,
		RNA,
	}

	// allow for 5' and 3' ends. ' may be strange char, so use dot
	public static final String DNA_REGEX_S = "\\s*((5.\\s*\\-\\s*)?\\s*[ACGT]{6,}(\\s*\\-\\s*3.\\s*)?)";
	public final static Pattern DNA_PATTERN = Pattern.compile(DNA_REGEX_S);
	public static final String PROTEIN_REGEX_S = "\\s*[ACDEFGHIKLMNPQRSTVWY]{6,}\\s*";
	public final static Pattern PROTEIN_PATTERN = Pattern.compile(PROTEIN_REGEX_S);
	public static final String RNA_REGEX_S = "\\s*[ACGU]{6,}\\s*";
	public final static Pattern RNA_PATTERN = Pattern.compile(RNA_REGEX_S);
	private List<String> summaryMethods;

	public SequenceArgProcessor() {
		super();
	}

	public SequenceArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	/** select methods to use
	 * 
	 * @param option list of methods (none gives help)
	 * @param argIterator
	 */
	public void parseSequence(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			helpMethods();
		} else {
//			chosenMethods = getChosenList(ANALYSIS_METHODS, tokens);
		}
	}

	
	public void parseSequenceTypes(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			helpSequenceTypes();
		} else {
//			chosenWordTypes = getChosenList(WORD_TYPES, tokens);
		}
	}
	
	public void outputSequences(ArgumentOption option) {
		List<File> outputDirectories = currentQuickscrapeNorma.createResultsDirectoriesAndOutputResultsElement(
				option, resultsElementList, QuickscrapeNorma.RESULTS_XML);
		for (int i = 0; i < outputDirectories.size(); i++) {
			File outputDirectory = outputDirectories.get(i);
			File htmlFile = new File(outputDirectory, QuickscrapeNorma.RESULTS_HTML);
//			writeResultsElementAsHTML(htmlFile, (WordResultsElement)resultsElementList.get(i));
		}
	}
	
	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
		summaryMethods = argIterator.createTokenListUpToNextNonDigitMinus(option);
	}
	
	public void finalSummary(ArgumentOption option) {
//		WordResultsElementList frequenciesElementList = this.aggregateOverQSNormaList(SequenceArgProcessor.WORD_ARG_PROCESSOR, SequenceArgProcessor.FREQUENCIES);
//		WordCollectionFactory wordCollectionFactory = new WordCollectionFactory(this);
//		for (String method : summaryMethods) {
//			runSummaryMethod(frequenciesElementList, wordCollectionFactory, method);
//		}
	}

	private static void writeResultsElement(File outputFile, ResultsElement resultsElement) {
		try {
			outputFile.getParentFile().mkdirs();
			XMLUtil.debug(resultsElement, new FileOutputStream(outputFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file "+outputFile, e);
		}
	}
	
	// =============================

	private void helpMethods() {
		// TODO Auto-generated method stub
		
	}

	private void helpSequenceTypes() {
		// TODO Auto-generated method stub
		
	}


	private void addSequenceType0(SequenceType type) {
		if (SequenceType.DNA.equals(type)) {
			this.addPattern(DNA_PATTERN);
		} else if (SequenceType.DNA.equals(type)) {
			this.addPattern(RNA_PATTERN);
		} else {
			throw new RuntimeException("Type not supported: "+type);
		}
//		this.sequenceType = (documentFragment.getPatternList().size() == 1) ? type : SequenceType.MIXED;
	}

	private void addPattern(Pattern dnaPattern) {
		// TODO Auto-generated method stub
		
	}




//	private void helpMethods() {
//		System.err.println("ANALYSIS METHODS");
//		for (String method : ANALYSIS_METHODS) {
//			System.err.println("  "+method);
//		}
//	}
//	
//	private void helpWordTypes() {
//		System.err.println("WORD TYPES");
//		for (String type : WORD_TYPES) {
//			System.err.println("  "+type);
//		}
//	}


}
