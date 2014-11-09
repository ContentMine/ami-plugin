package org.xmlcml.xhtml2stm.visitable;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.xhtml2stm.Fixtures;
import org.xmlcml.xhtml2stm.visitable.html.HtmlVisitable;

public class VisitableTest {

	@Test
	@Ignore
	public void testRecursiveSearch() throws Exception {
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.setTopDirectory(Fixtures.TEST_DIRECTORIES_DIR);
		htmlVisitable.setRecursiveVisit(true);
		List<File> htmlFiles = htmlVisitable.findFilesInDirectories();
		Assert.assertEquals("html files", 22, htmlFiles.size());
	}

}
