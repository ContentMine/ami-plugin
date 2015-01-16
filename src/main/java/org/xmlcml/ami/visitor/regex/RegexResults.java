package org.xmlcml.ami.visitor.regex;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.SimpleResultWrapper;
import org.xmlcml.euclid.Real;

/** captures results of regex search.
 * 
 * may be made more abstract later.
 * 
 * @author pm286
 *
 */
@Deprecated // use a list of RegexResult's
public class RegexResults {

	private final static Logger LOG = Logger.getLogger(RegexResults.class);
	private List<RegexComponent> regexComponentList;

	public RegexResults() {
		
	}
	
	void add(RegexComponent regexComponent, MatcherResult matcherResult) {
		ensureComponentList();
		regexComponentList.add(regexComponent);
	}

	private void ensureComponentList() {
		if (regexComponentList == null) {
			regexComponentList = new ArrayList<RegexComponent>();
		}
	}

	protected List<RegexComponent> getComponentList() {
		ensureComponentList();
		return regexComponentList;
	}

	public void debug() {
		Double weightedCount = 0.0;
		LOG.trace("results");
		for (RegexComponent regexComponent : regexComponentList) {
			weightedCount += regexComponent.getWeight(); 
		}
		if (weightedCount > 0.001) {
			LOG.trace("weight: "+Real.normalize(weightedCount, 2));
		}
	}

	public SimpleResultWrapper getSimpleResult() {
		SimpleResultWrapper simpleResult = new SimpleResultWrapper();
		for (RegexComponent regexComponent : regexComponentList) {
			simpleResult.setResultString(regexComponent.toString());
		}
		LOG.debug("simpleResult: "+simpleResult);
		return simpleResult;
	}

}
