package org.xmlcml.ami.visitor.metadata;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitor.metadata.MetadataVisitor;

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
