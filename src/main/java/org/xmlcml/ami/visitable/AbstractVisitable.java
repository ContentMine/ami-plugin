package org.xmlcml.ami.visitable;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.XPathProcessor;

/** 
 * Superclass of SVG2XML visitables.
 * 
 * Typical examples are XHTML, PNG, SVG, Table
 * 
 * @author pm286
 */
public abstract class AbstractVisitable {

	private final static Logger LOG = Logger.getLogger(AbstractVisitable.class);
	
	private File topDirectory;
	private boolean recursiveVisit = false;
	protected List<File> fileList;
	protected URL url;
	protected int sleepTime = 4000; // includes other time so ca 5000
	protected AbstractVisitor visitor; // not sure whether we need to transfer reference
//	protected DocumentTagger tagger;
	protected XPathProcessor xPathProcessor;

	protected AbstractVisitable() {
		
	}
	
	/** 
	 * Default is for immediate visitation.
	 * 
	 * @param visitor
	 */
	public void accept(AbstractVisitor visitor) {
		setVisitorProperties(visitor);
		visitor.visit(this);
	}
	
	void setVisitorProperties(AbstractVisitor visitor) {
//		this.setTagger(visitor.getJournalTagger());
		this.setXPathProcessor(visitor.getXPathProcessor());
	}

//	private void setTagger(DocumentTagger tagger) {
//		this.tagger = tagger;
//	}
//	
//	public DocumentTagger getTagger() {
//		return tagger;
//	}

	public void setXPathProcessor(XPathProcessor xPathProcessor) {
		this.xPathProcessor = xPathProcessor;
	}
	
	public XPathProcessor getXPathProcessor() {
		return xPathProcessor;
	}

    public List<File> findFilesInDirectories() {
    	if (topDirectory != null) {
    		LOG.trace("Using Visitable :" + getClass().getName());
    		Collection<File> files = FileUtils.listFiles(topDirectory, getExtensions(), recursiveVisit);

    		fileList = new ArrayList<File>(files);
    		Collections.sort(fileList);
    		
    		for (File file : fileList) {
    			LOG.trace(file.getAbsolutePath());
    		}
    	}
    	return fileList;
    }

    /** 
     * A list of allowed extensions
     * <p>
     * No dot (e.g. "htm", "html")
     * <p>
     * See Apache.FileUtils.listFiles
     * <p>
     * Returning null will result in visits to ALL files, empty String[] none
     * 
     * @return
     */
    public abstract String[] getExtensions();

	// ==========================================
	
    public AbstractVisitor getVistor() {
    	return visitor;
    }
    
	public File getTopDirectory() {
		return topDirectory;
	}

	public boolean isRecursiveVisit() {
		return recursiveVisit;
	}

	public void setRecursiveVisit(boolean recursiveVisit) {
		this.recursiveVisit = recursiveVisit;
	}

	public void setTopDirectory(File topDir) {
		 topDirectory = topDir;
		 LOG.trace("topDirectory set to :" + topDirectory);
    }
	
	/** every visitable should provide a way to add metadata if possible.
	 * 
	 * This may not be the same as MetadataVisitor, though this is inexorably muddled.
	 */
	public abstract void getMetadata();

	/** add File to be searched.
	 * 
	 * @param file could be file or directory
	 * 
	 * @throws Exception
	 */
	public abstract void addFile(File file) throws Exception;

	/** add URL to be searched.
	 * 
	 * @param url
	 * @throws Exception
	 */
	public abstract void downloadParseAndAddURL(URL url) throws Exception;

	public List<File> getFileList() {
		ensureFileList();
		return fileList;
	}

	protected void ensureFileList() {
		if (fileList == null) {
			this.fileList = new ArrayList<File>();
		}
	}

	protected void checkFile(File file) {
		if (file == null) {
			throw new RuntimeException("Null file in: "+this.getClass());
		}
		if (!file.exists()) {
			throw new RuntimeException("File does not exist: "+file.getAbsolutePath());
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append((url != null) ? "url: "+url+"\n" : "");
		if (fileList != null && fileList.size() > 0) {
			sb.append((topDirectory != null ? "top directory: "+topDirectory+"; " : "") +"files\n");
			if (fileList != null) {
				for (File file : fileList) {
					sb.append("file: "+file+"\n");
				}
			}
		}
		return sb.toString();
	}

	public void addURL(URL url) throws Exception {
		this.url = url;
		addDelayForCourtesy();
	}

	private void addDelayForCourtesy() throws InterruptedException {
		Thread.sleep(sleepTime);
	}

//	protected void addTags(Element htmlElement) {
//		if (tagger != null) {
//			LOG.trace("tagging with "+tagger);
//			tagger.addTagsToSections(htmlElement, InputType.HTML);
//			List<Element> taggedElements = XMLUtil.getQueryElements(htmlElement, "//*[@tag]");
//			LOG.trace("added tags: "+taggedElements.size());
//		}
//	}

}
