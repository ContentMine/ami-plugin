package org.xmlcml.ami.visitor.words;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.visitor.AMIArgProcessor;
import org.xmlcml.ami.visitor.regex.RegexContainer;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class WordArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(WordArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static String RESOURCE_WORD_NAME_TOP = AMIArgProcessor.RESOURCE_NAME_TOP + "/words";
	private static String ARGS_RESOURCE = RESOURCE_WORD_NAME_TOP+"/"+"args.xml";
	private List<WordSetWrapper> stopwordSetList;

	public WordArgProcessor() {
		super();
		this.readArgumentOptions(ARGS_RESOURCE);
        for (ArgumentOption argumentOption : argumentOptionList) {
			LOG.trace("WORD "+argumentOption.getHelp());
		}
	}

	public WordArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	public void parseWords(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		List<String> stopwordLocations = option.processArgs(tokens).getStringValues();
		ensureStopwordSetList();
		for (String stopwordLocation : stopwordLocations) {
			WordSetWrapper stopwordSet = WordSetWrapper.createStopwordSet(stopwordLocation);
			if (stopwordSet != null) {
				stopwordSetList.add(stopwordSet);
			}
		}
	}
	
	// =============================

	private void ensureStopwordSetList() {
		if (stopwordSetList == null) {
			stopwordSetList = new ArrayList<WordSetWrapper>();
		}
	}

	protected void printHelp() {
		System.out.println(
			"\n"
			+ "====REGEX====\n"
					+ "process regex help NYI"
			);
		super.printHelp();
	}

}
