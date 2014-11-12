package org.xmlcml.xhtml2stm.visitor.regex;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.ResultsElement;
import org.xmlcml.xhtml2stm.visitable.html.HtmlContainer;
import org.xmlcml.xhtml2stm.visitable.html.HtmlVisitable;
import org.xmlcml.xhtml2stm.visitable.xml.XMLContainer;
import org.xmlcml.xhtml2stm.visitable.xml.XMLVisitable;
import org.xmlcml.xhtml2stm.visitor.AbstractSearcher;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;
import org.xmlcml.xhtml2stm.visitor.ArgProcessor;

public class RegexVisitor extends AbstractVisitor {

	final static Logger LOG = Logger.getLogger(RegexVisitor.class);

	private final static File REGEX_DIRECTORY_BASE = new File("src/main/resources/org/xmlcml/xhtml2stm/visitor/regex");
	private final static String REGEX_SUFFIX = ".xml";
	
	private static final String G          = "-g";
	private static final String REGEX      = "--regex";

	private RegexContainer regexContainer;
	private List<String> regexFiles;


	public RegexVisitor() {
		ensureAndFillRegexContainer();
		LOG.trace("created... RegexContainer");
	}

	private void ensureAndFillRegexContainer() {
		if (regexContainer == null) {
			regexContainer = new RegexContainer();
		}
	}

	public List<CompoundRegex> getCompoundRegexList() {
		return regexContainer.getCompoundRegexList();
	}

	public CompoundRegex getCompoundRegex(String title) {
		return regexContainer.getCompoundRegexByTitle(title);
	}

	/**
	 * @return the regexContainer
	 */
	public RegexContainer getRegexContainer() {
		return regexContainer;
	}

	public void debug() {
		LOG.debug("regex list "+regexContainer.getCompoundRegexList());
		for (CompoundRegex compoundRegex : regexContainer.getCompoundRegexList()) {
			LOG.debug(compoundRegex.getTitle()+"/"+compoundRegex.getRegexValues().size());
		}
	}
	
	// ===================Called on Visitables===================
	
	@Override
	public void visit(HtmlVisitable htmlVisitable) {
		doVisit(htmlVisitable);
	}
	
	@Override
	public void visit(XMLVisitable xmlVisitable) {
		doVisit(xmlVisitable);
	}


	// =======================Called by Visitables===============

	@Override
	protected AbstractSearcher createSearcher() {
		return new RegexSearcher(this);
	}
	
	public static void main(String[] args) throws Exception {
		RegexVisitor regexVisitor = new RegexVisitor();
		regexVisitor.processArgs(args);
	}
	
	@Override
	public void usage() {
		super.usage();
		
	}

	@Override
	protected void additionalUsage() {
		System.err.println();
		System.err.println("    -g   --regex     <regexDictionary> [<regexDictionary> ...] ");
		System.err.println("                   where dictionary 'foo' is found in src/main/resources/org/xmlcml/xhtml2stm/visitor/regex/foo.xml (NYI)");
		System.err.println("                   so --regex 'foo bar' uses both foo.xml and bar.xml in that order");
		System.err.println("    ");
	}
	
	@Override
	public String getDescription() {
		return "Regex: Applies regular expressions to HTML or XML Visitables to extract information.";
	}

	@Override
	/** only reads URLs?
	 * 
	 */
	protected boolean processArg(String arg, ListIterator<String> listIterator) {
		boolean processed = false;
		if (G.equals(arg) || REGEX.equals(arg)) {
			regexFiles = extractArgs(listIterator);
			addRegexFiles(regexFiles);
			processed = true;
		}
		return processed;
	}

	private RegexContainer addRegexFiles(List<String> regexRoots) {
		List<File> regexFiles = new ArrayList<File>();
		for (String regexRoot : regexRoots) {
			File regexFile = new File(regexRoot);
			if (regexFile.exists() && !regexFile.isDirectory()) {
				regexFiles.add(regexFile);
			} else {
				throw new RuntimeException("Cannot find regexFile: "+regexFile);
			}
		}
		for (File regexFile : regexFiles) {
			regexContainer.readCompoundRegexFile(regexFile);
		}
		LOG.trace("regex container "+regexContainer.getCompoundRegexList());
		return regexContainer;
	}

	protected List<String> extractArgs(ListIterator<String> listIterator) {
		List<String> argList = new ArrayList<String>();
		while (listIterator.hasNext()) {
			String next = listIterator.next();
			if (next.startsWith(ArgProcessor.MINUS)) {
				listIterator.previous();
				break;
			}
			argList.add(next);
		}
		return argList;
	}

	public void addRegexFile(String filename) {
		File file = new File(filename);
		if (!file.exists() || file.isDirectory()) {
			throw new RuntimeException("cannot read regex file: "+filename);
		}
	}



}
