package org.xmlcml.ami.visitor.species;

import org.apache.log4j.Logger;
import org.xmlcml.ami.Type;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.visitable.SourceElement;
import org.xmlcml.ami.visitable.html.HtmlContainer;
import org.xmlcml.ami.visitable.xml.XMLContainer;
import org.xmlcml.ami.visitable.xml.XMLVisitable;
import org.xmlcml.ami.visitor.AbstractSearcher;
import org.xmlcml.ami.visitor.AbstractVisitor;

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
		addSpeciesType(type);
	}

	void addSpeciesType(SpeciesType type) {
		if (SpeciesType.BINOMIAL.equals(type)) {
			this.addPattern(LinneanName.BINOMIAL_PATTERN);
		} else if (SpeciesType.GENUS.equals(type)) {
			this.addPattern(LinneanName.GENUS_PATTERN);
		} else {
			throw new RuntimeException("Type not supported: "+type);
		}
		this.speciesType = (documentFragment.getPatternList().size() == 1) ? type : SpeciesType.ANY;
	}

	@Override
	protected SpeciesListElement createListElement(SimpleResultList binomialSet) {
		return new SpeciesListElement(speciesType, binomialSet);
	}

	@Override 
	protected void search(HtmlContainer htmlContainer) {
		ensureDocumentFragment();
		documentFragment.searchXPathPatternAndCollectResults(new SourceElement(htmlContainer));
	}
	
	@Override 
	protected void search(XMLContainer xmlContainer) {
		ensureDocumentFragment();
		documentFragment.searchXPathPatternAndCollectResults(new SourceElement(xmlContainer));
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
