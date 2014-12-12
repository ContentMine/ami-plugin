package org.xmlcml.ami.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.SourceElement;
import org.xmlcml.ami.visitor.EIC;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class SimpleResultList implements Iterable<SimpleResultWrapper> {

	private final static Logger LOG = Logger.getLogger(SimpleResultList.class);
	
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
	
	public void add(EIC eic) {
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

	public Element createElement() {
		Element resultList = new ResultsElement();
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
		for (SimpleResultWrapper simpleResultWrapper : simpleResultList) {
			sb.append("> "+simpleResultWrapper.toString()+"\n");
			
		}
		return sb.toString();
	}



}
