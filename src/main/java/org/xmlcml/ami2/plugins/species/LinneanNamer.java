package org.xmlcml.ami2.plugins.species;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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

	private Multimap<String, LinneanName> linneanNameByAbbreviationMap;
	private Multimap<String, LinneanName> linneanNameBySpeciesMap;
	private Multiset<LinneanName> linneanNameSet;

	private Map<String, String> genusByAbbreviationMap;
	
	public LinneanNamer() {
		
	}
	
	/** create from two string name.
	 * 
	 * does not expand abbreviations
	 * 
	 * @param gs form "Homo sapiens" or "H.sapiens"
	 * @return null if of wrong form
	 */
	public static LinneanName createBinomial(String gs) {
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

	public void createAbbreviationExpandedBinomialSet(List<String> speciesNameList) {
		getOrCreateLinneanNameBySpeciesMap(speciesNameList);
		getOrCreateLinneanNameByAbbreviationMap();
		getOrCreateLinneanNameSetIncludingAbbreviationExpansion(speciesNameList);
	}
	
	public Multiset<LinneanName> getBinomialSet() {
		return linneanNameSet;
	}

	private void getOrCreateLinneanNameSetIncludingAbbreviationExpansion(List<String> speciesNameList) {
		if (linneanNameSet == null) {
			linneanNameSet = HashMultiset.create();
			for (String speciesName : speciesNameList) {
				LinneanName binomial = LinneanNamer.createBinomial(speciesName);
				if (binomial == null) {
					LOG.debug("null binomial: "+speciesName);
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

	private Multimap<String, LinneanName> getOrCreateLinneanNameBySpeciesMap(List<String> speciesNameList) {
		getOrCreateLinneanNameSetIncludingAbbreviationExpansion(speciesNameList);
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

	/** expand single character abbreviations.
	 * 
	 * assume an order list of names where the genus is always listed explicitly
	 * before the abbreviation.
	 * 
	 * @param nameList
	 */
	public List<String> expandAbbreviations(List<String> nameList) {
		List<String> newNameList = new ArrayList<String>();
		genusByAbbreviationMap = new HashMap<String, String>();
		for (String name : nameList) {
			LinneanName linneanName = LinneanNamer.createBinomial(name);
			if (linneanName == null) {
				LOG.debug("Not a binomial");
			} else {
				String genusAbbreviation = linneanName.getGenusAbbreviation();
				String fullGenus = genusByAbbreviationMap.get(genusAbbreviation);
				if (linneanName.isSingleCharacterGenus()) {
					if (fullGenus == null) {
						LOG.error("Cannot resolve abbreviation: ["+genusAbbreviation+"]");
					} else {
						linneanName.setGenus(fullGenus);
					}
				} else {
					String linneanGenus = linneanName.getGenus();
					if (fullGenus == null) {
						genusByAbbreviationMap.put(genusAbbreviation, linneanGenus);
					} else if (!fullGenus.equals(linneanGenus)) {
						LOG.error("Possible duplicate Genera for abbreviation ("+genusAbbreviation+") : "+fullGenus+", "+linneanGenus);
					}
				}
				name = linneanName.getName();
			}
//			LOG.debug(name);
			newNameList.add(name);
		}
		return newNameList;
	}

}
