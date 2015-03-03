package org.xmlcml.ami.visitor.words;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.xml.XMLVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;

public class RegexVisitor extends AbstractVisitor {

	final static Logger LOG = Logger.getLogger(RegexVisitor.class);

	private final static File WORD_DIRECTORY_BASE = new File("src/main/resources/org/xmlcml/ami/visitor/word");
	private final static String WORD_SUFFIX = ".xml";
	


	public RegexVisitor() {
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
		return new WordSearcher(this);
	}
	
	public static void main(String[] args) throws Exception {
		RegexVisitor wordVisitor = new RegexVisitor();
		wordVisitor.processArgs(args);
	}
	
	@Override
	public void usage() {
		super.usage();
		
	}

	@Override
	protected void runArgProcessor(String[] commandLineArgs) {
		argProcessor = new WordArgProcessor(commandLineArgs);
		processArgs();
	}


}
