package org.xmlcml.ami.plugin.words;

import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.plugin.plugins.AMIArgProcessor;
import org.xmlcml.ami.plugin.plugins.words.WordArgProcessor;

public class WordsTest {

	@Test
	public void testWordsHelp() {
		new WordArgProcessor();
	}

	@Test
	public void testWords() {
		String[] args = {
			"-q", Fixtures.TEST_PLOSONE_0115884.toString(),
			"--w.words", WordArgProcessor.WORD_LENGTHS, WordArgProcessor.WORD_FREQUENCIES,
			"--w.stopwords", "/org/xmlcml/ami/plugin/words/stopwords.txt",
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
			"--w.stopwords", "/org/xmlcml/ami/plugin/words/stopwords.txt",
			"--w.wordlengths", "2", "12",
			"--w.wordtypes", "abbreviation", /* "capitalized", */
		};
		AMIArgProcessor argProcessor = new WordArgProcessor(args);
		argProcessor.runAndOutput();
	}
}
