package org.xmlcml.ami2.plugins.simple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class SimpleArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(SimpleArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static String SIMPLE_RESOURCE_NAME = AMIArgProcessor.PLUGIN_RESOURCE + "/simple";
	private static String ARGS_RESOURCE = SIMPLE_RESOURCE_NAME+"/"+"args.xml";
	protected List<String> words;

	public SimpleArgProcessor() {
		super();
		this.readArgumentOptions(ARGS_RESOURCE);
	}

	public SimpleArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	public void parseSimple(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
//		List<String> stopwordLocations = option.processArgs(tokens).getStringValues();
	}
	
	public void countWords(ArgumentOption option) {
		words = extractWordsFromScholarlyHtml();
	}

	public void outputWordCounts(ArgumentOption option) {
		String outputFilename = getOutput();
		if (!QuickscrapeNorma.isReservedFilename(outputFilename)) {
			throw new RuntimeException("Output is not a reserved file: "+outputFilename);
		}
		ResultsElement resultsElement = new ResultsElement();
		ResultElement resultElement = new ResultElement();
		resultElement.setValue("wordCount", String.valueOf(words.size()));
		resultsElement.appendChild(resultElement);
		currentQuickscrapeNorma.writeResults(outputFilename, resultsElement);
	}
	
	// =============================

	@Override
	/** parse args and resolve their dependencies.
	 * 
	 * (don't run any argument actions)
	 * 
	 */
	public void parseArgs(String[] args) {
		super.parseArgs(args);
	}

	
}
