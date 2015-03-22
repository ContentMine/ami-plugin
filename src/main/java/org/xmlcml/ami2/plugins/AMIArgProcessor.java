package org.xmlcml.ami2.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.args.DefaultArgProcessor;
import org.xmlcml.files.QuickscrapeNorma;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlElement;
import org.xmlcml.html.HtmlFactory;
import org.xmlcml.html.HtmlP;

/** 
 * Processes commandline arguments.
 * for Norma
 * 
 * @author pm286
 */
public class AMIArgProcessor extends DefaultArgProcessor {
	
	public static final String RESULTS = "results";
	public static final Logger LOG = Logger.getLogger(AMIArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected static String RESOURCE_NAME_TOP = "/org/xmlcml/ami2";
	protected static String PLUGIN_RESOURCE = RESOURCE_NAME_TOP+"/plugins";
	private static String ARGS_RESOURCE = PLUGIN_RESOURCE+"/"+"args.xml";

	private static final String OVERWRITE = "overwrite";
	private static final String NO_DUPLICATES = "noDuplicates";
	private static final String MERGE = "merge";
	protected static final String NAME = "name";

	private Integer[] contextCount = new Integer[] {98, 98};
	private List<String> params;
	private XPathProcessor xPathProcessor;
	protected List<ResultsElement> resultsElementList;
	private String update;
	private String plugin;
	
	public AMIArgProcessor() {
		super();
		readArgsResourcesIntoOptions();
	}

	private void readArgsResourcesIntoOptions() {
		// the default ami2 options
		super.readArgumentOptions(ARGS_RESOURCE);
		// the plugin-specific options
		super.readArgumentOptions(createPluginArgsResourceName());
	}

	public AMIArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}
	
	protected String createPluginArgsResourceName() {
		String clazz = this.getClass().getSimpleName();
		plugin = clazz.replace("ArgProcessor", "").toLowerCase();
		return AMIArgProcessor.PLUGIN_RESOURCE + "/"+plugin+"/"+ARGS_XML;
	}



	// ============= METHODS =============
	
	public void parseContext(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
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

	public void parseUpdate(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() != 1) {
			throw new IllegalArgumentException("required single argument missing");
		}
		update = tokens.get(0);
	}

	public void parseParam(ArgumentOption option, ArgIterator argIterator) {
		setParams(argIterator.createTokenListUpToNextNonDigitMinus(option));
		for (String param : getParams()) {
//			if (!HARDCODED_PARAMS.contains(param)) {
//				LOG.debug("The parameters can be "+HARDCODED_PARAMS +"found..."+getParams()+";");
//				throw new RuntimeException("Bad param: "+param);
//			}
		}
	}

	public void parseTest(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		LOG.debug("The test strings are..."+tokens+"; override this if you want to use your own parseTest()");
	}

	public void parseXpath(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens = argIterator.createTokenListUpToNextNonDigitMinus(option);
		if (tokens.size() == 0) {
//			LOG.debug(XPATH_OPTION).getHelp());
		} else if (tokens.size() > 1) {
			LOG.debug("Exactly one xpath required");
		} else {
			xPathProcessor = new XPathProcessor(tokens.get(0));
		}
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

	public String getUpdate() {
		return update;
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
		HtmlElement htmlElement = getScholarlyHtmlElement(currentQuickscrapeNorma);
		List<HtmlP> pElements = HtmlP.extractSelfAndDescendantIs(htmlElement);
		return pElements;
	}

	public List<String> extractWordsFromScholarlyHtml() {
		HtmlElement htmlElement = getScholarlyHtmlElement(currentQuickscrapeNorma);
		String value = htmlElement == null ? null : htmlElement.getValue();
		return value == null ? new ArrayList<String>() :  new ArrayList<String>(Arrays.asList(value.split("\\s+")));
	}

	public Integer[] getContextCount() {
		return contextCount;
	}

	public void addResultsElement(ResultsElement resultsElement0) {
		ensureResultsElementList();
		String title = resultsElement0.getTitle();
		if (title == null) {
			throw new RuntimeException("Results Element must have title");
		}
		checkNoDuplicatedTitle(title);
		resultsElementList.add(resultsElement0);
	}

	private void checkNoDuplicatedTitle(String title) {
		for (ResultsElement resultsElement : resultsElementList) {
			if (title.equals(resultsElement.getTitle())) {
				String duplicates = getUpdate();
				if (OVERWRITE.equals(duplicates)) {
					// carry on
				} else if (NO_DUPLICATES.equals(duplicates)) {
					throw new RuntimeException("Cannot have two ResultsElement with same title: "+title);
				} else if (MERGE.equals(duplicates)) {
					throw new RuntimeException("Merge not supported: Cannot have two ResultsElement with same title: "+title);
				}
			}
		}
	}
	
	private void ensureResultsElementList() {
		if (resultsElementList == null) {
			resultsElementList = new ArrayList<ResultsElement>();
		}
	}

	public void parseArgsRunAndOutput(String[] args) {
		this.parseArgs(args);
		this.runAndOutput();
	}

	public String getPlugin() {
		return plugin;
	}

	/** gets the HtmlElement for ScholarlyHtml.
	 * 
	 * ugly static because Euclid cannot depend on html library.
	 * 
	 * DO NOT MOVE TO QuickscrapeNorma
	 * 
	 * @return
	 */
	public static HtmlElement getScholarlyHtmlElement(QuickscrapeNorma quickscrapeNorma) {
		HtmlElement htmlElement = null;
		if (quickscrapeNorma != null && quickscrapeNorma.hasScholarlyHTML()) {
			File scholarlyHtmlFile = quickscrapeNorma.getExistingScholarlyHTML();
			try {
				htmlElement = new HtmlFactory().parse(scholarlyHtmlFile);
			} catch (Exception e) {
				LOG.error("Cannot create scholarlyHtmlElement");
			}
		}
		return htmlElement;
	}


}
