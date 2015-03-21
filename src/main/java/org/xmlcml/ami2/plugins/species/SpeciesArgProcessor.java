package org.xmlcml.ami2.plugins.species;

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
public class SpeciesArgProcessor extends AMIArgProcessor {
	
	
	public static final Logger LOG = Logger.getLogger(SpeciesArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public SpeciesArgProcessor() {
		super();
	}

	public SpeciesArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	/** select methods to use
	 * 
	 * @param option list of methods (none gives help)
	 * @param argIterator
	 */
	public void parseSpecies(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			helpMethods();
		} else {
//			chosenMethods = getChosenList(ANALYSIS_METHODS, tokens);
		}
	}
	
	public void parseSpeciesTypes(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			helpSpeciesTypes();
		} else {
//			chosenWordTypes = getChosenList(WORD_TYPES, tokens);
		}
	}
	
	public void runSpecies(ArgumentOption option) {
		List<File> outputDirectories = currentQuickscrapeNorma.createResultsDirectoriesAndOutputResultsElement(
				option, resultsElementList, QuickscrapeNorma.RESULTS_XML);
		for (int i = 0; i < outputDirectories.size(); i++) {
			File outputDirectory = outputDirectories.get(i);
			File htmlFile = new File(outputDirectory, QuickscrapeNorma.RESULTS_HTML);
//			writeResultsElementAsHTML(htmlFile, (WordResultsElement)resultsElementList.get(i));
		}
	}
	
	public void outputSpecies(ArgumentOption option) {
		List<File> outputDirectories = currentQuickscrapeNorma.createResultsDirectoriesAndOutputResultsElement(
				option, resultsElementList, QuickscrapeNorma.RESULTS_XML);
		for (int i = 0; i < outputDirectories.size(); i++) {
			File outputDirectory = outputDirectories.get(i);
			File htmlFile = new File(outputDirectory, QuickscrapeNorma.RESULTS_HTML);
//			writeResultsElementAsHTML(htmlFile, (WordResultsElement)resultsElementList.get(i));
		}
	}
	
	public void parseSummary(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
			LOG.error("parseSummary needs a list of actions");
		} else {
//			summaryMethods = tokens;
		}
	}
	
	public void finalSummary(ArgumentOption option) {
//		WordResultsElementList frequenciesElementList = this.aggregateOverQSNormaList(SequenceArgProcessor.WORD_ARG_PROCESSOR, SequenceArgProcessor.FREQUENCIES);
//		WordCollectionFactory wordCollectionFactory = new WordCollectionFactory(this);
//		for (String method : summaryMethods) {
//			runSummaryMethod(frequenciesElementList, wordCollectionFactory, method);
//		}
	}

	// =============================

	private static void writeResultsElement(File outputFile, ResultsElement resultsElement) {
		try {
			outputFile.getParentFile().mkdirs();
			XMLUtil.debug(resultsElement, new FileOutputStream(outputFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write file "+outputFile, e);
		}
	}
	
	private void helpMethods() {
		// TODO Auto-generated method stub
		
	}

	private void helpSpeciesTypes() {
		// TODO Auto-generated method stub
		
	}


	public static void main(String[] args) {
		new SpeciesArgProcessor();
	}
}
