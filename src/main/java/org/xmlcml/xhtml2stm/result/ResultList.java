package org.xmlcml.xhtml2stm.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlcml.xhtml2stm.visitor.ElementInContext;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class ResultList implements Iterable<SimpleResult> {

	private List<SimpleResult> resultList;

	public ResultList() {
		ensureResultList();
	}

	private void ensureResultList() {
		if (resultList == null) {
			resultList = new ArrayList<SimpleResult>();
		}
	}

	public void add(SimpleResult simpleResult) {
		resultList.add(simpleResult);
	}

	public void add(String resultString) {
		SimpleResult simpleResult = new SimpleResult(resultString);
		this.add(simpleResult);
	}
	
	public void add(ElementInContext eic) {
		SimpleResult simpleResult = new SimpleResult(eic);
		this.add(simpleResult);
	}
	
	@Override
	public Iterator<SimpleResult> iterator() {
		return resultList.iterator();
	}

	public int size() {
		return resultList.size();
	}

	public List<SimpleResult> getList() {
		return resultList;
	}

	/** set with counts of occurrences.
	 * 
	 * @return
	 */
	public Multiset<String> getOrCreateStringMultiSet() {
		Multiset<String> stringSet = HashMultiset.create();
		for (SimpleResult result : resultList) {
			stringSet.add(result.toString());
		}
		return stringSet;
	}

	/** map with lists of contexts indexed by search terms.
	 * 
	 * @return
	 */
	public Multimap<String, ElementInContext> getOrCreateStringMultimap() {
		Multimap<String, ElementInContext> eicListByKeyword = HashMultimap.create();
		for (SimpleResult result : resultList) {
			eicListByKeyword.put(result.toString(), result.getElementInContext());
		}
		return eicListByKeyword;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("hits: "+resultList.size());
		for (SimpleResult simpleResult : resultList) {
			sb.append("> "+simpleResult.toString()+"\n");
			
		}
		return sb.toString();
	}



}
