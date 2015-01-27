package org.xmlcml.ami.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.ami.util.AMIUtil;
import org.xmlcml.ami.visitable.VisitableInput;

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
	private static final String C          = "-c";
	private static final String CONTEXT    = "--context";
//	private static final String S          = "-s";
//	private static final String SECTIONS = "--sections";
	private static final String X          = "-x";
	private static final String XPATH      = "--xpath";
	private static final String[] DEFAULT_EXTENSIONS = {AMIUtil.HTM};
	private static Pattern INTEGER_RANGE = Pattern.compile("(.*)\\{(\\d+),(\\d+)\\}(.*)");

	
	public static final String MINUS = "-";
	
	private List<VisitableInput> visitableInputList;
	private VisitorOutput visitorOutput;
	private XPathProcessor xPathProcessor;
	private List<String> extensions = Arrays.asList(DEFAULT_EXTENSIONS);
	private boolean recursive = false;
	private AbstractVisitor visitor;
	private Integer contextSize;

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
			if (C.equals(arg) || CONTEXT.equals(arg)) {processContext(listIterator); continue;}
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
		List<String> inputs = createTokenListUpToNextMinus(listIterator);
		if (inputs.size() == 0) {
			visitableInputList = null;
			LOG.error("Must give at least one input");
		} else {
			if (inputs.size() == 1) {
				inputs = expandWildcards(inputs.get(0));
			}
			visitableInputList = new ArrayList<VisitableInput>();
			for (String input : inputs) {
				VisitableInput visitableInput = new VisitableInput(input);
				visitableInputList.add(visitableInput);
			}
		}
	}

	/** expand expressions/wildcards in input.
	 * 
	 * @param input
	 * @return
	 */
	private List<String> expandWildcards(String input) {
		Matcher matcher = INTEGER_RANGE.matcher(input);
		List<String> inputs = new ArrayList<String>();
		if (matcher.matches()) {
			int start = Integer.parseInt(matcher.group(2));
			int end = Integer.parseInt(matcher.group(3));
			if (start <= end) {
				for (int i = start; i <= end; i++) {
					String input0 = matcher.group(1)+i+matcher.group(4);
					inputs.add(input0);
				}
			}
		} else {
			inputs.add(input);
		}
		LOG.trace("inputs: "+inputs);
		return inputs;
	}

	private void processOutput(ListIterator<String> listIterator) {
		checkHasNext(listIterator);
		String output = listIterator.next();
		visitorOutput = new VisitorOutput(output);
	}

	private void processContext(ListIterator<String> listIterator) {
		checkHasNext(listIterator);
		String context = listIterator.next();
		contextSize = new Integer(context);
	}

	private void processRecursive(ListIterator<String> listIterator) {
		recursive = true;
	}

	private void processTagger(ListIterator<String> listIterator) {
		List<String> taggerNames = createTokenListUpToNextMinus(listIterator);
		visitor.setTaggers(taggerNames);
	}

	/** read tokens until next - sign.
	 * 
	 * leave iterator ready to read next minus
	 * 
	 * @param listIterator
	 * @return
	 */
	private static List<String> createTokenListUpToNextMinus(ListIterator<String> listIterator) {
		List<String> list = new ArrayList<String>();
		while (listIterator.hasNext()) {
			String next = listIterator.next();
			if (next.startsWith(MINUS)) {
				listIterator.previous();
				break;
			}
			list.add(next);
		}
		return list;
	}

	private void processXpath(ListIterator<String> listIterator) {
		checkHasNext(listIterator);
		String xpath = listIterator.next();
		xPathProcessor = new XPathProcessor(xpath);
	}

	private void processExtensions(ListIterator<String> listIterator) {
		extensions = createTokenListUpToNextMinus(listIterator);
	}

	private void checkHasNext(ListIterator<String> listIterator) {
		if (!listIterator.hasNext()) {
			throw new RuntimeException("ran off end; expected more arguments");
		}
	}

	public List<VisitableInput> getVisitableInputList() {
		return visitableInputList;
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
	
	public Integer getContextSize() {
		return contextSize;
	}

}
