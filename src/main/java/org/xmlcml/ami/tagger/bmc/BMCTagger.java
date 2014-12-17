package org.xmlcml.ami.tagger.bmc;

import java.io.File;

import org.apache.log4j.Logger;
import org.xmlcml.ami.tagger.JournalTagger;

public class BMCTagger extends JournalTagger {

	private final static Logger LOG = Logger.getLogger(BMCTagger.class);

	public final static String BMC = "bmc";
	private static final File TAGGER_BMC_DIR = new File(TAGGER_DIR, BMC);
	public static final File BMC_TAGDEFINITIONS_FILE = new File(TAGGER_BMC_DIR, "tagDefinitions.xml");

	public BMCTagger() {
		super(BMC_TAGDEFINITIONS_FILE);
	}

	public static String getTaggerName() {
		return BMC;
	}

}
