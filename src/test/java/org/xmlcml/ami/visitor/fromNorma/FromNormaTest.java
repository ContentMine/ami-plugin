package org.xmlcml.ami.visitor.fromNorma;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.AMIArgProcessor;
import org.xmlcml.ami.visitor.species.SpeciesVisitor;
import org.xmlcml.files.QuickscrapeDirectory;

public class FromNormaTest {

	
	private static final Logger LOG = Logger.getLogger(FromNormaTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	@Test
	public void testReadQuickscrape() throws Exception {
		File qd0115884temp = new File("target/plosone/qd0115884temp/");
		if (qd0115884temp.exists()) FileUtils.forceDelete(qd0115884temp);
		qd0115884temp.mkdirs();
		File qd0115884 = new File(Fixtures.FROM_NORMA_DIR, "journal.pone.0115884.norma");
		FileUtils.copyDirectory(qd0115884, qd0115884temp);
		String[] args = {
			"-q", qd0115884temp.toString(),    // output from norma
//			"-x", "nlm2html",                  // stylesheet to use (code)
			"-e", "html"                       // type of file to transform
		};
		SpeciesVisitor speciesVisitor = new SpeciesVisitor();
		speciesVisitor.processArgs(args);
		AMIArgProcessor argProcessor = speciesVisitor.getArgProcessor();
//		LOG.debug(argProcessor.createDebugString());
		List<QuickscrapeDirectory> quickscrapeDirectoryList = argProcessor.getQuickscrapeDirectoryList();
		Assert.assertNotNull(quickscrapeDirectoryList);
		Assert.assertEquals("QuickscrapeDirectory/s",  1,  quickscrapeDirectoryList.size());
		QuickscrapeDirectory quickscrapeDirectory = quickscrapeDirectoryList.get(0);
		List<File> files = quickscrapeDirectory.listFiles(true);
		for (File file : files) {
			LOG.debug(file);
		}
		Assert.assertEquals(6, files.size());
	}

}
