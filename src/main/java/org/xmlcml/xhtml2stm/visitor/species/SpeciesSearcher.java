package org.xmlcml.xhtml2stm.visitor.species;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.Type;
import org.xmlcml.xhtml2stm.result.ResultList;
import org.xmlcml.xhtml2stm.visitable.SourceElement;
import org.xmlcml.xhtml2stm.visitable.html.HtmlContainer;
import org.xmlcml.xhtml2stm.visitable.xml.XMLContainer;
import org.xmlcml.xhtml2stm.visitable.xml.XMLVisitable;
import org.xmlcml.xhtml2stm.visitor.AbstractSearcher;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;

import com.google.common.collect.Multiset;

public class SpeciesSearcher extends AbstractSearcher {

	private final static Logger LOG = Logger.getLogger(SpeciesSearcher.class);

	public enum SpeciesType implements Type {
		ANY,
		BINOMIAL,
		GENUS;
	}

	private SpeciesType speciesType;
	private Multiset<LinneanName> binomialSet;
	private LinneanNamer linneanNamer;

	public SpeciesSearcher(AbstractVisitor visitor) {
		super(visitor);
		setDefaults();
	}

	private void setDefaults() {
		this.setSpeciesType(SpeciesType.BINOMIAL);
		this.addXPath(XMLVisitable.ITALIC);
	}

	void setSpeciesType(SpeciesType type) {
		this.setType(type);
		this.patternList = new ArrayList<Pattern>();
		addSpeciesType(type);
	}

	void addSpeciesType(SpeciesType type) {
		ensurePatternList();
		if (SpeciesType.BINOMIAL.equals(type)) {
			this.addPattern(LinneanName.BINOMIAL_PATTERN);
		} else if (SpeciesType.GENUS.equals(type)) {
			this.addPattern(LinneanName.GENUS_PATTERN);
		} else {
			throw new RuntimeException("Type not supported: "+type);
		}
		this.speciesType = (patternList.size() == 1) ? type : SpeciesType.ANY;
	}

	@Override
	protected SpeciesListElement createListElement(ResultList binomialSet) {
		return new SpeciesListElement(speciesType, binomialSet);
	}

	@Override 
	protected void search(HtmlContainer htmlContainer) {
		searchXPathPatternAndCollectResults(new SourceElement(htmlContainer));
	}
	
	@Override 
	protected void search(XMLContainer xmlContainer) {
		searchXPathPatternAndCollectResults(new SourceElement(xmlContainer));
	}
	
	public SpeciesListElement createListElement() {
		return new SpeciesListElement(speciesType, resultList);
	}

	@Override
	protected void transformResultList() {
		ensureLinneanNamer();
		linneanNamer.createAbbreviationExpandedBinomialSet(resultList);
		binomialSet = linneanNamer.getBinomialSet();
	}
	
	/**
	 * @return the binomialSet
	 */
	public Multiset<LinneanName> getBinomialSet() {
		return binomialSet;
	}

	private void ensureLinneanNamer() {
		if (linneanNamer == null) {
			this.linneanNamer = new LinneanNamer();
		}
	}

}
