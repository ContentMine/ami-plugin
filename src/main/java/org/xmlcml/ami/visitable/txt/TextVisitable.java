package org.xmlcml.ami.visitable.txt;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlcml.ami.util.AMIUtil;
import org.xmlcml.ami.visitable.AbstractVisitable;

public class TextVisitable extends AbstractVisitable  {

	private final static Logger LOG = Logger.getLogger(TextVisitable.class);

	private List<TextContainer> textContainerList;

	public TextVisitable() {
		
	}

	@Override
	public void addFile(File file) throws Exception {
		checkFile(file);
		ensureTextContainerList();
		ensureFileList();
		try {
			this.fileList.add(file);
			TextContainer textContainer = new TextContainer(file, FileUtils.readLines(file)); 
			textContainerList.add(textContainer);
		} catch (Throwable t) {
			LOG.error("Cannot parse document: "+file+" ("+t+") ");
		}
	}

	@Override
	public void downloadParseAndAddURL(URL url) throws Exception {
		super.addURL(url);
		ensureTextContainerList();
		try {
			TextContainer textContainer = new TextContainer(url, IOUtils.readLines(url.openStream())); 
			textContainerList.add(textContainer);
		} catch (Throwable t) {
			LOG.error("Cannot parse document: "+url+" ("+t+") ");
		}
	}

	private void ensureTextContainerList() {
		if (textContainerList == null) {
			textContainerList = new ArrayList<TextContainer>();
		}
	}

	public List<TextContainer> getTextContainerList() {
		ensureTextContainerList();
		if (super.findFilesInDirectories() != null) {
			textContainerList = createContainersFromFiles();
		} 
		return textContainerList;
	}

	public List<TextContainer> createContainersFromFiles() {
		textContainerList = new ArrayList<TextContainer>();
		if (fileList != null) {
			for (File file : fileList) {
				try {
					List<String> lineList = FileUtils.readLines(file);
					textContainerList.add(new TextContainer(file, lineList));
				} catch (Exception e) {
					LOG.error("file: "+file+"; "+file.exists());
					LOG.error("not a text file: "+file+ ": " + e);
				}
			}
		}
		return textContainerList;
		
	}

	@Override
	public String[] getExtensions() {
		return new String[] {"txt"};
	}

	public static boolean hasSuffix(String suffix) {
		return AMIUtil.XML.equalsIgnoreCase(suffix);
	}

	@Override
	public void getMetadata() {
		// null op
	}
	
}
