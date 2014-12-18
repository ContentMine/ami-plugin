package org.xmlcml.ami.visitable;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.xmlcml.ami.util.AMIUtil;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.image.ImageVisitable;
import org.xmlcml.ami.visitable.pdf.PDFVisitable;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitable.txt.TextVisitable;
import org.xmlcml.ami.visitable.xml.XMLVisitable;
import org.xmlcml.ami.visitor.AbstractVisitor;

public class VisitableInput {

	private static final String HTM = "htm";
	private static final String XML = "xml";

	private static final Logger LOG = Logger.getLogger(VisitableInput.class);
	
	private static final String HTTP = "http://";
	private static final String DOI  = "doi:";
	private static final String PMID = "pmid:";
	
	private List<AbstractVisitable> visitableList;
	private String inputArg;
	private String inputFilenameExtension;
	private String[] extensions;
	private boolean isDirectory;
	private boolean recursive;

	// messy, but seems the only way to use getExtensions statically
	private final static AbstractVisitable HTML_VISITABLE = new HtmlVisitable();
	private final static ImageVisitable IMAGE_VISITABLE = new ImageVisitable();
	private final static AbstractVisitable XML_VISITABLE = new XMLVisitable();
	private final static PDFVisitable PDF_VISITABLE = new PDFVisitable();
	private final static SVGVisitable SVG_VISITABLE = new SVGVisitable();
	private final static TextVisitable TXT_VISITABLE = new TextVisitable();
	private File inputFile;
	private boolean isUrl;
	private AbstractVisitor visitor;

	public VisitableInput(String arg) {
		this.inputArg = arg;
	}
	
	public List<AbstractVisitable> createVisitableList(AbstractVisitor visitor) {
		this.visitor = visitor;
		String inputItem = inputArg;
		inputFilenameExtension = FilenameUtils.getExtension(inputItem);
		isDirectory = AMIUtil.endsWithSeparator(inputItem) || new File(inputItem).isDirectory() 
				|| inputFilenameExtension == null || "".equals(inputItem)  ;
		isUrl = inputItem.startsWith(HTTP);
		ensureVisitableList();
		if (isDirectory) {
			addFilesToVisitableList(inputItem);
		} else if (isUrl){
			//FIXME awful kludge // but how do we know it's HTML? maybe force extension
			if (!(XML.equals(inputFilenameExtension))) { 	
				inputFilenameExtension = HTM;
			}
			addURLToVisitableList(inputItem);
		} else {
			addFileToVisitableList(inputItem);
		}
		return visitableList;
	}

	private void addFilesToVisitableList(String inputDirectory) {
		LOG.debug("input directory: "+inputDirectory);
		Iterator<File> fileIterator = FileUtils.iterateFiles(new File(inputDirectory), extensions, recursive);
		while (fileIterator.hasNext()) {
			addFileToVisitableList(fileIterator.next().toString());
		}
	}

	private void addURLToVisitableList(String urlString) {
		try {
			URL url = new URL(urlString);
			LOG.debug("downloading "+url);
			AbstractVisitable visitable = downloadAndCreateVisitable(url, visitor);
			if (visitable != null) {
				visitableList.add(visitable);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("FAILED "+e);
			throw new RuntimeException("Cannot read url: "+urlString, e);
		}
	}

	private void addFileToVisitableList(String inputFile) {
		try {
			AbstractVisitable visitable = createVisitable(inputFile);
			if (visitable != null) {
				visitableList.add(visitable);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("add file "+e);
			throw new RuntimeException("Cannot read file: "+inputFile, e);
		}
		LOG.debug("visitableList "+visitableList.size());
	}

	private void ensureVisitableList() {
		if (visitableList == null) {
			visitableList = new ArrayList<AbstractVisitable>();
		}
	}

	public AbstractVisitable createVisitable(String inputFile) throws Exception {
		AbstractVisitable visitable = getVisitableFromFileExtension(inputFile, visitor);
		if (visitable != null) {
			File file = new File(inputFile);
			if (!file.exists()) {
				throw new RuntimeException("input does not exist: "+file.getAbsolutePath());
			}
			visitable.addFile(file);
		}
		return visitable;
	}

	public AbstractVisitable downloadAndCreateVisitable(URL url, AbstractVisitor visitor) throws Exception {
		AbstractVisitable visitable = createNewSubclassedVisitableFromExtension(inputFilenameExtension);
		if (visitable != null) {
			visitable.setVisitorProperties(visitor);
//			visitable.accept(visitor);
			visitable.downloadParseAndAddURL(url);
		}
		return visitable;
	}

	private static AbstractVisitable getVisitableFromFileExtension(String inputFile, AbstractVisitor visitor) {
		AbstractVisitable visitable = null;
		if (FilenameUtils.isExtension(inputFile, HTML_VISITABLE.getExtensions())) {
			visitable = new HtmlVisitable();
		} else if (FilenameUtils.isExtension(inputFile, IMAGE_VISITABLE.getExtensions())) {
			visitable = new ImageVisitable();
		} else if (FilenameUtils.isExtension(inputFile, XML_VISITABLE.getExtensions())) {
			visitable = new XMLVisitable();
		} else if (FilenameUtils.isExtension(inputFile, PDF_VISITABLE.getExtensions())) {
			visitable = new PDFVisitable();
		} else if (FilenameUtils.isExtension(inputFile, SVG_VISITABLE.getExtensions())) {
			visitable = new SVGVisitable();
		} else if (FilenameUtils.isExtension(inputFile, TXT_VISITABLE.getExtensions())) {
			visitable = new TextVisitable();
		}
//		visitable.accept(visitor);
		visitable.setVisitorProperties(visitor);
		return visitable;
	}
	
	private static AbstractVisitable createNewSubclassedVisitableFromExtension(String extension) {
		AbstractVisitable visitable = null;
		if (isExtensionIn(extension, HTML_VISITABLE.getExtensions())) {
			visitable = new HtmlVisitable();
		} else if (isExtensionIn(extension, IMAGE_VISITABLE.getExtensions())) {
			visitable = new ImageVisitable();
		} else if (isExtensionIn(extension,  XML_VISITABLE.getExtensions())) {
			visitable = new XMLVisitable();
		} else if (isExtensionIn(extension, PDF_VISITABLE.getExtensions())) {
			visitable = new PDFVisitable();
		} else if (isExtensionIn(extension, SVG_VISITABLE.getExtensions())) {
			visitable = new SVGVisitable();
		}
		return visitable;
	}
	
	private static boolean isExtensionIn(String extension, String[] extensions) {
		if (extension != null && extensions != null) {
			for (String ext : extensions) {
				if (extension.equalsIgnoreCase(ext)) {
					return true;
				}
			}
		}
		return false;
	}

	public List<AbstractVisitable> getVisitableList() {
		ensureVisitableList();
		return visitableList;
	}

	/** 
	 * @return the precise inputString
	 */
	public String getInputArg() {
		return inputArg;
	}
	
	public File getInputFile() {
		if (inputFile == null) {
			File file = new File(inputArg);
			if (file.exists()) {
				inputFile = file;
			}
		}
		return inputFile;
	}
	
	public String getBaseName() {
		getInputFile();
		return (inputFile == null) ? null : FilenameUtils.getBaseName(inputFile.toString());
	}

	public void setExtensions(List<String> extensions) {
		this.extensions = extensions.toArray(new String[0]);
	}

	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
//		sb.append("visitableList: "+visitableList+"\n");
		sb.append("inputArg: "+inputArg+"; ");
		sb.append("extension: "+inputFilenameExtension+"; ");
		if (extensions != null) sb.append("extensions: "+Arrays.asList(extensions)+"; ");
		sb.append("isDirectory: "+isDirectory+"; ");
		sb.append("recursive: "+recursive+"; ");
		return sb.toString();
	}
	
}
