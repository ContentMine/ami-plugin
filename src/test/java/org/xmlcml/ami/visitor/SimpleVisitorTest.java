package org.xmlcml.ami.visitor;

import java.io.File;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.files.EuclidSource;

import com.google.inject.internal.util.StackTraceElements;

public class SimpleVisitorTest {

	
	private static final Logger LOG = Logger.getLogger(SimpleVisitorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testHelp() throws Exception {
		String[] args = new String[] {
				"--help"
		};
		SimpleVisitor.main(args);
	}

	@Test
	public void testCorrectSyntax() throws Exception {
		parseCorrectParameters(new String[]{"-i", "foo"});
		parseCorrectParameters(new String[]{"-i", "foo", "bar"});
		parseCorrectParameters(new String[]{"-r", "true"});
		try {
			parseCorrectParameters(new String[]{"-i"});
			Assert.fail("should throw exception");
		} catch (Exception e) {
		}
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
	}

	// ====================================================
	
	private void parseCorrectParameters(String[] args) {
		try {
			SimpleVisitor.main(args);
		} catch (Exception e) {
			e.printStackTrace();
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
				"-p", "these", "are", "test", "params",
				"-e", EuclidSource.XML
		};
		SimpleVisitor.main(args);
	}
	
	
}
