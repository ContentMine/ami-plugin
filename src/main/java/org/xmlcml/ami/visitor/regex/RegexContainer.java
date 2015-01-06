package org.xmlcml.ami.visitor.regex;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/** contains the regexLists to use.
 * 
 * @author pm286
 *
 */
public class RegexContainer {

	final static Logger LOG = Logger.getLogger(RegexContainer.class);

	private static final String SRC_MAIN_RESOURCES = "src/main/resources/";
	private static final String XHTML2STM_RESOURCES = SRC_MAIN_RESOURCES+"org/xmlcml/ami/";
	private static final String MAIN_REGEX_DIR_NAME = XHTML2STM_RESOURCES+"visitor/regex";
	private static final File MAIN_REGEX_DIR = new File(MAIN_REGEX_DIR_NAME);

	private Map<String, CompoundRegex> compoundRegexByTitleMap;
	private List<CompoundRegex> compoundRegexList;

	public RegexContainer() {
	}

	/** reads all dictionaries in directory.
	 * 
	 * @param regexDir
	 */
	public void readCompoundRegexList(File regexDir) {
		File[] files = regexDir.listFiles();
		if (files != null) {
			readCompoundRegexes(files);
		}
		LOG.debug("read "+compoundRegexList+" regex files in; "+regexDir);
	}

	/** read given list of files.
	 * 
	 * @param files
	 */
	public void readCompoundRegexes(File[] files) {
		ensureCompoundRegexByTitleMap();
		ensureCompoundRegexList();
		for (File file : files) {
			readCompoundRegexFile(file);
		}
		LOG.debug("raw compound Regex "+compoundRegexList.size()+" "+((compoundRegexList.size() > 0) ? compoundRegexList.get(0) : "null"));
		LOG.trace(compoundRegexByTitleMap);
	}

	void readCompoundRegexFile(File file) {
		CompoundRegex compoundRegex = CompoundRegex.readAndCreateRegex(file);
		addCompoundRegex(compoundRegex);
	}

	void readCompoundRegexURL(URL url) {
		CompoundRegex compoundRegex = CompoundRegex.readAndCreateRegex(url);
		addCompoundRegex(compoundRegex);
	}

	private void addCompoundRegex(CompoundRegex compoundRegex) {
		if (compoundRegex != null) {
			ensureCompoundRegexByTitleMap();
			String title = compoundRegex.getTitle();
			if (compoundRegexByTitleMap.get(title) != null) {
				LOG.debug("already read regexes for "+title);
			} else {
				compoundRegexList.add(compoundRegex);
				compoundRegexByTitleMap.put(title, compoundRegex);
				LOG.trace("read regexes for "+title);
			}
		}
	}
	
	void ensureCompoundRegexList() {
		if (compoundRegexList == null) {
			compoundRegexList = new ArrayList<CompoundRegex>();
		}
	}

	private void ensureCompoundRegexByTitleMap() {
		if (compoundRegexByTitleMap == null) {
			compoundRegexByTitleMap = new HashMap<String, CompoundRegex>();
			LOG.trace("created compoundRegexByTitleMap");
		}
		ensureCompoundRegexList();
	}

	public CompoundRegex getCompoundRegexByTitle(String title) {
		ensureCompoundRegexByTitleMap();
		return compoundRegexByTitleMap.get(title);
	}

	public List<CompoundRegex> getCompoundRegexList() {
		ensureCompoundRegexList();
		return compoundRegexList;
	}

}
