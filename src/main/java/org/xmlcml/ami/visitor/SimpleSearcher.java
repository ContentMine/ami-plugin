package org.xmlcml.ami.visitor;

import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami.result.AbstractListElement;
import org.xmlcml.ami.result.SimpleResultList;
import org.xmlcml.ami.result.SimpleResultWrapper;
import org.xmlcml.ami.visitable.xml.XMLContainer;
import org.xmlcml.ami.visitor.regex.CompoundRegex;
import org.xmlcml.ami.visitor.regex.RegexArgProcessor;
import org.xmlcml.ami.visitor.regex.RegexResultElement;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class SimpleSearcher extends AbstractSearcher {

	
	private static final Logger LOG = Logger.getLogger(SimpleSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	protected SimpleSearcher(AbstractVisitor visitor) {
		super(visitor);
	}

	@Override
	protected AbstractListElement createListElement(SimpleResultList resultList) {
		AbstractListElement listElement = new SimpleListElement(resultList);
		return listElement;
	}

	/** very simple demo search counts lengths of words.
	 * 
	 * Do not use for anything serious
	 * 
	 */
	@Override
	protected void search(XMLContainer xmlContainer) {
		simpleSearch(xmlContainer);
		LOG.trace("RESULT LIST... "+(resultList == null ? "null results" : ""+resultList.size()));
	}

	private void simpleSearch(XMLContainer xmlContainer) {
		AMIArgProcessor amiArgProcessor = abstractVisitor.getArgProcessor();
		
		String value = xmlContainer.getElement().getValue().trim();
		String[] words = value.split("\\s+");
		Element lengthsElement = getWordlengthFrequency(words);
		LOG.debug(lengthsElement.toXML());
		ensureResultList(sourceElement);
		SimpleResultWrapper resultsWrapper = new SimpleResultWrapper();
		resultsWrapper.setResultElement(lengthsElement);
		resultList.add(resultsWrapper);
		
		LOG.debug("MADE RESULT LIST: "+resultList.size());
		return;
	}

	private Element getWordlengthFrequency(String[] words) {
		Multiset<Integer> lengthSet = HashMultiset.create();
		for (String word : words) {
			lengthSet.add(word.length());
		}
		Element lengthsElement = new Element("lengths");
		for (Entry<Integer> entry : lengthSet.entrySet()) {
			Element lengthElement = new Element("length");
			lengthElement.addAttribute(new Attribute("length", ""+entry.getElement().intValue()));
			lengthElement.addAttribute(new Attribute("count", ""+entry.getCount()));
			lengthsElement.appendChild(lengthElement);
		}
		return lengthsElement;
	}
}
