package org.xmlcml.ami.plugin.plugins.regex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/** contains the regexLists to use.
 * 
 * @author pm286
 *
 */
public class CompoundRegexList implements Iterable<CompoundRegex> {

	final static Logger LOG = Logger.getLogger(CompoundRegexList.class);

	private static final String SRC_MAIN_RESOURCES = "src/main/resources/";
	private static final String XHTML2STM_RESOURCES = SRC_MAIN_RESOURCES+"org/xmlcml/ami/";
	private static final String MAIN_REGEX_DIR_NAME = XHTML2STM_RESOURCES+"plugin/regex";
	private static final File MAIN_REGEX_DIR = new File(MAIN_REGEX_DIR_NAME);

	private Map<String, CompoundRegex> compoundRegexByTitleMap;
	private List<CompoundRegex> compoundRegexList;

	private RegexArgProcessor regexArgProcessor;

	public CompoundRegexList(RegexArgProcessor regexArgProcessor) {
		this.regexArgProcessor = regexArgProcessor;
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
		ensureCompoundRegexByTitleMapAndCompoundRegexList();
		ensureCompoundRegexList();
		for (File file : files) {
			readCompoundRegexFile(file);
		}
		LOG.debug("raw compound Regex "+compoundRegexList.size()+" "+((compoundRegexList.size() > 0) ? compoundRegexList.get(0) : "null"));
		LOG.trace(compoundRegexByTitleMap);
	}

	void readCompoundRegexFile(File file) {
		try {
			CompoundRegex compoundRegex = regexArgProcessor.readAndCreateCompoundRegex(new FileInputStream(file));
			addCompoundRegex(compoundRegex);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read regex file:"+file);
		}
	}

	void readCompoundRegexURL(URL url) {
		try {
			CompoundRegex compoundRegex = regexArgProcessor.readAndCreateCompoundRegex(url.openStream());
			addCompoundRegex(compoundRegex);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read regex url:"+url);
		}
	}

	void readCompoundRegex(InputStream is) {
		CompoundRegex compoundRegex = regexArgProcessor.readAndCreateCompoundRegex(is);
		addCompoundRegex(compoundRegex);
	}

	void addCompoundRegex(CompoundRegex compoundRegex) {
		if (compoundRegex != null) {
			ensureCompoundRegexByTitleMapAndCompoundRegexList();
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

	private void ensureCompoundRegexByTitleMapAndCompoundRegexList() {
		if (compoundRegexByTitleMap == null) {
			compoundRegexByTitleMap = new HashMap<String, CompoundRegex>();
			LOG.trace("created compoundRegexByTitleMap");
		}
		ensureCompoundRegexList();
	}

	public CompoundRegex getCompoundRegexByTitle(String title) {
		ensureCompoundRegexByTitleMapAndCompoundRegexList();
		return compoundRegexByTitleMap.get(title);
	}

	public List<CompoundRegex> getCompoundRegexList() {
		ensureCompoundRegexList();
		return compoundRegexList;
	}
	
	@Override
	public String toString() {
		StringBuilder sb =  new StringBuilder("CompoundRegexList:\n");
		for (CompoundRegex compoundRegex : compoundRegexList) {
			sb.append(compoundRegex.toString()+"\n");
		}
		return sb.toString();
	}

	@Override
	public Iterator<CompoundRegex> iterator() {
		ensureCompoundRegexList();
		return compoundRegexList.iterator();
	}

	public void add(CompoundRegex compoundRegex) {
		ensureCompoundRegexList();
		compoundRegexList.add(compoundRegex);
	}

	public int size() {
		ensureCompoundRegexList();
		return compoundRegexList.size();
	}

}
