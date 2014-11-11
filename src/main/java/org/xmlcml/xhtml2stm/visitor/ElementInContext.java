package org.xmlcml.xhtml2stm.visitor;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;

public class ElementInContext {
	
	private final static Logger LOG = Logger.getLogger(ElementInContext.class);

	private static final String PRE = "[[";
	private static final String POST = "]]";
	private static final String SEP = "";
	private static final String DOTS = "...";
	
	private ParentNode parent;
	private Element resultElement;
	private int index;
	private int maxChar;
	private String resultValue;
	private List<String> postStrings;
	private List<String> preStrings;

	private String xPath;

	private ElementInContext() {
		setDefaults();
	}
	
	private void setDefaults() {
		index = -1;
		resultValue = "";
		postStrings = new ArrayList<String>();
		preStrings = new ArrayList<String>();
	}

	public ElementInContext(Element element, int maxChar) {
		this.resultElement = element;
		this.resultValue = element.getValue();
		parent = element.getParent();
		if (parent != null) {
			index = parent.indexOf(element);
			this.maxChar = maxChar;
			computeStrings(); 
			setXPath(getXPathOfResultElement());
		} else {
			setDefaults();
		}
	}

	private void setXPath(String xPath) {
		this.xPath = xPath;
	}

	/** creates EIC with no wrapping strings.
	 * 
	 * @param resultValue
	 */
	public ElementInContext(String resultValue) {
		setDefaults();
		this.resultValue = resultValue;
	}

	/** used when EIC has been made by XPath and then there is further splitting.
	 * 
	 * Part of this is a copy constructor
	 * 
	 * // FIXME this is a mess - 
	 * @param eic
	 * @param value
	 * @param start
	 * @param end
	 * @return
	 */
	public static ElementInContext createNewElementInContext(
			ElementInContext eic, String value, int start, int end) {
		ElementInContext newEic = new ElementInContext();
		newEic.maxChar = eic.maxChar;
		newEic.resultValue = value.substring(start, end);
		newEic.preStrings = new ArrayList<String>(eic.preStrings);
		preTruncate(newEic.preStrings, eic.maxChar);
		newEic.postStrings = new ArrayList<String>(eic.postStrings);
		postTruncate(newEic.postStrings, eic.maxChar);
		newEic.xPath = eic.xPath;
		newEic.resultElement = eic.resultElement;
		LOG.trace("XP "+newEic.xPath);
		return newEic;
	}

	private static void preTruncate(List<String> strings, int maxChar) {
		for (int i = 0; i < strings.size(); i++) {
			String s = strings.get(i);
			if (s.length() > maxChar) {
				s = DOTS+s.substring(s.length() - maxChar);
				strings.set(i, s);
			}
		}
	}

	private static void postTruncate(List<String> strings, int maxChar) {
		for (int i = 0; i < strings.size(); i++) {
			String s = strings.get(i);
			if (s.length() > maxChar) {
				s = s.substring(0, maxChar) + DOTS;
				strings.set(i, s);
			}
		}
	}

	public String getResultValue() {
		return resultValue;
	}

	private void computeStrings() {
		StringBuilder sb = new StringBuilder();
		getOrCreatePrecedingSiblingNodeStrings(); 
		this.resultValue = /*this.resultElement.getLocalName()+": "+*/this.resultElement.getValue();
		getOrCreateFollowingSiblingNodeStrings();
	}

	public List<String> getOrCreatePrecedingSiblingNodeStrings() {
		// these are cached because copies may not carry parent information 
		if (preStrings == null) {
			preStrings = new ArrayList<String>();
			int charCount = 0;
			for (int i = index - 1; i >= 0; i--) {
				String value = parent == null ? resultElement.getValue() : parent.getChild(i).getValue();
				charCount += value.length();
				int delta = charCount - maxChar;
				if (delta > 0) {
					value = value.substring(delta);
					value = DOTS + value;
				}
				preStrings.add(0, value);
				if (delta > 0) break;
			}
			LOG.trace("PRE: "+preStrings);
		}
		return preStrings;
	}

	public List<String> getOrCreateFollowingSiblingNodeStrings() {
		if (postStrings == null) {
			postStrings = new ArrayList<String>();
			int charCount = 0;
			int count = parent == null ? 0 : parent.getChildCount();
			for (int i = index + 1; i < count; i++) {
				String value =  parent == null ? resultElement.getValue() : parent.getChild(i).getValue();
				charCount += value.length();
				int delta = charCount - maxChar;
				if (delta > 0) {
					value = value.substring(0, value.length() - delta);
					value = value + DOTS;
				}
				postStrings.add(value);
				if (delta > 0) break;
			}
		}
		return postStrings;
	}

	public final static String getConcatenatedStrings(List<String> strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append(s);
		}
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < preStrings.size(); i++) {
			String pre = preStrings.get(i);
			sb.append(pre);
			if (i < preStrings.size() - 2) sb.append(SEP);
		}
		sb.append(PRE);
		sb.append(resultValue);
		sb.append(POST);
		for (int i = 0; i < postStrings.size(); i++) {
			String post = postStrings.get(i);
			if (i > 0 && i < postStrings.size() - 1) sb.append(SEP);
			sb.append(post);
		}
		return sb.toString();
	}

	public String getXPathOfResultElement() {
		StringBuilder sb = new StringBuilder();
		addAncestors(sb, resultElement);
		return sb.toString();
	}

	private void addAncestors(StringBuilder sb, Element element) {
		if (element == null) return;
		String name = element.getLocalName();
		StringBuilder sb1 = new StringBuilder();
//		sb1.append("/*[local-name()='"+name+"'"); // forget namespaces for now
		sb1.append("/"+name);
		Attribute attribute = getFirstUsefulAttribute(element);
		if (attribute != null) {
			sb1.append("[@"+attribute.getLocalName()+"='"+attribute.getValue()+"']");
		} else {
			int ordinal = getOrdinalOfChildWithName(element, name);
			sb1.append("["+ordinal+"]");
		}
		sb.insert(0, sb1.toString());
		ParentNode parent = element.getParent();
		if (parent != null && parent instanceof Element) {
			addAncestors(sb, (Element) parent);
		}
	}

	private int getOrdinalOfChildWithName(Element element, String name) {
		int ordinal = 1;  // we count from 1 in XPath
		ParentNode parent = element.getParent();
		if (parent != null) {
			int position = parent.indexOf(element);
			for (int i = 0; i < position; i++) {
				Node sibling = parent.getChild(i);
				if (sibling instanceof Element && name.equals(((Element)sibling).getLocalName())) {
					ordinal++;
				}
			}
		}
		return ordinal;
	}

	private Attribute getFirstUsefulAttribute(Element element) {
		Attribute attribute = element.getAttribute("id");
		if (attribute == null) {
			attribute = element.getAttribute("name");
		}
		// not worth trying to find meaningful attributes
//		if (attribute == null) {
//			for (int i = 0; i < element.getAttributeCount(); i++) {
//				Attribute attribute0 = element.getAttribute(i);
//				if (attribute0.getLocalName().equals("lang")) continue;
//				if (attribute0.getLocalName().equals("dtd-version")) continue;
//				attribute = attribute0;
//			}
//		}
		return attribute;
	}

	public void setResultValue(String value) {
		this.resultValue = value;
	}

	public Element getResultElement() {
		return resultElement;
	}
}
