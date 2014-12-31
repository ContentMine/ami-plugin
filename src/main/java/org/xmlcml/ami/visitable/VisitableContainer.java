package org.xmlcml.ami.visitable;

import java.io.File;
import java.net.URL;

import nu.xom.Element;

/**
 * Container for the Visitable.
 * 
 * Manages things like location, DOI, etc..
 * 
 * @author pm286
 */
public abstract class VisitableContainer {

	protected String doi;
	protected File file;
	protected URL url;

	protected VisitableContainer() {
		// dummy at present 
	}
	public abstract String getDoi();

	
	public File getFile() {
		return file;
	}

	public abstract String getName();

	public Element getElement() {
		return null;
	}

	public URL getURL() {
		return url;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Doi: "+doi+"; ");
		sb.append("File: "+(file == null ? "NULL" : file.getAbsolutePath())+"; ");
		sb.append("URL: "+url+"\n");
		return sb.toString();
	}
	
//	public void setTagger(ContentTagger tagger) {
//		this.tagger = tagger;
//	}
//	
//	public ContentTagger getTagger() {
//		return this.tagger;
//	}
}
