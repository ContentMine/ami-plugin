package org.xmlcml.ami.visitable.html;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;

public class HtmlVisitableTest {
	@Test
	@Ignore
	public void testCreateHTMLElementList() throws Exception {
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.setTopDirectory(Fixtures.TEST_DIRECTORIES_DIR);
		htmlVisitable.setRecursiveVisit(true);
		List<File> htmlFiles = htmlVisitable.findFilesInDirectories();
		Assert.assertEquals("html files", 22, htmlFiles.size());
		List<HtmlContainer> htmlContainers = htmlVisitable.createContainersFromFiles();
		Assert.assertEquals("html containers", 22, htmlContainers.size());
	}

}
