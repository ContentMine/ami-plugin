package org.xmlcml.ami.visitor;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xmlcml.ami.result.ResultsListElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.visitable.AbstractVisitable;
import org.xmlcml.ami.visitable.SourceElement;
import org.xmlcml.ami.visitable.VisitableContainer;
import org.xmlcml.ami.visitable.VisitableInput;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.image.ImageContainer;
import org.xmlcml.ami.visitable.image.ImageVisitable;
import org.xmlcml.ami.visitable.pdf.PDFContainer;
import org.xmlcml.ami.visitable.pdf.PDFVisitable;
import org.xmlcml.ami.visitable.svg.SVGContainer;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitable.table.TableVisitable;
import org.xmlcml.ami.visitable.txt.TextContainer;
import org.xmlcml.ami.visitable.txt.TextVisitable;
import org.xmlcml.ami.visitable.xml.XMLContainer;
import org.xmlcml.ami.visitable.xml.XMLVisitable;

/** visits the visitables (data).
 * 
 * Normally the visitation is to carry out search.
 * Search is delegated to a searcher.
 * 
 * @author pm286
 *
 */
public abstract class AbstractVisitor {

	private final static Logger LOG = Logger.getLogger(AbstractVisitor.class);

	public static final String RESULTS_XML = "results.xml";
	private static final String XML = "xml";
	private static final String HTM = "htm";

	protected ResultsListElement resultsElement;
	protected SourceElement sourceElement;
	private List<VisitableInput> visitableInputList;
	private VisitorOutput visitorOutput;
	protected AbstractVisitable currentVisitable;
	private XPathProcessor xPathProcessor;
	private AbstractSearcher searcher;
	private File resultsFile;
	private List<String> taggerNames;

	// ============== VISITATION ========================
	
	public void visit(AbstractVisitable visitable) {
		// we seem to have to subclass to achieve double dispatch
		ensureResultsElement();
		// maybe need to loop though Containers in visitable
		visitSubclassed(visitable);
		currentVisitable = visitable;
	}

	/** The doVisit(Visitable) routines should be overridden in any subclass
	 * which wants to use them. This is normally through a stub of the form:
	 * 
	 * 	public void visit(HtmlVisitable htmlVisitable) {
	 * 		doVisit(htmlVisitable);
	 *  }
	 */
	
	/**
	 * HTML
	 * 
	 * Override as above if visitor can process HTML
	 * 
	 * @param htmlVisitable
	 */

	public void visit(HtmlVisitable htmlVisitable) {
		notYetImplemented(htmlVisitable);
	}
	
	protected final void doVisit(HtmlVisitable htmlVisitable) {
		List<HtmlContainer> htmlContainerList = htmlVisitable.getHtmlContainerList();
		for (HtmlContainer htmlContainer : htmlContainerList) {
			doSearchAndAddResults(htmlContainer);
		}
	}

	private void doSearchAndAddResults(VisitableContainer container) {
		LOG.trace("doSearchAndAddResults "+container.getClass());
		searcher = createSearcher();
		searcher.search(container);
		ensureResultsElement();
		SimpleResultList resultsList = searcher.getResultsList();
		if (resultsList != null) {
			Element element = resultsList.createElement();
			resultsElement.appendChild(element);
		}
	}

	/**
	 * XML
	 * 
	 * Override as above if visitor can process XML
	 * 
	 * @param xmlVisitable
	 */

	public void visit(XMLVisitable xmlVisitable) {
		notYetImplemented(xmlVisitable);
	}
	
	protected final void doVisit(XMLVisitable xmlVisitable) {
		List<XMLContainer> containerList = xmlVisitable.getXMLContainerList();
		LOG.debug("doVisit: searching containerList "+containerList.size());
		for (XMLContainer xmlContainer : containerList) {
			doSearchAndAddResults(xmlContainer);
		}
	}

	/**
	 * SVG
	 * 
	 * Override as above if visitor can process SVG
	 * 
	 * @param svgVisitable
	 */

	public void visit(SVGVisitable svgVisitable) {
		notYetImplemented(svgVisitable);
	}
	
	protected final void doVisit(SVGVisitable svgVisitable) {
		for (SVGContainer svgContainer : svgVisitable.getSVGContainerList()) {
			doSearchAndAddResults(svgContainer);
		}
	}

	/**
	 * Image
	 * 
	 * Override as above if visitor can process Image
	 * 
	 * @param imageVisitable
	 */

	public void visit(ImageVisitable imageVisitable) {
		notYetImplemented(imageVisitable);
	}

	protected final void doVisit(ImageVisitable imageVisitable) {
		for (ImageContainer imageContainer : imageVisitable.getImageContainerList()) {
			doSearchAndAddResults(imageContainer);
		}
	}


	/**
	 * PDF
	 * 
	 * @param pdfVisitable
	 */
	public void visit(PDFVisitable pdfVisitable) {
		notYetImplemented(pdfVisitable);
	}

	/** This may get altered to reflect PDF components.
	 * 
	 * @param pdfVisitable
	 */
	protected final void doVisit(PDFVisitable pdfVisitable) {
		if (pdfVisitable != null) {
			for (PDFContainer pdfContainer : pdfVisitable.getPDFContainerList()) {
				doSearchAndAddResults(pdfContainer);
			}
		}
	}

	/**
	 * Table
	 * 
	 * Not yet working - may get redesigned
	 * 
	 * @param tableVisitable
	 */
	public void visit(TableVisitable tableVisitable) {
		notYetImplemented(tableVisitable);
	}
	
	/**
	 * Table
	 * 
	 * Not yet working - may get redesigned
	 * 
	 * @param tableVisitable
	 */
	public void visit(TextVisitable textVisitable) {
		notYetImplemented(textVisitable);
	}
	
	protected final void doVisit(TextVisitable textVisitable) {
		for (TextContainer textContainer : textVisitable.getTextContainerList()) {
			doSearchAndAddResults(textContainer);
		}
	}

	private void visitSubclassed(AbstractVisitable visitable) {
		if (visitable instanceof HtmlVisitable) {
			this.visit((HtmlVisitable) visitable);
		} else if (visitable instanceof ImageVisitable) {
			this.visit((ImageVisitable) visitable);
		} else if (visitable instanceof XMLVisitable) {
			this.visit((XMLVisitable) visitable);
		} else if (visitable instanceof PDFVisitable) {
			this.visit((PDFVisitable) visitable);
		} else if (visitable instanceof SVGVisitable) {
			this.visit((SVGVisitable) visitable);
		} else if (visitable instanceof TableVisitable) {
			this.visit((TableVisitable) visitable);
		} else if (visitable instanceof TextVisitable) {
			this.visit((TextVisitable) visitable);
		} else {
			throw new RuntimeException("Unknown visitable: " + visitable);
		}
	}

	protected void notApplicable(AbstractVisitable visitable) {
		throw new RuntimeException(this.getClass().getName()
				+ " cannot be applied to " + visitable);
	}

	protected void notYetImplemented(AbstractVisitable visitable) {
		throw new RuntimeException(this.getClass().getName()
				+ " is not yet applicable to " + visitable.getClass().getName()+"; perhaps add doVisit() to visitor?");
	}

	protected String getDescription() {
		return "Visitor, often invoked by using specific command (e.g.'species') \n"+
			   "Command line of form: '[command] [options], command being 'species', 'sequence', etc.\n";

	}

	// ============== ARGUMENTS ================
	
	protected void usage() {
		System.err.println(getDescription());
		additionalUsage(); // for specific visitors
		System.err
				.println("Universal options ('-f' is short for '--foo', etc.):");
		System.err.println("    -i  --input  inputSpec");
		System.err
				.println("                 mandatory: filename, directoryName, url, or (coming RSN) identifier (e.g. PMID:12345678)");
		System.err.println("    -o  --output  outputSpec");
		System.err
				.println("                 mandatory: filename, directoryName");
		System.err.println("    -r  --recursive");
		System.err.println("                 recurse through directories");
		System.err.println("    -e  --extensions ext1 [ext2 ...]");
		System.err
				.println("                 mandatory if input directory: file extensions (htm, pdf, etc.)");
		System.err.println("");
	}

	protected void additionalUsage() {

	}

	private void runArgProcessor(String[] commandLineArgs) {
		ArgProcessor argProcessor = new ArgProcessor(commandLineArgs, this);
		
		createVisitableInputListFromArgs(argProcessor);
		createVisitableOutputFromArgs();
		
		visitVistablesAndWriteOutputFiles();
	}

	private void createVisitableInputListFromArgs(ArgProcessor argProcessor) {
		visitableInputList = argProcessor.getVisitableInputList();
		setVisitorOutput(argProcessor.getVisitorOutput());
		setXPathProcessor(argProcessor.getXPathProcessor());
		if (visitableInputList == null || visitableInputList.size() == 0) {
			throw new RuntimeException("input option mandatory");
		}
		createVistablesAndExtensionsForEachVisitable(argProcessor);
	}

	private void createVistablesAndExtensionsForEachVisitable(ArgProcessor argProcessor) {
		for (VisitableInput visitableInput : visitableInputList) {
			visitableInput.setExtensions(argProcessor.getExtensions());
			visitableInput.setRecursive(argProcessor.isRecursive());
			try {
				visitableInput.createVisitableList(this);
				LOG.debug("visitable list: "+visitableInput.getVisitableList().size());
				LOG.trace("in: " + visitableInputList);
			} catch (Exception e) {
				LOG.error("FAILED TO PARSE");
			}
		}
	}

	private void createVisitableOutputFromArgs() {
		if (getOrCreateVisitorOutput() == null) {
			setVisitorOutput(new VisitorOutput());
		}
		getOrCreateVisitorOutput().setVisitableInputList(visitableInputList);
		getOrCreateVisitorOutput().setExtension(XML);
	}

	private void setXPathProcessor(XPathProcessor xPathProcessor) {
		this.xPathProcessor = xPathProcessor;
	}
	
	public XPathProcessor getXPathProcessor() {
		return xPathProcessor;
	}

	/** Normal argument processing.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public void processArgs(String[] args) throws Exception {
		if (args != null && args.length > 0) {
			runArgProcessor(args);
		} else {
			usage();
		}
	}

	/**
	 * Overriden by subclasses which can have extra arguments.
	 * 
	 * @param arg immediate arg after "-foo" flag
	 * @param listIterator multiple argument values
	 */
	protected boolean processArg(String arg, ListIterator<String> listIterator) {
		return false;
	}
	
	// =============== SEARCH ===============

	/** searches the visitable container.
	 * 
	 * Wraps container in a SourceElement to carry metadata and context.
	 * 
	 * precise search is delegated to subclasses.
	 * 
	 * output is wrapped in resultsElement.
	 * 
	 * @param container
	 */
	public void searchContainer(VisitableContainer container) {
		AbstractSearcher searcher = AbstractSearcher.createDefaultSearcher(this);
		ensureResultsElement();
		SourceElement sourceElement = new SourceElement(container);
//		resultsElement.appendChild(sourceElement); // need to capture metadata somehow
		SimpleResultList resultList = searcher.searchXPathPatternAndCollectResults(sourceElement);
		resultsElement.appendChild(searcher.createListElement(resultList));
	}

	// ================= PDF search (needs rewriting) ==========
	/**
	 * Turns PDF to HTML and searches that.
	 * // FIXME need a better way 
	 * @param pdfVisitable
	 */
	protected void createAndSearchHtmlContainer(PDFVisitable pdfVisitable) {
		List<PDFContainer> pdfContainerList = pdfVisitable.getPDFContainerList();
		for (PDFContainer pdfContainer : pdfContainerList) {
			HtmlContainer htmlContainer = pdfContainer.getHtmlContainer();
			if (htmlContainer != null) {
				searchContainer(htmlContainer);
			}
		}
	}

	/**
	 * Turns PDF to SVG and searches that.
	 * 
	 * @param pdfVisitable
	 */
	protected void createAndSearchSVGContainer(PDFVisitable pdfVisitable) {
		List<PDFContainer> pdfContainerList = pdfVisitable.getPDFContainerList();
		for (PDFContainer pdfContainer : pdfContainerList) {
			List<SVGContainer> svgContainerList = pdfContainer.getSVGListContainer();
			if (svgContainerList != null) {
				searchContainer(svgContainerList);
			}
		}
	}

	protected void searchContainer(List<SVGContainer> svgContainerList) {throw new RuntimeException("Must override");}

	// =============== RESULTS =======================
	
	protected void ensureResultsElement() {
		if (resultsElement == null) {
			resultsElement = new ResultsListElement();
		}
	}

	public ResultsListElement getResultsElement() {
		ensureResultsElement();
		return resultsElement;
	}

	// OUTPUT ======================================

	public VisitorOutput getOrCreateVisitorOutput() {
		if (visitorOutput == null) {
			visitorOutput = new VisitorOutput();
		}
		return visitorOutput;
	}

	public void setVisitorOutput(VisitorOutput visitorOutput) {
		this.visitorOutput = visitorOutput;
	}

	private void visitVistablesAndWriteOutputFiles() {
		for (VisitableInput visitableInput : visitableInputList) {
			List<AbstractVisitable> inputVisitableList = visitableInput.getVisitableList();
			if (inputVisitableList.size() == 0) {
				LOG.error("No visitable input list");
			} else {
				LOG.debug("InputVisitables " + inputVisitableList.size());
				for (AbstractVisitable visitable : inputVisitableList) {
					LOG.trace("input file List "+ visitable.getFileList().size());
					visit(visitable);
					createAndWriteOutputFiles();
				}
			}
		}
	}

	public File getResultsFile() {
		return resultsFile;
	}
	
	private void createAndWriteOutputFiles() {
		File outputDir = getOrCreateVisitorOutput().getOutputDirectoryFile();
		resultsFile = new File(outputDir, RESULTS_XML);
		List<File> files = currentVisitable.getFileList();
		if (resultsElement == null) {
			LOG.error("***WARNING results element is null");
		} else if (files.size() <= 1) {  // why not <= 0??
			writeFile(resultsElement.toXML(), resultsFile);
		} else {
			LOG.error("visitableOutput fileList "+files.size());
			for (File file : files) {
				LOG.trace("making directory from file: " + file);
				File ff = writeFile(outputDir, "about.txt", "created");
				if (resultsElement == null) {
					LOG.error("no results to write");
				} else {
					writeFile(outputDir, file.toString(), resultsElement.toXML());
					LOG.trace("ACTUALLY writing to: " + resultsFile);
				}
			}
		}
	}

	private File writeFile(File newDir, String filename, String text) {
		File file = new File(newDir, filename);
		writeFile(text, file);
		return file;
	}

	private void writeFile(String text, File ff) {
		try {
			FileUtils.writeStringToFile(ff, text);
			LOG.debug("WROTE "+ff);
		} catch (IOException e) {
			LOG.error("Cannot create file: " + ff);
		}
	}

	/** Visitor-specific search tool.
	 * 
	 * @return
	 */
	protected abstract AbstractSearcher createSearcher();
	
	public void setTaggers(List<String> taggerNames) {
		this.taggerNames = taggerNames;
	}
	
	public List<String> getTaggerNames() {
		return taggerNames;
	}

//	/** replace by pretagged documents.
//	 * 
//	 * @return
//	 */
//	public DocumentTagger getJournalTagger() {
//		DocumentTagger tagger = null;
//		if (taggerNames != null) {
//			for (String taggerName : taggerNames) {
//				if (BMCTagger.getTaggerName().equals(taggerName)) {
//					tagger = new BMCTagger();
//					break;
//				}
//				if (PLOSONETagger.getTaggerName().equals(taggerName)) {
//					tagger = new PLOSONETagger();
//					break;
//				}
//			}
//			if (tagger == null) {
//				LOG.error("cannot find tagger for any of: "+taggerNames);
//			}
//		}
//		return tagger;
//	}
	

	// ==============================================
}
