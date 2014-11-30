package org.xmlcml.ami.visitable.xml;

import java.io.File;
import java.net.URL;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.ami.visitable.VisitableContainer;

public class XMLContainer extends VisitableContainer {

	private Element xmlElement;

	public XMLContainer(File file, Element xmlElement) {
		this.file = file;
		this.xmlElement = xmlElement;
	}

	public XMLContainer(URL url, Element xmlElement) {
		this.url = url;
		this.xmlElement = xmlElement;
	}

	@Override
	public Element getElement() {
		return xmlElement;
	}

	/**
	 * <article-id pub-id-type="doi">10.1371/journal.pone.0080753</article-id>
	 */
	@Override
	public String getDoi() {
		if (doi == null) {
			if (xmlElement != null) {
				Nodes dois = xmlElement.query(".//*[local-name()='article-id' and @pub-id-type='doi']");
				if (dois.size() > 0) {
					doi = dois.get(0).getValue();
				}
			}
		}
		return doi;
	}
	
	@Override
	public String getName() {
		return file == null ? null : file.getAbsolutePath();
	}

}
