package org.xmlcml.ami;

import java.io.File;

public class Fixtures {

	public static final File MAIN_RESOURCES_DIR = new File("src/main/resources/");
	public static final File TEST_RESOURCES_DIR = new File("src/test/resources/");
	public static final File PDF_DIR = new File("../pdfs");

	public static final File AMI_DIR = new File(TEST_RESOURCES_DIR, "org/xmlcml/ami/");
	
	//CHEM
	public static final File MOLECULES_DIR = new File(AMI_DIR, "molecules");
	
	public static File IMAGE_2_10_SVG = new File(MOLECULES_DIR, "image.g.2.10.svg");
	public static File IMAGE_2_11_SVG = new File(MOLECULES_DIR, "image.g.2.11.svg");
	public static File IMAGE_2_13_SVG = new File(MOLECULES_DIR, "image.g.2.13.svg");
	public static File IMAGE_2_15_SVG = new File(MOLECULES_DIR, "image.g.2.15.svg");
	public static File IMAGE_2_16_SVG = new File(MOLECULES_DIR, "image.g.2.16.svg");
	public static File IMAGE_2_18_SVG = new File(MOLECULES_DIR, "image.g.2.18.svg");
	public static File IMAGE_2_21_SVG = new File(MOLECULES_DIR, "image.g.2.21.svg");
	public static File IMAGE_2_22_SVG = new File(MOLECULES_DIR, "image.g.2.22.svg");
	public static File IMAGE_2_23_SVG = new File(MOLECULES_DIR, "image.g.2.23.svg");
	public static File IMAGE_2_25_SVG = new File(MOLECULES_DIR, "image.g.2.25.svg");
	public static File IMAGE_3_10_SVG = new File(MOLECULES_DIR, "image.g.3.10.svg");
	public static File IMAGE_3_15_SVG = new File(MOLECULES_DIR, "image.g.3.15.svg");
	public static File IMAGE_5_11_SVG = new File(MOLECULES_DIR, "image.g.5.11.svg");
	public static File IMAGE_5_12_SVG = new File(MOLECULES_DIR, "image.g.5.12.svg");
	public static File IMAGE_5_13_SVG = new File(MOLECULES_DIR, "image.g.5.13.svg");
	public static File IMAGE_5_14_SVG = new File(MOLECULES_DIR, "image.g.5.14.svg");
	
	public static File IMAGE_02_00100_6_5_SVG = new File(MOLECULES_DIR, "02.00100.g.6.5.svg");
	
	public static File SMALL_TEST_1 = new File(MOLECULES_DIR, "smalltest1.svg");
	public static File SMALL_TEST_2 = new File(MOLECULES_DIR, "smalltest2.svg");
	public static File SMALL_TEST_3 = new File(MOLECULES_DIR, "smalltest3.svg");
	public static File SMALL_TEST_4 = new File(MOLECULES_DIR, "smalltest4.svg");
	public static File SMALL_TEST_5 = new File(MOLECULES_DIR, "smalltest5.svg");
	
	public static File WEDGES = new File(AMI_DIR, "wedges.svg");
	public static File WEDGESREDUCED = new File(AMI_DIR, "wedgesreduced.svg");
	public static File ARROWHEADS = new File(AMI_DIR, "arrowheads.svg");
	public static File THICKLINES = new File(AMI_DIR, "thicklines.svg");
	public static File THICKLINESREDUCED = new File(AMI_DIR, "thicklinesreduced.svg");
	
	public static File ASPERFURAN_LABEL_CML = new File(MOLECULES_DIR, "asperfuran.label.cml");
	public static File METHOXYMETHANE = new File(MOLECULES_DIR, "methoxymethane.svg");
	public static File KOJIC_LABEL_CML = new File(MOLECULES_DIR, "kojic.label.cml");
	public static File METHOXYMETHANE1_LABEL_CML = new File(MOLECULES_DIR, "methoxymethane1.label.cml");
	
	public static File GROUPS_CML = new File(MAIN_RESOURCES_DIR, "org/xmlcml/ami/visitor/chem/groups.cml");
	
	// FIGURES
	public static final File FIGURE_DIR = new File(TEST_RESOURCES_DIR, "org/xmlcml/ami/figures");
	public static final File LINEPLOTS_10_2_SVG = new File(FIGURE_DIR, "lineplots.g.10.2.svg");
	public static final File SCATTERPLOT_FIVE_7_2_SVG = new File(FIGURE_DIR, "scatterplot5.g.7.2.svg");
	public static final File SCATTERPLOT_7_2_SVG = new File(FIGURE_DIR, "scatterplot.g.7.2.svg");

	public static final File IMAGES_DIR = new File(AMI_DIR, "images");
	public static final File IMAGE_G_2_2_SVG = new File(IMAGES_DIR, "image.g.2.2.svg");
	public static final File IMAGE_G_3_2_SVG = new File(IMAGES_DIR, "image.g.3.2.svg");
	public static final File IMAGE_G_8_0_SVG = new File(IMAGES_DIR, "image.g.8.0.svg");
	public static final File IMAGE_G_8_2_SVG = new File(IMAGES_DIR, "image.g.8.2.svg");
	
	// HTML
	public static final File HTML_DIR = new File(AMI_DIR, "html");
	public static final File MULTIPLE_312_HTML = new File(HTML_DIR, "multiple.312.html");
	public static final File METABOLITE_00039_HTML = new File(MOLECULES_DIR, "metabolite00039.html");
	
	// XML
	public static final File XML_DIR = new File(AMI_DIR, "xml");
	public static final File PLOSONE_0080753_XML = new File(XML_DIR, "journal.pone.0080753.xml");
	
	// MATERIALS/SPECTRA
	public static final File MATERIALS_DIR = new File(AMI_DIR, "materials");

	// OUTPUT
	public static final File TEST_DIRECTORIES_DIR = new File(TEST_RESOURCES_DIR, "org/xmlcml/ami/directories");
	
	// REGEX
	public static final File MAIN_REGEX_DIR = new File(MAIN_RESOURCES_DIR, "org/xmlcml/ami/regex");
	public static final File TEST_REGEX_DIR = new File(TEST_RESOURCES_DIR, "org/xmlcml/ami/regex");
	public static final File PHYLO_REGEX_XML = new File(MAIN_REGEX_DIR, "phylotree.xml");
	public static final File ARMBRUSTER_HTML = new File(TEST_REGEX_DIR, "armbruster.html");

	// SEQUENCE
	public static final File SEQUENCE_DIR = new File(AMI_DIR, "sequences");

	// SPECIES
	public static final File SPECIES_DIR = new File(AMI_DIR, "species");
//	public static final File MULTIPLE_SPECIES_312_HTML = new File(SPECIES_DIR, "multiple.species.312.html");
	public static final File MULTIPLE_SPECIES_312_HTML = new File(SPECIES_DIR, "1471-2148-11-312.html");
	public static final File MULTIPLE_SPECIES_312_XML = new File(SPECIES_DIR, "1471-2148-11-312.xml");
	public static final File PASSERINES_313_HTML = new File(SPECIES_DIR, "passerines.313.html");
	public static final File HADROSAUR_XML = new File(SPECIES_DIR, "journal.pone.0077058.xml");
	public static final File MANY_SPECIES_DIR = new File(SPECIES_DIR, "many");

	// TREE
	public static final File TREE_DIR = new File(TEST_RESOURCES_DIR, "org/xmlcml/ami/tree");
	public static final File TREE_CLUSTER1_SVG = new File(TREE_DIR, "page4panel1Cluster1.svg");
	public static final File TREE_CLUSTER1A_SVG = new File("target/page4panel1Cluster1a.svg");
	public static final File TREE_CLUSTER1B_SVG = new File("target/page4panel1Cluster1b.svg");
	public static final File TREE_CLUSTER2A_SVG = new File("target/page4panel1Cluster2a.svg");
	public static final File TREE_PANEL1_SVG = new File(TREE_DIR, "panel1.svg");
	public static final File TREE_GIBBONS_LARGE_SVG = new File(TREE_DIR, "gibbons.large.svg");
	public static final File TREE_GIBBONS_SMALL_SVG = new File(TREE_DIR, "gibbons.small.svg");
	public static final File TREE_BIRDS_CLEAN_SVG = new File(TREE_DIR, "birds.clean.svg");
	public static final File TREE_BIRDS_CLEAN_SMALL_SVG = new File(TREE_DIR, "birds.clean.small.svg");
	
	//PLOSONE
	public static final File PLOSONE_DIR = new File(AMI_DIR, "plosone");
	public static final File TEST_RESULTS0113556_XML = new File("target/journal.pone.0113556.tagged.xml/results.xml");
	public static final File RAW_0113556 = new File(PLOSONE_DIR, "journal.pone.0113556.xml");
	public static final File TAGGED_0113556 = new File(PLOSONE_DIR, "journal.pone.0113556.tagged.xml");
	public static final File RESULTS_0113556 = new File(PLOSONE_DIR, "journal.pone.0113556.results.xml");
	public static final File RESULTS_ABSTRACT_0113556 = new File(PLOSONE_DIR, "journal.pone.0113556.results.abstract.xml");
	public static final File RESULTS_FIGURES_0113556 = new File(PLOSONE_DIR, "journal.pone.0113556.results.figures.xml");
	
	//SECTIONS
	public static final File SECTIONS_DIR = new File(AMI_DIR, "sections");
	public static final File BMC_SECTIONS = new File(SECTIONS_DIR, "bmc");

	// PDFS
	public static final File BMC_PDFS = new File(PDF_DIR, "bmc");
	public static final File BMC_SAMPLE = new File(BMC_PDFS, "svgSample");
	public static final File BMC_ALL =  new File(BMC_PDFS, "svgOutput");
	
	// LOCAL PDFS
	public static final File XHTML_PDF_DIR = new File(AMI_DIR, "pdf");
	public static final File MULTIPLE_312_PDF = new File(XHTML_PDF_DIR, "multiple-1471-2148-11-312.pdf");
	public static final File _329_PDF = new File(XHTML_PDF_DIR, "1471-2148-11-329.pdf");
	
	// AMI
	public static final File AMI_OUT = new File("target/amiout");

	
}