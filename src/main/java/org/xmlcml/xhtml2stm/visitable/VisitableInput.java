package org.xmlcml.xhtml2stm.visitable;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.util.AMIUtil;
import org.xmlcml.xhtml2stm.visitable.html.HtmlVisitable;
import org.xmlcml.xhtml2stm.visitable.image.ImageVisitable;
import org.xmlcml.xhtml2stm.visitable.pdf.PDFVisitable;
import org.xmlcml.xhtml2stm.visitable.svg.SVGVisitable;
import org.xmlcml.xhtml2stm.visitable.xml.XMLVisitable;

public class VisitableInput {

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
	private final static HtmlVisitable HTML_VISITABLE = new HtmlVisitable();
	private final static ImageVisitable IMAGE_VISITABLE = new ImageVisitable();
	private final static AbstractVisitable XML_VISITABLE = new XMLVisitable();
	private final static PDFVisitable PDF_VISITABLE = new PDFVisitable();
	private final static SVGVisitable SVG_VISITABLE = new SVGVisitable();
	private File inputFile;
	private boolean isUrl;

	public VisitableInput(String arg) {
		this.inputArg = arg;
		//createVisitableList();
	}
	
	public List<AbstractVisitable> createVisitableList() {
		String inputItem = inputArg;
		inputFilenameExtension = FilenameUtils.getExtension(inputItem);
		isDirectory = AMIUtil.endsWithSeparator(inputItem) || new File(inputItem).isDirectory() 
				|| inputFilenameExtension == null || "".equals(inputItem)  ;
		isUrl = inputItem.startsWith(HTTP);
		ensureVisitableList();
		if (isDirectory) {
			addFilesToVisitableList(inputItem);
		} else if (isUrl){
			//FIXME awful kludge
			//if ("".equals(filenameExtension)) {
			inputFilenameExtension = "htm";
			//}
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
			//FIXME will have to tidy HTML here
			AbstractVisitable visitable = createVisitable(url);
			if (visitable != null) {
				visitableList.add(visitable);
			}
		} catch (Exception e) {
			LOG.error(e);
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
			LOG.error(e);
			throw new RuntimeException("Cannot read file: "+inputFile, e);
		}
	}

	private void ensureVisitableList() {
		if (visitableList == null) {
			visitableList = new ArrayList<AbstractVisitable>();
		}
	}

	public AbstractVisitable createVisitable(String inputFile) throws Exception {
		AbstractVisitable visitable = getVisitableFromFileExtension(inputFile);
		if (visitable != null) {
			File file = new File(inputFile);
			if (!file.exists()) {
				throw new RuntimeException("input does not exist: "+file.getAbsolutePath());
			}
			visitable.addFile(file);
		}
		return visitable;
	}

	public AbstractVisitable createVisitable(URL url) throws Exception {
		AbstractVisitable visitable = getVisitableFromExtension(inputFilenameExtension);
		if (visitable != null) {
			visitable.addURL(url);
		}
		return visitable;
	}

	private static AbstractVisitable getVisitableFromFileExtension(String inputFile) {
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
		}
		return visitable;
	}
	
	private static AbstractVisitable getVisitableFromExtension(String extension) {
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
		sb.append("extensions: "+Arrays.asList(extensions)+"; ");
		sb.append("isDirectory: "+isDirectory+"; ");
		sb.append("recursive: "+recursive+"; ");
		return sb.toString();
	}
	
}
