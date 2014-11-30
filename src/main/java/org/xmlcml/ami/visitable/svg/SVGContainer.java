package org.xmlcml.ami.visitable.svg;

import java.io.File;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.ami.visitable.VisitableContainer;

public class SVGContainer extends VisitableContainer {

	private Element svgElement;

	public SVGContainer(File file, Element svgElement) {
		this.file = file;
		this.svgElement = svgElement;
	}

	@Override
	public Element getElement() {
		return svgElement;
	}

	/**
	 * <article-id pub-id-type="doi">10.1371/journal.pone.0080753</article-id>
	 */
	@Override
	public String getDoi() {
		if (doi == null) {
			if (svgElement != null) {
				Nodes dois = svgElement.query(".//*[local-name()='article-id' and @pub-id-type='doi']");
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
