package org.xmlcml.xhtml2stm.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.visitable.SourceElement;
import org.xmlcml.xhtml2stm.visitor.ElementInContext;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class SimpleResultList implements Iterable<SimpleResultWrapper> {

	private final static Logger LOG = Logger.getLogger(SimpleResultList.class);
	
	private static final String RESULTS_LIST = "results";
	
	private List<SimpleResultWrapper> simpleResultList;

	private SourceElement sourceElement;

	public SimpleResultList(SourceElement sourceElement) {
		ensureResultList();
		this.sourceElement = sourceElement;
	}

	private void ensureResultList() {
		if (simpleResultList == null) {
			simpleResultList = new ArrayList<SimpleResultWrapper>();
		}
	}

	public void add(SimpleResultWrapper simpleResult) {
		simpleResultList.add(simpleResult);
	}

	public void add(String resultString) {
		SimpleResultWrapper simpleResult = new SimpleResultWrapper(resultString);
		this.add(simpleResult);
	}
	
	public void add(ElementInContext eic) {
		SimpleResultWrapper simpleResult = new SimpleResultWrapper(eic);
		this.add(simpleResult);
	}
	
	@Override
	public Iterator<SimpleResultWrapper> iterator() {
		return simpleResultList.iterator();
	}

	public int size() {
		return simpleResultList.size();
	}

	public List<SimpleResultWrapper> getList() {
		return simpleResultList;
	}

	/** set with counts of occurrences.
	 * 
	 * @return
	 */
	public Multiset<String> getOrCreateStringMultiSet() {
		Multiset<String> stringSet = HashMultiset.create();
		for (SimpleResultWrapper result : simpleResultList) {
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
		for (SimpleResultWrapper result : simpleResultList) {
			eicListByKeyword.put(result.toString(), result.getElementInContext());
		}
		return eicListByKeyword;
	}
	
	public Element createElement() {
		Element resultList = new Element(RESULTS_LIST);
		if (sourceElement != null) {
			resultList.appendChild(sourceElement.copy());
		}
		for (SimpleResultWrapper result : simpleResultList) {
			Element resultElement = result.createElement();
			if (resultElement == null) {
				resultList.appendChild(new Element("null"));
			} else {
				resultList.appendChild(result.createElement());
			}
		}
		return resultList;
		
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("hits: "+simpleResultList.size());
		for (SimpleResultWrapper simpleResult : simpleResultList) {
			sb.append("> "+simpleResult.toString()+"\n");
			
		}
		return sb.toString();
	}



}