package org.xmlcml.xhtml2stm.visitor.regex;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real;

/** captures results of regex search.
 * 
 * may be made more abstract later.
 * 
 * @author pm286
 *
 */
public class RegexResults {

	private final static Logger LOG = Logger.getLogger(RegexResults.class);
	private HashMap<RegexComponent, Integer> countMap;

	
	void put(RegexComponent regexComponent, int count) {
		ensureCountMap();
		countMap.put(regexComponent, count);
	}

	private void ensureCountMap() {
		if (countMap == null) {
			countMap = new HashMap<RegexComponent, Integer>();
		}
	}

	protected Map<RegexComponent, Integer> getCountMap() {
		ensureCountMap();
		return countMap;
	}

	public void debug() {
		Double weightedCount = 0.0;
		LOG.trace("results");
		for (RegexComponent regexComponent : countMap.keySet()) {
			Integer count = countMap.get(regexComponent);
			if (count > 0) {
				LOG.trace(regexComponent+": "+count);
				weightedCount += ((double) count) * regexComponent.getWeight(); 
			}
		}
		if (weightedCount > 0.001) {
			LOG.trace("weight: "+Real.normalize(weightedCount, 2));
		}
	}

}
