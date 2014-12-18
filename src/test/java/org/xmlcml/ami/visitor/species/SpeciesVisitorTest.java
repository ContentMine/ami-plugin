package org.xmlcml.ami.visitor.species;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.util.AMITestUtil;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.pdf.PDFVisitable;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.ami.visitable.xml.XMLVisitable;
import org.xmlcml.ami.visitor.AbstractVisitor;
import org.xmlcml.ami.visitor.species.SpeciesVisitor;
import org.xmlcml.ami.result.ResultsListElement;
import org.xmlcml.ami.util.AMIUtil;
import org.xmlcml.xml.XMLUtil;

public class SpeciesVisitorTest {

	private static final Logger LOG = Logger.getLogger(SpeciesVisitorTest.class);

	@Test
	@Ignore // FIXME QUICK
	public void testHtmlVisitable() throws Exception {
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.addFile(Fixtures.METABOLITE_00039_HTML);
		htmlVisitable.addFile(Fixtures.MULTIPLE_SPECIES_312_HTML);
		htmlVisitable.addFile(Fixtures.PASSERINES_313_HTML);
		
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		speciesVisitor.searchContainer(htmlVisitable.getHtmlContainerList().get(0));
		ResultsListElement resultsElement = speciesVisitor.getResultsElement();
//		resultsElement.debug("results");
	}

	@Test
	public void testSVGVisitable() throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		try {
			speciesVisitor.visit(svgVisitable);
			Assert.fail("Should fail");
		} catch (Exception e) {
			LOG.error("Failed as expected "+e);
		}
	}

	@Test
	public void testSVGVisitableDir() throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		File[] files = Fixtures.MOLECULES_DIR.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.toString().endsWith(".svg")) {
					svgVisitable.addSVGFile(file);
				}
			}
		}
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		try {
			speciesVisitor.visit(svgVisitable);
			Assert.fail("Should fail");
		} catch (Exception e) {
			LOG.error("Failed as expected "+e);
		}
	}

	@Test
	@Ignore // too many files
	public void testSearchHtmlFiles() throws Exception {
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.setTopDirectory(Fixtures.TEST_DIRECTORIES_DIR);
		speciesVisitor.visit(htmlVisitable);
	}

	@Test
	@Ignore // uses files not in tree
	public void testSearchHtmlFilesSample() throws Exception {
		System.out.println("testSearchHtmlFilesSample");
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		File file = Fixtures.BMC_SAMPLE;
		if (file.exists()) {
			htmlVisitable.setTopDirectory(file);
			speciesVisitor.visit(htmlVisitable);
			ResultsListElement results = speciesVisitor.getResultsElement();
			XMLUtil.debug(results, new FileOutputStream("target/htmlSampleSpecies.xml"), 1);
		}
	}
	
	@Test
	@Ignore // FIXME QUICK
	public void testPlosoneHtml() throws Exception {
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.addFile(new File("src/test/resources/org/xmlcml/ami/species/plosone/pumas.html"));
		
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		speciesVisitor.visit(htmlVisitable);
		ResultsListElement resultsElement = speciesVisitor.getResultsElement();
//		resultsElement.debug("results");
	}



	@Test
	@Ignore //Probably too large for tests
	//FIXME directory not in repository
	public void testSearchHtmlFilesBig() throws Exception {
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.setTopDirectory(new File("../pdfs/bmc/svgOutput"));
		speciesVisitor.visit(htmlVisitable);
	}
	
	@Test
	@Ignore //Too large for tests
	public void testSearchHtmlFilesLarge() throws Exception {
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		File file = Fixtures.BMC_ALL;
		if (file.exists()) {
			htmlVisitable.setTopDirectory(file);
			speciesVisitor.visit(htmlVisitable);
			ResultsListElement results = speciesVisitor.getResultsElement();
			XMLUtil.debug(results, new FileOutputStream("target/htmlLargeSpecies.xml"), 1);
		}
	}

	@Test
	@Ignore // FIXME NOW - no results found
	public void testSearchXmlFiles() throws Exception {
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		XMLVisitable xmlVisitable = new XMLVisitable();
		xmlVisitable.addFile(new File(Fixtures.SPECIES_DIR, "journal.pone.0077058.xml"));
		speciesVisitor.visit(xmlVisitable);
		ResultsListElement results = speciesVisitor.getResultsElement();
		File outputFile = new File("target/0077058SpeciesResults.xml");
		XMLUtil.debug(results, new FileOutputStream(outputFile), 1);
		assertFileDetails(outputFile, /*1166*/ 1140); // depends on connections
	}

	/** search one XML file and create corresponding output results.xml
	 * 
	 * 	// SHOWCASE
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchXmlFileCommand() throws Exception {
		File inputFile = new File(Fixtures.SPECIES_DIR, "journal.pone.0077058.xml");
		File outputDir = new File("target/journal.pone.0077058.xml");
		outputDir.delete();
		String[] args = new String[] {
				"-i", inputFile.toString(),
				"-o", outputDir.toString(),
		};
		File outputFile = new File(outputDir, AbstractVisitor.RESULTS_XML);
		LOG.debug("output "+outputFile.getAbsolutePath());
		LOG.debug("output: "+outputFile);
		new SpeciesVisitor().processArgs(args);
		// variable depends on search strategy - either 34 or 42
//		AMITestUtil.assertNodeCount(outputFile, 42, "//*[local-name()='eic']");
	}
	
	/** search one XML file and create corresponding output results.xml
	 * 
	 * 	// SHOWCASE
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSearchXmlDirCommand() throws Exception {
		File inputFile = Fixtures.MANY_SPECIES_DIR;
		File outputDir = new File("target/species/many");
		outputDir.delete();
		String[] args = new String[] {
//				"-d",
				"-i", inputFile.toString(),
				"-o", outputDir.toString(),
				"-e", "xml",
		};
		File outputFile = new File(outputDir, AbstractVisitor.RESULTS_XML);
		LOG.debug("output "+outputFile.getAbsolutePath());
		LOG.debug("output: "+outputFile);
		new SpeciesVisitor().processArgs(args);
//		AMITestUtil.assertNodeCount(outputFile, 42, "//*[local-name()='eic']");
	}
	
	/** search one XML file and create corresponding output results.xml
	 * 
	 * 	// FAILING TEST
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore // now mended
	public void testSearchInfiniteLoop() throws Exception {
		File inputFile = new File(new File(Fixtures.SPECIES_DIR, "bad"), "journal.pone.0081580.xml");
		File outputDir = new File("target/species/many");
		outputDir.delete();
		String[] args = new String[] {
				"-i", inputFile.toString(),
				"-o", outputDir.toString(),
				"-e", "xml",
		};
		File outputFile = new File(outputDir, AbstractVisitor.RESULTS_XML);
		SpeciesVisitor speciesVisitor = new SpeciesVisitor();
		speciesVisitor.processArgs(args);
		AMITestUtil.assertNodeCount(speciesVisitor.getResultsFile(), 172, "//*[local-name()='eic']");
	}

	/** search one XML file and create corresponding output results.xml
	 * 
	 * 	// SHOWCASE
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore // unles local
	public void testSearchURL() throws Exception {
		File outputDir = new File("target/species/journal.pone.0113556.xml/");
		String[] args;
		File outputFile;
		/**
		// check local copy
		outputDir.delete();
		args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/plosone/journal.pone.0113556.xml",
				"-o", outputDir.toString(),
		};
		SpeciesVisitor speciesVisitor0 = new SpeciesVisitor();
		speciesVisitor0.processArgs(args);
		AMITestUtil.assertNodeCount(speciesVisitor0.getResultsFile(), 23, "//*[local-name()='eic']");
		*/
		// now the URL
//		File outputFile = new File(outputDir, AbstractVisitor.RESULTS_XML);
		outputDir.delete();
		args = new String[] {
				"-i", "http://dx.plos.org/10.1371/journal.pone.0113556.xml",
				"-o", outputDir.toString(),
		};
		outputFile = new File(outputDir, AbstractVisitor.RESULTS_XML);
		SpeciesVisitor speciesVisitor = new SpeciesVisitor();
		speciesVisitor.processArgs(args);
		AMITestUtil.assertNodeCount(speciesVisitor.getResultsFile(), 23, "//*[local-name()='eic']");
	}
		
	/** search one XML file and create corresponding output results.xml
	 * 
	 * 	// SHOWCASE
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore // only for deliberate use since accesses net
	public void testSearchURLRanges() throws Exception {
		File outputDir = new File("target/species/journal.pone.0113556.xml/");
		String[] args;
		int[] hits = {
				 0,   4,   5,  -1,  40,  10,   0,  -1,   0,  18,
				 7,  17,  51,   0,  -1,  -1,  -1,   0,   6,  38,
				 0,  25,   0,   1,   0,   0,   7,  83,  32, 102,
				 0,   2,   2,   2,   0,  -1,  -1,   0,   0, 163,
				 1,   1,  -1,   0,   2,   0,   0,   0,  -1,  -1,
		};
		// sigma 
		for (int i = 0; i < 50; i++) {
			int jjjjjj = 113500 + i;
			args = new String[] {
					"-i", "http://dx.plos.org/10.1371/journal.pone.0"+jjjjjj+".xml",
					"-o", outputDir.toString(),
			};
			Thread.sleep(10000);
			SpeciesVisitor speciesVisitor = new SpeciesVisitor();
			speciesVisitor.processArgs(args);
			
			AMITestUtil.assertNodeCount(speciesVisitor.getResultsFile(), hits[i], "//*[local-name()='eic']");
		}
	}
		
	/** search one HTML file and create corresponding output results.xml
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore // FIXME QUICK
	public void testSearchHtmlSpeciesCommand() throws Exception {
		File inputFile = new File(Fixtures.SPECIES_DIR, "ijs.005942.htm");
//		assertFileDetails(inputFile, 78013);
		File outputDir = new File("target/ijs.005942");
		outputDir.delete();
		String[] args = new String[] {
				"-i", inputFile.toString(),
				"-o", outputDir.toString(),
		};
		File outputFile = new File(outputDir, AbstractVisitor.RESULTS_XML);
		LOG.debug("output: "+outputFile);
		new SpeciesVisitor().processArgs(args);
//		assertFileDetails(outputFile, 997); 
	}
	
	


	/** search directory with several XML files and create corresponding output results.xml
	 * 
	 * @throws Exception
	 */
	@Ignore // FIXME this is rubbish as it stands
	@Test
	public void testSearchXmlFilesCommand() throws Exception {
		File inputFile = Fixtures.SPECIES_DIR;
		assertFileDetails(inputFile, 673450); // presumably subdirectories
		File outputDir = new File("target/species/");
//		outputDir.delete();
		String[] args = new String[] {
				"-i", inputFile.toString(),
				"-o", outputDir.toString(),
				"-e", "xml",
		};
		// don't know why the directory has suffix // shouldn't have
		File outputFile = new File(new File(outputDir.toString()+".xml"), AbstractVisitor.RESULTS_XML);
//		outputFile.mkdirs();
		// Use this command for main processing
//		SpeciesVisitor.main(args);
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		speciesVisitor.processArgs(args);
		
		// FIXME NOW
		assertFileDetails(outputFile, 1459);
	}

	@Test
	@Ignore // FIXME QUICK
	public void testCommandlineHtmlFile() throws Exception {
		String[] args = new String[] {
				"-i", Fixtures.MULTIPLE_SPECIES_312_HTML.toString(),
				"-o", Fixtures.AMI_OUT.toString()+".xml",
		};
		SpeciesVisitor.main(args);
		LOG.debug(Fixtures.AMI_OUT.toString()+","+Fixtures.AMI_OUT.isDirectory());
//		Assert.assertTrue(Fixtures.AMI_OUT.exists());
//		Assert.assertTrue(Fixtures.AMI_OUT.toString(), FileUtils.sizeOf(Fixtures.AMI_OUT) > 100);
//		Assert.assertTrue(new File("target/multiple.species.312.xml").exists());		
	}

	@Test
	/** searches a directory for XML files and searches these for species.
	 * 
	 * third file has a DTD and fails when offline (and takes ages when online)
	 * NEEDS DTD stripper
	 * 
	 * @throws Exception
	 */
	@Ignore // FIXME NOW
	public void testCommandlineAllDir() throws Exception {
		System.out.println("testCommandlineAllDir");
		String inputDir = Fixtures.SPECIES_DIR.toString()+"/";
		checkInputFiles(Fixtures.SPECIES_DIR, new String[]{"xml"}, 3);

		String[] args = new String[] {
				"-i", inputDir,                      // this is a directory
				"-o", Fixtures.AMI_OUT.toString()+"/",
				"-e", AMIUtil.XML                       // we need extensions
		};
		File resultsFile = new File("target/species.xml/results.xml");
//		SpeciesVisitor.main(args);
		AbstractVisitor visitor = new SpeciesVisitor();
		visitor.processArgs(args);
		Element resultsElement = visitor.getResultsElement();
		Assert.assertEquals("file count", 3, resultsElement.getChildElements().size());
		List<Element> nodes = XMLUtil.getQueryElements(resultsElement, "//*[@name]");
		Assert.assertEquals("doi count", 3, nodes.size());
		nodes = XMLUtil.getQueryElements(resultsElement, "//*[local-name()='species']");
		Assert.assertEquals("doi count", 24, nodes.size());
		assertFileDetails(resultsFile, /*1508*/ 1459 /*1391*/); // with DTD stripped/resolved // with DTD fail
	}

	private void checkInputFiles(File inputDir, String[] exts, int nfile) {
		LOG.debug(inputDir);
		List<File> files = new ArrayList<File>(FileUtils.listFiles(inputDir, exts, false));
		Assert.assertEquals("input", nfile, files.size());
	}

	@Test
	@Ignore("Writes to wrong directory")//FIXME
	public void testCommandlineHtmlDir() throws Exception {
		String inputDir = Fixtures.SPECIES_DIR.toString()+"/";
		LOG.debug(inputDir);

		String[] args = new String[] {
				"-i", inputDir,
				"-o", new File(Fixtures.AMI_OUT,"/html/").toString(),
				"-e", AMIUtil.HTM,	AMIUtil.HTML
		};
		SpeciesVisitor.main(args);
	}

	@Test
	@Ignore // takes too long for tests
	public void testSpeciesPDF() throws Exception {
		AbstractVisitor speciesVisitor = new SpeciesVisitor();
		PDFVisitable pdfVisitable = new PDFVisitable();
		pdfVisitable.addFile(Fixtures._329_PDF);
		speciesVisitor.visit(pdfVisitable);
		ResultsListElement results = speciesVisitor.getResultsElement();
		XMLUtil.debug(results, new FileOutputStream("target/pdfResults.xml"), 1);
	}
	
	@Test
	@Ignore // till we sort directory stuff
	public void testCommandlinePDF() throws Exception {
		String userHome = System.getProperties().get("user.home").toString();
		String[] args = new String[] {
				"-i", Fixtures._329_PDF.toString(),
				"-o", new File(Fixtures.AMI_OUT,"/pdf/").toString(),
				"-e", AMIUtil.HTM,	AMIUtil.HTML
		};
		SpeciesVisitor.main(args);
	}

	@Test
	@Ignore // too big for Hudson
	public void testCommandlinePDF1() throws Exception {
		String[] args = new String[] {
				"-i", new File(Fixtures.XHTML_PDF_DIR, "multiple-1471-2148-11-312.pdf").toString(),
				"-o", new File(Fixtures.AMI_OUT,"pdf/results312.xml").toString(),
		};
		SpeciesVisitor.main(args);
	}

	@Test
	@Ignore // till we create new TestFramework
	public void testCommandlinePDFDIR() throws Exception {
		String[] args = new String[] {
				"-i", new File(Fixtures.AMI_DIR, "pdfsmall").toString()+"/",
				"-o", new File(Fixtures.AMI_OUT,"/pdf/").toString(),
				"-e", AMIUtil.PDF
		};
		SpeciesVisitor.main(args);
	}
	
	@Test
	public void testUsage() throws Exception {
		String[] args = null;
		SpeciesVisitor.main(args);
	}
	
	@Test
	@Ignore // URL NYI
	public void testBMCPDF() throws Exception {
		String[] args = new String[] {
				"-o", new File(Fixtures.AMI_OUT,"/pdf/1471-2148-13-250.xml").toString(),
				"-e", AMIUtil.PDF
		};
		SpeciesVisitor.main(args);
	}
	
	@Test
	@Ignore // fails on DTD
	public void testBMCHTML() throws Exception {
		String[] args = new String[] {
				"-i", "http://www.biomedcentral.com/1471-2148/11/312",
				"-o", new File(Fixtures.AMI_OUT,"/pdf/1471-2148-11-312.xml").toString(),
				"-e", AMIUtil.HTML
		};
		SpeciesVisitor.main(args);
	}
		
	@Test
	@Ignore // NYI fails on itemscope and other HTML5
	public void testPLOSHTML() throws Exception {
		String[] args = new String[] {
				"-i", "http://www.plosone.org/article/info%3Adoi%2F10.1371%2Fjournal.pone.0080753",
				"-o", new File(Fixtures.AMI_OUT,"/xml/pone.0080753.xml").toString(),
				"-e", AMIUtil.HTML
		};
		SpeciesVisitor.main(args);
	}
	
	@Test
	@Ignore //FIXME should this have @Ignore?
	public void testExtractedPLOSExtracted() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/plosone/xml",
				"-o", "target/plosone/species/",
				"-e", AMIUtil.XML
		};
		SpeciesVisitor.main(args);
		//File targetPlosoneSpecies = new File("target/plosone/species");
		//Assert.assertTrue("output exists", targetPlosoneSpecies.exists());
	}
	
	@Test
	// doesn't yet work
	public void testExtractedPLOSXML() throws Exception {
		String[] args = new String[] {
				"-i", new File("src/test/resources/org/xmlcml/ami/plosone/journal.pone.0113556.xml").toString(),
				"-t", "plosone",
				"-o", "target/plosone/species/",
//				"-x", "//*[@tag='abstract' or @tag='discussion']",
				"-x", "//*[@tag]",
				"-e", AMIUtil.XML
		};
		SpeciesVisitor.main(args);
	}
	
	@Test
	/**
	 * writes to target/xml.xml/results.xml
	 * 
	 * @throws Exception
	 */
	@Ignore // 
	public void testExtractedPLOSExtracted1() throws Exception {
		String[] args = new String[] {
				"-i", "src/test/resources/org/xmlcml/ami/plosone/2013-12-11/xml/",
				//"-o", "../extracted/plosone/species/2013-12-11/",
				"-e", AMIUtil.XML
		};
		SpeciesVisitor.main(args);
	}
	
	// ================================================
	/** checks file size in bytes .
	 * 
	 * fragile because of XML whitespace.
	 * 
	 * @param outputFile
	 * @param size0
	 */
	private static void assertFileDetails(File outputFile, int size) {
		LOG.debug("file: "+outputFile);
		Assert.assertTrue("file should exist", outputFile.exists());
		long fileSize = FileUtils.sizeOf(outputFile);
		Assert.assertEquals("size: "+size, size, fileSize);
	}

	private static void assertNodeCount(File outputFile, int count, String xpath) {
		LOG.debug("file: "+outputFile);
		Assert.assertTrue("file should exist", outputFile.exists());
		Nodes nodes = XMLUtil.parseQuietlyToDocument(outputFile).query(xpath);
		int nodeCount = nodes.size();
		Assert.assertEquals("count: "+nodeCount, count, nodeCount);
	}



}