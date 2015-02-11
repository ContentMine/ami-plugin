package org.xmlcml.ami.visitor.metadata;

import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.image.ImageVisitable;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;

/** manages metadata in visitables.
 * 
 * mught be run automatically or might be user called
 * 
 * not fully worked out...
 * 
 * @author pm286
 *
 */
public class MetadataVisitor extends AbstractVisitor {

	// ===================Called on Visitables===================

	
	@Override
	public void visit(HtmlVisitable htmlVisitable) {
		doVisit(htmlVisitable);
	}

	@Override
	public void visit(ImageVisitable imageVisitable) {
		doVisit(imageVisitable);
	}

	@Override
	public void visit(SVGVisitable svgVisitable) {
		svgVisitable.getMetadata();
	}
	
	// =======================Called by Visitables===============
	
	@Override
	protected AbstractSearcher createSearcher() {
		return new MetadataSearcher(this);
	}

	@Override
	public String getDescription() {
		return "Extracts metadata.";
	}

	
	
}
