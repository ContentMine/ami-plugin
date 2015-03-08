package org.xmlcml.ami.plugin.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.XPathProcessor;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.args.DefaultArgProcessor;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlFactory;
import org.xmlcml.html.HtmlP;

/** 
 * Processes commandline arguments.
 * for Norma
 * 
 * @author pm286
 */
public class AMIArgProcessor extends DefaultArgProcessor{
	
	public static final Logger LOG = Logger.getLogger(AMIArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected static String RESOURCE_NAME_TOP = "/org/xmlcml/ami";
	protected static String PLUGIN_RESOURCE = RESOURCE_NAME_TOP+"/plugin";
	private static String ARGS_RESOURCE = PLUGIN_RESOURCE+"/"+"args.xml";

	public static final String WORD_FREQUENCIES = "wordFrequencies";
	public static final String WORD_LENGTHS = "wordLengths";
	public static List<String> HARDCODED_PARAMS = Arrays.asList(new String[] {WORD_LENGTHS, WORD_FREQUENCIES});

	Integer[] contextCount = new Integer[] {98, 98};
	private List<String> params;
	private XPathProcessor xPathProcessor;
	protected List<String> words;

	public AMIArgProcessor() {
		super();
		this.readArgumentOptions(ARGS_RESOURCE);
	}

	public AMIArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// ============= METHODS =============
	
	public void parseContext(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		if (tokens.size() == 0) {
			throw new IllegalArgumentException("required argument/s missing");
		}
		contextCount[0] = new Integer(tokens.get(0));
		if (tokens.size() == 2) {
			contextCount[1] = new Integer(tokens.get(1));
		} else {
			contextCount[1] = contextCount[0];
		}
	}

	public void parseParam(ArgumentOption option, ArgIterator argIterator) {
		setParams(argIterator.createTokenListUpToNextMinus(option));
		for (String param : getParams()) {
			if (!HARDCODED_PARAMS.contains(param)) {
				LOG.debug("The parameters can be "+HARDCODED_PARAMS +"found..."+getParams()+";");
				throw new RuntimeException("Bad param: "+param);
			}
		}
	}

	public void parseTest(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		LOG.debug("The test strings are..."+tokens+"; override this if you want to use your own parseTest()");
	}

	public void parseXpath(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextMinus(option);
		if (tokens.size() == 0) {
//			LOG.debug(XPATH_OPTION).getHelp());
		} else if (tokens.size() > 1) {
			LOG.debug("Exactly one xpath required");
		} else {
			xPathProcessor = new XPathProcessor(tokens.get(0));
		}
	}


	public void printHelp() {
		System.err.println(
				"\n"
				+ "====AMI====\n"
				);
		super.printHelp();
	}

	
	// =============transformations=============
	
	

	// ============output options==============
	



	// ==========================

	
	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}


	@Override
	/** parse args and resolve their dependencies.
	 * 
	 * (don't run any argument actions)
	 * 
	 */
	public void parseArgs(String[] args) {
		super.parseArgs(args);
	}

	protected List<HtmlP> extractPElements() {
		HtmlElement htmlElement = AMIArgProcessor.getScholarlyHtmlElement(currentQuickscrapeNorma);
		List<HtmlP> pElements = HtmlP.extractSelfAndDescendantIs(htmlElement);
		return pElements;
	}

	public List<String> extractWordsFromScholarlyHtml() {
		HtmlElement htmlElement = AMIArgProcessor.getScholarlyHtmlElement(currentQuickscrapeNorma);
		String value = htmlElement == null ? null : htmlElement.getValue();
		return value == null ? new ArrayList<String>() :  new ArrayList<String>(Arrays.asList(value.split("\\s+")));
	}

	/** gets the HtmlElement for ScholarlyHtml.
	 * 
	 * ugly static because Euclid cannot depend on html library.
	 * 
	 * @param qsNorma
	 * @return
	 */
	public static HtmlElement getScholarlyHtmlElement(QuickscrapeNorma qsNorma) {
		HtmlElement htmlElement = null;
		if (qsNorma != null && qsNorma.hasScholarlyHTML()) {
			File scholarlyHtmlFile = qsNorma.getExistingScholarlyHTML();
			try {
				htmlElement = new HtmlFactory().parse(scholarlyHtmlFile);
			} catch (Exception e) {
				LOG.error("Cannot create scholarlyHtmlElement");
			}
		}
		return htmlElement;
	}

	public Integer[] getContextCount() {
		return contextCount;
	}


}
