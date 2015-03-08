package org.xmlcml.ami.plugin.plugins.regex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.plugins.AMIArgProcessor;
import org.xmlcml.args.ArgIterator;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.files.EuclidSource;
import org.xmlcml.html.HtmlP;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class RegexArgProcessor extends AMIArgProcessor {
	
	public static final Logger LOG = Logger.getLogger(RegexArgProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static String REGEX_RESOURCE_NAME = AMIArgProcessor.PLUGIN_RESOURCE + "/regex";
	private static String ARGS_RESOURCE = REGEX_RESOURCE_NAME+"/"+"args.xml";

	private List<CompoundRegexList> regexContainerList;
	
	public RegexArgProcessor() {
		super();
		this.readArgumentOptions(ARGS_RESOURCE);
        for (ArgumentOption argumentOption : argumentOptionList) {
			LOG.trace("REGEX "+argumentOption.getHelp());
		}
	}

	public RegexArgProcessor(String[] args) {
		this();
		parseArgs(args);
	}

	// =============== METHODS ==============

	public void parseRegex(ArgumentOption option, ArgIterator argIterator) {
		List<String> tokens= argIterator.createTokenListUpToNextMinus(option);
		List<String> regexLocations = option.processArgs(tokens).getStringValues();
		ensureRegexContainerList();
		for (String regexLocation : regexLocations) {
			try {
				CompoundRegexList regexContainer = parseRegex(regexLocation);
				regexContainerList.add(regexContainer);
			} catch (IOException e) {
				LOG.error("Could not parse "+regexLocation);
			}
		}
		for (CompoundRegexList regexContainer : regexContainerList) {
			LOG.debug(regexContainer);
		}
	}
	
	public void runRegex(ArgumentOption option) {
		LOG.debug("Running REGEX NYI");
		List<HtmlP> pElements = extractPElements();
		for (HtmlP pElement : pElements) {
//			search(pElement);
		}
	}

	public void outputResultElements(ArgumentOption option) {
		LOG.debug("Output REGEX NYI");
	}

	// =============================

	private void ensureRegexContainerList() {
		if (regexContainerList == null) {
			regexContainerList = new ArrayList<CompoundRegexList>();
		}
	}

	private CompoundRegexList parseRegex(String regexLocation) throws IOException {
		CompoundRegex compoundRegex = readAndCreateCompoundRegex(regexLocation);
		LOG.debug("CompoundRegex: "+compoundRegex.toString());
		CompoundRegexList regexContainer = new CompoundRegexList(this);
		regexContainer.addCompoundRegex(compoundRegex);
		return regexContainer;
	}

	public List<CompoundRegexList> getRegexContainerList() {
		ensureRegexContainerList();
		return regexContainerList;
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

	/** creates a regex from name if possible
	 * 	 * 
	 * @param name could be  resourceName, file or URL
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	public CompoundRegex readAndCreateCompoundRegex(String name) {
		return readAndCreateCompoundRegex(EuclidSource.getInputStream(name));
	}

	/** creates a regex from URL if possible
	 * 	 * 
	 * @param file
	 * @param url TODO
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	public CompoundRegex readAndCreateCompoundRegex(URL url) throws IOException{
		return readAndCreateCompoundRegex(url.openStream());
	}

	/** creates a regex from file if possible
	 * 
	 * Looks at *.xml
	 * 
	 * @param file
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	public CompoundRegex readAndCreateCompoundRegex(File file) throws IOException {
		return readAndCreateCompoundRegex(new FileInputStream(file));
	}

	/** creates a regex from InputStream if possible
	 * 	 * 
	 * @param file
	 * @param is TODO
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	public CompoundRegex readAndCreateCompoundRegex(InputStream is) {
		Element rootElement = null;
		try {
			Document doc = new Builder().build(is);
			rootElement = doc.getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot read or parse regexInputStream", e);
		}
		return new CompoundRegex(this, rootElement);
	}

}
