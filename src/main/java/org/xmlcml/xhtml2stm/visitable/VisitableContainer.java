package org.xmlcml.xhtml2stm.visitable;

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
	
}
