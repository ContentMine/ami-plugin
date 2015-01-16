package org.xmlcml.ami.visitor.chem.chemicaltagger;

import nu.xom.Document;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;

import uk.ac.cam.ch.wwmm.chemicaltagger.ChemistryPOSTagger;
import uk.ac.cam.ch.wwmm.chemicaltagger.ChemistrySentenceParser;
import uk.ac.cam.ch.wwmm.chemicaltagger.POSContainer;
import uk.ac.cam.ch.wwmm.chemicaltagger.Utils;
//import org.xmlcml.cml.converters.format.LineReader.FormatType;

public class ChemicalTaggerSearcher extends AbstractSearcher {

	private final static Logger LOG = Logger.getLogger(ChemicalTaggerSearcher.class);

	private static final String ALL_TEXT_PATH = "//text()";
	private static final String ALL_ELEMENTS_PATH = "//*[text()[not(normalize-space()='')]]";
	private static final String ALL_PARA_PATH = "//*[local-name()='p']";
	private static final String ALL_DIV_PATH = "//*[local-name()='div']";


	public ChemicalTaggerSearcher(AbstractVisitor visitor) {
		super(visitor);
		setDefaults();
	}

	private void setDefaults() {
		//
	}

	@Override
	protected ChemicalTaggerListElement createListElement(SimpleResultList resultSet) {
		throw new RuntimeException("cannot create OSCAR list yet");
//		return new OSCARListElement(sequenceType, resultSet);
	}
	
	@Override 
	protected void search(HtmlContainer htmlContainer) {
		// ensureDocumentFragment();
		// documentFragment.searchXPathPatternAndCollectResults(new
		// SourceElement(htmlContainer));
		String s = htmlContainer.getHtmlElement().getValue();
		// Calling ChemistryPOSTagger
		POSContainer posContainer = ChemistryPOSTagger.getDefaultInstance()
				.runTaggers(s);

		// Returns a string of TAG TOKEN format (e.g.: DT The NN cat VB sat IN
		// on DT the NN matt)
		// Call ChemistrySentenceParser either by passing the POSContainer or by
		// InputStream
		ChemistrySentenceParser chemistrySentenceParser = new ChemistrySentenceParser(
				posContainer);

		// Create a parseTree of the tagged input
		chemistrySentenceParser.parseTags();

		// Return an XMLDoc
		Document doc = chemistrySentenceParser.makeXMLDocument();

		Utils.writeXMLToFile(doc, "target/file1.xml");
	}
	
}
