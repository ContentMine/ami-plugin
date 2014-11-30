package org.xmlcml.ami.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.VisitableInput;
import org.xmlcml.ami.util.AMIUtil;

/** 
 * Processes commandline arguments.
 * 
 * @author pm286
 */
public class ArgProcessor {

	private final static Logger LOG = Logger.getLogger(ArgProcessor.class);
	
	private static final String I          = "-i";
	private static final String INPUT      = "--input";
	private static final String O          = "-o";
	private static final String OUTPUT     = "--output";
	private static final String R          = "-r";
	private static final String RECURSIVE  = "--recursive";
	private static final String E          = "-e";
	private static final String EXTENSIONS = "--extensions";
	private static final String X          = "-x";
	private static final String XPATH      = "--xpath";
	private static final String[] DEFAULT_EXTENSIONS = {AMIUtil.HTM};
	
	public static final String MINUS = "-";
	
	private VisitableInput visitableInput;
	private VisitorOutput visitorOutput;
	private XPathProcessor xPathProcessor;
	private List<String> extensions = Arrays.asList(DEFAULT_EXTENSIONS);
	private boolean recursive = false;

	private AbstractVisitor visitor;
	
	public ArgProcessor(String[] commandLineArgs, AbstractVisitor visitor) {
		this.visitor = visitor;
		processArgs(Arrays.asList(commandLineArgs));
	}

	private void processArgs(List<String> args) {
		ListIterator<String> listIterator = args.listIterator();
		while (listIterator.hasNext()) {
			String arg = listIterator.next();
			if (!arg.startsWith(MINUS)) {
				LOG.error("Parsing failed at: ("+arg+"), expected \"-\" trying to recover");
				continue;
			}
			if (E.equals(arg) || EXTENSIONS.equals(arg)) {processExtensions(listIterator); continue;}
			if (I.equals(arg) || INPUT.equals(arg)) {processInput(listIterator); continue;}
			if (O.equals(arg) || OUTPUT.equals(arg)) {processOutput(listIterator); continue;}
			if (R.equals(arg) || RECURSIVE.equals(arg)) {processRecursive(listIterator); continue;}
			if (X.equals(arg) || XPATH.equals(arg)) {processXpath(listIterator); continue;}
			if (visitor.processArg(arg, listIterator)) {continue;}
			LOG.error("Unknown arg: ("+arg+"), trying to recover");
		}
	}

	private void processInput(ListIterator<String> listIterator) {
		checkHasNext(listIterator);
		String input = listIterator.next();
		visitableInput = new VisitableInput(input);
	}

	private void processOutput(ListIterator<String> listIterator) {
		checkHasNext(listIterator);
		String output = listIterator.next();
		visitorOutput = new VisitorOutput(output);
	}

	private void processRecursive(ListIterator<String> listIterator) {
		recursive = true;
	}

	private void processXpath(ListIterator<String> listIterator) {
		checkHasNext(listIterator);
		String xpath = listIterator.next();
		xPathProcessor = new XPathProcessor(xpath);
	}

	private void processExtensions(ListIterator<String> listIterator) {
		extensions = new ArrayList<String>();
		while (listIterator.hasNext()) {
			String next = listIterator.next();
			if (next.startsWith(MINUS)) {
				listIterator.previous();
				break;
			}
			extensions.add(next);
		}
	}

	private void checkHasNext(ListIterator<String> listIterator) {
		if (!listIterator.hasNext()) {
			throw new RuntimeException("ran off end; expected more arguments");
		}
	}

	public VisitableInput getVisitableInput() {
		return visitableInput;
	}

	public VisitorOutput getVisitorOutput() {
		return visitorOutput;
	}

	/** 
	 * @return allowed extensions for input
	 */
	public List<String> getExtensions() {
		return extensions;
	}

	public boolean isRecursive() {
		return recursive;
	}

	public XPathProcessor getXPathProcessor() {
		return xPathProcessor;
	}

}
