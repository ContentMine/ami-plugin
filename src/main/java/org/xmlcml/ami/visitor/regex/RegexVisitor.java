package org.xmlcml.ami.visitor.regex;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.pdf.PDFVisitable;
import org.xmlcml.ami.visitable.txt.TextVisitable;
import org.xmlcml.ami.visitable.xml.XMLVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.ArgProcessor;

public class RegexVisitor extends AbstractVisitor {

	final static Logger LOG = Logger.getLogger(RegexVisitor.class);

	private final static File REGEX_DIRECTORY_BASE = new File("src/main/resources/org/xmlcml/ami/visitor/regex");
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
	public void visit(PDFVisitable pdfVisitable) {
		doVisit(pdfVisitable);
	}
	
	@Override
	public void visit(HtmlVisitable htmlVisitable) {
		doVisit(htmlVisitable);
	}
	
	@Override
	public void visit(TextVisitable textVisitable) {
		doVisit(textVisitable);
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
		System.err.println("                   where dictionary 'foo' is found in src/main/resources/org/xmlcml/ami/visitor/regex/foo.xml (NYI)");
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

	private RegexContainer addRegexFiles(List<String> regexLocations) {
		List<File> regexFiles = new ArrayList<File>();
		List<URL> regexURLs = new ArrayList<URL>();
		for (String regexLocation : regexLocations) {
			File regexFile = new File(regexLocation);
			if (regexFile.exists() && !regexFile.isDirectory()) {
				regexFiles.add(regexFile);
			} else if (regexLocation.startsWith("http")) {
				try {
					URL regexURL = new URL(regexLocation);
					regexURLs.add(regexURL);
				} catch (Exception e) {
					LOG.error("Cannot create Regex from URL "+regexLocation+" "+e);
				}
			} else {
				throw new RuntimeException("Cannot find regexFile: "+regexFile);
			}
		}
		for (File regexFile : regexFiles) {
			regexContainer.readCompoundRegexFile(regexFile);
		}
		for (URL regexURL : regexURLs) {
			regexContainer.readCompoundRegexURL(regexURL);
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
