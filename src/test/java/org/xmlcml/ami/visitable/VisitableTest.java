package org.xmlcml.ami.visitable;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitable.html.HtmlVisitable;

public class VisitableTest {

	@Test
	@Ignore
	public void testRecursiveSearch() throws Exception {
		AbstractVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.setTopDirectory(Fixtures.TEST_DIRECTORIES_DIR);
		htmlVisitable.setRecursiveVisit(true);
		List<File> htmlFiles = htmlVisitable.findFilesInDirectories();
		Assert.assertEquals("html files", 22, htmlFiles.size());
	}

}
