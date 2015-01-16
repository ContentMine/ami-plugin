package org.xmlcml.ami.visitor.chem.oscar;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;

import uk.ac.cam.ch.wwmm.oscar.Oscar;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ChemicalStructure;
//import org.xmlcml.cml.converters.format.LineReader.FormatType;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.FormatType;
import uk.ac.cam.ch.wwmm.oscar.chemnamedict.entities.ResolvedNamedEntity;

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
	protected OSCARListElement createListElement(SimpleResultList resultSet) {
		throw new RuntimeException("cannot create OSCAR list yet");
//		return new OSCARListElement(sequenceType, resultSet);
	}
	
	@Override 
	protected void search(HtmlContainer htmlContainer) {
//		ensureDocumentFragment();
//		documentFragment.searchXPathPatternAndCollectResults(new SourceElement(htmlContainer));
		String s = htmlContainer.getHtmlElement().getValue();
		List<ResolvedNamedEntity> entities = oscarSearch(s);
	}

	private List<ResolvedNamedEntity> oscarSearch(String s) {
		Oscar oscar = new Oscar();
		List<ResolvedNamedEntity> entities = new ArrayList<ResolvedNamedEntity>();
//		try {
			entities = oscar.findAndResolveNamedEntities(s);
//		} catch (Exception e) {
//			LOG.error("Cannot parse: " + s + "in OPSIN "+e);
//		}
		for (ResolvedNamedEntity ne : entities) {
		    System.out.println(ne.getSurface());
		    ChemicalStructure stdInchi = ne.getFirstChemicalStructure(FormatType.STD_INCHI);
		    if (stdInchi != null) {
		        System.out.println(stdInchi);
		    }
		    System.out.println();
		}
		return entities;
	}
	
}
