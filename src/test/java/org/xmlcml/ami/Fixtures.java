package org.xmlcml.ami;

import java.io.File;

public class Fixtures {

	public final static File TEST_RESOURCES_DIR = new File("src/test/resources/");
	public final static File TEST_AMI_DIR = new File(Fixtures.TEST_RESOURCES_DIR, "org/xmlcml/ami");
//	public final static File TEST_PLUGIN_DIR = new File(Fixtures.TEST_AMI_DIR, "plugin");
	
	public final static File TEST_BMC_DIR = new File(Fixtures.TEST_AMI_DIR, "bmc");
	public final static File TEST_BMC_15_1_511_QSN = new File(Fixtures.TEST_BMC_DIR, "15_1_511");

	public final static File TEST_MIXED_DIR = new File(Fixtures.TEST_AMI_DIR, "mixed");
}
