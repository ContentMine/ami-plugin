package org.xmlcml.ami.visitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitor.words.WordSetWrapper;
import org.xmlcml.files.EuclidSource;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

public class SimpleVisitorTest {

	
	private static final int MIN_STEMMED_LENGTH = 2;
	private static final Logger LOG = Logger.getLogger(SimpleVisitorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static int MIN_COUNT = 4;
	
	@Test
	public void testHelp() throws Exception {
		String[] args = new String[] {
				"--help"
		};
		SimpleVisitor.main(args);
	}

	@Test
	@Ignore // FIXME -p should be parsed
	public void testCorrectSyntax() throws Exception {
		// DefaultArgProcessor args.xml
		parseCorrectParameters(new String[]{"-i", "foo"});
		parseCorrectParameters(new String[]{"-i", "foo", "bar"});
		parseCorrectParameters(new String[]{"-r", "true"});
		parseCorrectParameters(new String[]{"-o", "foo/bar"});
		parseCorrectParameters(new String[]{"-q", "foo/bar"});
		parseCorrectParameters(new String[]{"-e", "xml", "html"});
//		parseCorrectParameters(new String[]{"-h"});  // HELP works, but large output
		try {
			parseCorrectParameters(new String[]{"-i"});
			Assert.fail("should throw exception");
		} catch (Exception e) {
		}
		// AMIArgProcessor args.xml
		parseCorrectParameters(new String[]{"-y", "100"}); // context
		parseCorrectParameters(new String[]{"-y", "100", "120"});
		parseCorrectParameters(new String[]{"-p", "a", "b", "c"});
		parseCorrectParameters(new String[]{"-x", "/*[local-name()='bar]", "//h:div[@tag='abstract']"});
		parseCorrectParameters(new String[]{"--test"});
		parseCorrectParameters(new String[]{"--test", "a", "b", "c"});
	}

	@Test
	public void testArgSyntax() throws Exception {
		parseIncorrectParameters(new String[]{"-i"}, 
				"RuntimeException: cannot process argument: -i (IllegalArgumentException: argument count (0) is not compatible with {1,*})");
		parseIncorrectParameters(new String[]{"-p"}, 
				"RuntimeException: cannot process argument: -p (IllegalArgumentException: argument count (0) is not compatible with {1,*})");
		parseIncorrectParameters(new String[]{"-y", "100", "200", "300"}, 
				"RuntimeException: cannot process argument: -y (IllegalArgumentException: argument count (3) is not compatible with {1,2})");
		parseIncorrectParameters(new String[]{"-y", "100", "TWO", }, 
				"RuntimeException: cannot process argument: -y (NumberFormatException: For input string: \"TWO\")");
		parseIncorrectParameters(new String[]{"-r"}, 
				"RuntimeException: cannot process argument: -r (IllegalArgumentException: argument count (0) is not compatible with {1,1})");
		/** // FIXME
		parseIncorrectParameters(new String[]{"-r", "true", "false"}, 
				"RuntimeException: cannot process argument: -r (IllegalArgumentException: argument count (2) is not compatible with {1,1})");
// BOOLEANS NOT YET WORKING // FIXME
		parseIncorrectParameters(new String[]{"-r", "banana"}, 
				"dummy error");
				*/
	}

	@Test
	public void testReadInput() throws Exception {
		File file = new File(Fixtures.SECTIONS_DIR, "plosone/journal.pone.0112055.nodtd.xml");
		SimpleVisitor simpleVisitor = new SimpleVisitor();
		String[] args = null;
		args = new String[]{"-i", file.toString()};
		simpleVisitor.processArgs(args);
		File outputFile = analyzeOutput(new File("target/journal.pone.0112055.nodtd.xml/results.xml"));
		FileUtils.deleteQuietly(outputFile);
		Assert.assertTrue("deleted directory", !outputFile.exists());
		// fails silently (maybe change?)
		args = new String[]{"-i", new File(Fixtures.SECTIONS_DIR, "plosone/non-exist").toString()};
		simpleVisitor.processArgs(args);
		Assert.assertTrue("no new directory", !outputFile.exists());
	}

	@Test
	public void testReadInputAndWriteOutput() throws Exception {
		File file = new File(Fixtures.SECTIONS_DIR, "plosone/journal.pone.0112055.nodtd.xml");
		SimpleVisitor simpleVisitor = new SimpleVisitor();
		String[] args = null;
		args = new String[]{"-i", file.toString(), "-o", "target/foo/bar/"};
		simpleVisitor.processArgs(args);
		File outputFile = analyzeOutput(new File("target/journal.pone.0112055.nodtd.xml/results.xml"));
		FileUtils.deleteQuietly(outputFile);
		Assert.assertTrue("deleted directory", !outputFile.exists());
		// fails silently (maybe change?)
		args = new String[]{"-i", new File(Fixtures.SECTIONS_DIR, "plosone/non-exist").toString()};
		simpleVisitor.processArgs(args);
		Assert.assertTrue("no new directory", !outputFile.exists());
	}

	@Test
	public void testReadMultipleInput() throws Exception {
		File file = new File(Fixtures.SECTIONS_DIR, "plosone/journal.pone.0112055.nodtd.xml");
		SimpleVisitor simpleVisitor = new SimpleVisitor();
		String[] args = null;
		args = new String[]{"-i", file.toString()};
		simpleVisitor.processArgs(args);
		File outputFile = analyzeOutput(new File("target/journal.pone.0112055.nodtd.xml/results.xml"));
		FileUtils.deleteQuietly(outputFile);
		Assert.assertTrue("deleted directory", !outputFile.exists());
		// fails silently (maybe change?)
		args = new String[]{"-i", new File(Fixtures.SECTIONS_DIR, "plosone/non-exist").toString()};
		simpleVisitor.processArgs(args);
		Assert.assertTrue("no new directory", !outputFile.exists());
	}


	@Test
	public void testParams() throws Exception {
		File[] files = {
			new File(Fixtures.TEST_AMI_DIR, "fromNorma/journal.pone.0115884.norma/fulltext.html"),
			new File(Fixtures.MOLECULES_DIR, "metabolite00039.html"),
			new File(Fixtures.BMC_DIR, "15_1_511.html"),
			new File(Fixtures.SECTIONS_DIR, "plosone/journal.pone.0112055.nodtd.xml")
		};
		for (File file : files) {
			SimpleVisitor simpleVisitor = new SimpleVisitor();
			simpleVisitor.processArgs(new String[]{
					"-i", file.toString(),
					"--param", "wordLengths"});
			simpleVisitor.processArgs(new String[]{
					"-i", file.toString(),
					"--param", "wordFrequencies"});
		}
	}
	
	@Test
	public void testLuceneStemmming() throws Exception{
		WordAnalyzer testAnalyzer = new WordAnalyzer();
		WordSetWrapper stopwordSet = WordSetWrapper.getCommonEnglishStopwordSet();
		String fieldName = Version.LUCENE_4_10_3.toString();
		Document documentToBeAnalyzed = XMLUtil.parseQuietlyToDocument(
				new File("src/test/resources/org/xmlcml/ami/plosone/journal.pone.0115884/fromnorma.html"));
		WordSetWrapper wordSet = createWordSet(testAnalyzer, stopwordSet, documentToBeAnalyzed);
		
		Iterable<Multiset.Entry<String>> entriesSortedByCount = 
			Multisets.copyHighestCountFirst(wordSet.getMultiset()).entrySet();
		Iterable<Multiset.Entry<String>> sortedEntries = entriesSortedByCount;
		Iterable<Multiset.Entry<String>> entriesSortedByValue =
			ImmutableSortedMultiset.copyOf(wordSet.getMultiset()).entrySet();
//		Iterable<Multiset.Entry<String>> sortedEntries = entriesSortedByValue;
		
		Element lengthsElement = new Element("frequencies");
		for (Entry<String> entry : sortedEntries) {
			int count = +entry.getCount();
			if (count < MIN_COUNT) continue;
			Element lengthElement = new Element("frequency");
			lengthElement.addAttribute(new Attribute("word", ""+entry.getElement()));
			lengthElement.addAttribute(new Attribute("count", ""+count));
			lengthsElement.appendChild(lengthElement);
		}
		LOG.debug(lengthsElement.toXML());
		XMLUtil.debug(lengthsElement, new FileOutputStream("target/frequencyTest.xml"),1);

		testAnalyzer.close();
	}

	private static WordSetWrapper createWordSet(WordAnalyzer testAnalyzer,
			WordSetWrapper stopwordSet, Document documentToBeAnalyzed)
			throws IOException {
		TokenStreamComponents tokenStreamComponents = 
				testAnalyzer.createComponents(Version.LUCENE_4_10_3.toString(), new StringReader(documentToBeAnalyzed.getValue()));
		TokenStream tokenStream = tokenStreamComponents.getTokenStream();
		CharTermAttribute cattr = tokenStream.addAttribute(CharTermAttribute.class);
		tokenStream.reset();
		Multiset<String> multiset = HashMultiset.create(); 
		while (tokenStream.incrementToken()) {
			 String rawWord = cattr.toString();
			if (!stopwordSet.contains(rawWord) && rawWord.length() > MIN_STEMMED_LENGTH) { //remove stopwords and short strings
				multiset.add(rawWord);
			}
		}
		tokenStream.end();
		tokenStream.close();
		
		return new WordSetWrapper(multiset);
	}

	// ====================================================
	
	private File analyzeOutput(File outputFile) {
		Assert.assertTrue("made directory", outputFile.exists());
		Element results = XMLUtil.parseQuietlyToDocument(outputFile).getRootElement();
		return outputFile;
	}
	
	private void parseCorrectParameters(String[] args) {
		try {
			SimpleVisitor.main(args);
		} catch (Exception e) {
//			e.printStackTrace();
			throw new RuntimeException("failed parse", e);
		}
	}

	private static void parseIncorrectParameters(String[] args, String errorMessage) throws Exception {
		try {
			SimpleVisitor.main(args);
			Assert.fail("should throw Exception: "+errorMessage);
		} catch (RuntimeException e) {
			String errorMessage1 = ExceptionUtils.getRootCauseMessage(e);
			Assert.assertEquals("error", errorMessage, errorMessage1);
		}
	}
	
	@Test
	public void testArgs() throws Exception {
		String[] args = new String[] {
				"-i", new File(Fixtures.TEST_REGEX_DIR, "genbank0.xml").toString(),
				"-p", "wordLengths", 
				"-t1", "a", "b", "c",
				"-y", "10", "20",
				"-x", "//*",
				"-e", EuclidSource.XML
		};
		SimpleVisitor.main(args);
	}
	
	
}
class WordAnalyzer extends Analyzer {
	  @Override
	  protected TokenStreamComponents createComponents(String fieldName, java.io.Reader reader) {
	    Tokenizer source = new LowerCaseTokenizer(getVersion(), reader);
	    return new TokenStreamComponents(source, new PorterStemFilter(source));
	  }

}

