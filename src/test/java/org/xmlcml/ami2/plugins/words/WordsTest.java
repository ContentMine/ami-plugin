package org.xmlcml.ami2.plugins.words;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami2.Fixtures;
import org.xmlcml.ami2.plugins.AMIArgProcessor;

public class WordsTest {

	
	private static final String STOPWORDS_TXT = "/org/xmlcml/ami2/plugins/words/stopwords.txt";
	private static final String CLINICAL_STOPWORDS_TXT = "/org/xmlcml/ami2/plugins/words/clinicaltrials200.txt";
	private static final Logger LOG = Logger.getLogger(WordsTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String DATA_16_1_1 = 
			"trialsdata/http_www.trialsjournal.com_content_16_1_1";
	private static final String TEMP_16_1_1 = 
			"trialstemp/http_www.trialsjournal.com_content_16_1_1";

	private static final String EXAMPLES = 
			"examples";
	private static final String EXAMPLES_TEMP = 
			"examplestemp";

	@Test
	public void testWordsHelp() {
		String[] args = {};
		new WordArgProcessor(args);
	}

	@Test
	public void testWords() {
		String[] args = {
			"-q", Fixtures.TEST_PLOSONE_0115884.toString(),
			"--w.words", WordArgProcessor.WORD_LENGTHS, WordArgProcessor.WORD_FREQUENCIES,
			"--w.stopwords", STOPWORDS_TXT,
			"--w.wordlengths", "2", "12",
			"--w.wordtypes", "acronym", "GROT",
		};
		new WordArgProcessor(args);
	}
	
	@Test
	public void testWordsRun() {
		String[] args = {
			"-q", Fixtures.TEST_PLOSONE_0115884.toString(),
			"--w.words", /*WordArgProcessor.WORD_LENGTHS, */WordArgProcessor.WORD_FREQUENCIES,
			"--w.stopwords", STOPWORDS_TXT,
			"--w.wordlengths", "2", "12",
			"--w.wordtypes", "abbreviation", /* "capitalized", */
		};
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testSingleFile() throws IOException {
		FileUtils.copyDirectory(
				new File(DATA_16_1_1), 
				new File(TEMP_16_1_1));
		LOG.debug("copied file");
		String[] args = {
			"-q", TEMP_16_1_1,
			"--w.words", WordArgProcessor.WORD_LENGTHS, WordArgProcessor.WORD_FREQUENCIES,
			"--w.stopwords", STOPWORDS_TXT,
			"--w.wordlengths", "2", "12",
			"--w.wordtypes", "acronym", "GROT",
		};
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}

	@Test
	public void testExamplesFrequencies() throws IOException {
		FileUtils.copyDirectory(
				new File(EXAMPLES), 
				new File(EXAMPLES_TEMP));
		LOG.debug("copied file");
		String[] args = {
			"-q", EXAMPLES_TEMP,
			"--w.words", WordArgProcessor.WORD_FREQUENCIES,
			"--w.stopwords", STOPWORDS_TXT, CLINICAL_STOPWORDS_TXT,
		};
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}

	@Test
	@Ignore
	public void testStemming() throws IOException {
		FileUtils.copyDirectory(new File(DATA_16_1_1), new File(TEMP_16_1_1));
		LOG.debug("copied files");
		String args[] = {
			"-q", TEMP_16_1_1, // contains 86 QSN files
	"--w.words", WordArgProcessor.WORD_FREQUENCIES,
	"--w.stopwords", STOPWORDS_TXT,
	"--w.wordlengths", "2", "12",
	"--w.stem", "true",
		};
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testLowercase() throws IOException {
		FileUtils.copyDirectory(new File(DATA_16_1_1), new File(TEMP_16_1_1));
		LOG.debug("copied files");
		String args[] = {
			"-q", TEMP_16_1_1, 
	"--w.words", WordArgProcessor.WORD_FREQUENCIES,
	"--w.stopwords", STOPWORDS_TXT,
	"--w.wordlengths", "2", "12",
	"--w.case", "ignore",
		};
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}

	@Test
	public void testSummarize() throws IOException {
		if (Fixtures.EXAMPLES_TEMP.exists()) FileUtils.forceDelete(Fixtures.EXAMPLES_TEMP);
		FileUtils.copyDirectory(Fixtures.EXAMPLES, Fixtures.EXAMPLES_TEMP);
		LOG.debug("copied files");
		String args[] = {
			"-q", Fixtures.EXAMPLES_TEMP.toString(), 
	"--w.words", WordArgProcessor.WORD_FREQUENCIES,
	"--w.stopwords", STOPWORDS_TXT, 
	"--w.case", "ignore",
	"--w.summary", "aggregate",
	"--summaryfile", "target/examples/"
		};
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}


	@Test
	public void testSummarizeDocumentFrequencies() throws IOException {
		if (Fixtures.EXAMPLES_TEMP.exists()) FileUtils.forceDelete(Fixtures.EXAMPLES_TEMP);
		FileUtils.copyDirectory(Fixtures.EXAMPLES, Fixtures.EXAMPLES_TEMP);
		LOG.debug("copied files");
		String args[] = {
			"-q", Fixtures.EXAMPLES_TEMP.toString(), 
	"--w.words", WordArgProcessor.WORD_FREQUENCIES,
	"--w.stopwords", STOPWORDS_TXT, CLINICAL_STOPWORDS_TXT,
	"--w.case", "ignore",
	"--w.summary", "booleanFrequency",
	"--summaryfile", "target/examples/",
	"--w.mincount", "3"
		};
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}

}
