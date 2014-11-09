package org.xmlcml.xhtml2stm.visitor.metadata;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.xhtml2stm.Fixtures;
import org.xmlcml.xhtml2stm.visitable.svg.SVGVisitable;

public class MetadataVisitorTest {
	@Test
	@Ignore
	public void testRecursiveSearch() throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		svgVisitable.setTopDirectory(Fixtures.TEST_DIRECTORIES_DIR);
		svgVisitable.setRecursiveVisit(true);
		List<File> svgFiles = svgVisitable.findFilesInDirectories();
		Assert.assertEquals("svg files", 56, svgFiles.size());
	}
	
	@Test
	public void testMetadata() throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		svgVisitable.addSVGFile(Fixtures.TREE_GIBBONS_LARGE_SVG);
		MetadataVisitor metadataVisitor = new MetadataVisitor();
		metadataVisitor.visit(svgVisitable);
	}
	
	@Test
	public void testMetadataInFiles() throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		svgVisitable.setTopDirectory(Fixtures.TEST_DIRECTORIES_DIR);
		MetadataVisitor metadataVisitor = new MetadataVisitor();
		metadataVisitor.visit(svgVisitable);
	}
}
