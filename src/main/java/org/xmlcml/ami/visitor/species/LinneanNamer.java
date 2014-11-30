package org.xmlcml.ami.visitor.species;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/** creates or processes LinneanNames.
 * 
 * @author pm286
 *
 */
public class LinneanNamer {

	private final static Logger LOG = Logger.getLogger(LinneanNamer.class);

	private SimpleResultList resultList; // input
	private Multimap<String, LinneanName> linneanNameByAbbreviationMap;
	private Multimap<String, LinneanName> linneanNameBySpeciesMap;
	private Multiset<LinneanName> linneanNameSet;
	
	public LinneanNamer() {
		
	}
	
	public LinneanName createBinomial(SimpleResultWrapper element) {
		String string = element == null ? null : element.toString();
		return createBinomial(string);
	}

	/** create from two string name.
	 * 
	 * does not expand abbreviations
	 * 
	 * @param gs form "Homo sapiens" or "H.sapiens"
	 * @return null if of wrong form
	 */
	public LinneanName createBinomial(String gs) {
		LinneanName binomial = null;
		if (gs != null) {
			gs = gs.replaceAll("\\s+", " ");
			String[] ss = gs.split(" ");
			if (ss.length == 2) {
				binomial = new LinneanName(ss[0], ss[1]);
			}
		}
		return binomial;
	}

	void createAbbreviationExpandedBinomialSet(SimpleResultList resultList) {
		this.resultList = resultList;
		getOrCreateLinneanNameBySpeciesMap();
		getOrCreateLinneanNameByAbbreviationMap();
		getOrCreateLinneanNameSetIncludingAbbreviationExpansion();
	}
	
	public Multiset<LinneanName> getBinomialSet() {
		return linneanNameSet;
	}

	private void getOrCreateLinneanNameSetIncludingAbbreviationExpansion() {
		if (linneanNameSet == null) {
			linneanNameSet = HashMultiset.create();
			for (SimpleResultWrapper result : resultList) {
				String keyword = result.getKeyword();
				LinneanName binomial = this.createBinomial(keyword);
				if (binomial == null) {
					LOG.debug("null binomial: "+keyword+" <"+result+">");
				} else if (binomial.isSingleCharacterGenus()) {
					
					String genus = binomial.getGenus();
					LOG.debug("genus: "+genus);
					Collection<LinneanName> linneanNames = linneanNameByAbbreviationMap == null ? null :
						linneanNameByAbbreviationMap.get(genus);
					LinneanName fullLinnean = linneanNames == null || linneanNames.size() == 0 ? null :
						new ArrayList<LinneanName>(linneanNames).get(0);
					if (fullLinnean == null) {
						LOG.error("Cannot resolve abbreviation: "+binomial);
					} else {
						binomial = fullLinnean;
					}
					linneanNameSet.add(binomial);
				}
			}
		}
	}

	private Multimap<String, LinneanName> getOrCreateLinneanNameBySpeciesMap() {
		getOrCreateLinneanNameSetIncludingAbbreviationExpansion();
		if (linneanNameBySpeciesMap == null) {
			linneanNameBySpeciesMap = ArrayListMultimap.create();
			for (LinneanName linneanName : linneanNameSet) {
				linneanNameBySpeciesMap.put(linneanName.getSpeciesString(), linneanName);
			}
		}
		return linneanNameBySpeciesMap;
	}

	private Multimap<String, LinneanName> getOrCreateLinneanNameByAbbreviationMap() {
		if (linneanNameByAbbreviationMap == null) {
			linneanNameByAbbreviationMap = HashMultimap.create();
			for (String key : linneanNameBySpeciesMap.keySet()) {
				List<LinneanName> binomialList = new ArrayList<LinneanName>(linneanNameBySpeciesMap.get(key));
				if (binomialList.size() == 2) {
					for (LinneanName binomial : binomialList) {
						if (binomial.isSingleCharacterGenus()) {
							int index = binomialList.indexOf(binomial);
							LinneanName fullBinomial = binomialList.get(1- index);
							linneanNameByAbbreviationMap.put(binomial.getGenus(), fullBinomial);
						}
					}
				}
			}
		}
		return linneanNameByAbbreviationMap;
	}

}
