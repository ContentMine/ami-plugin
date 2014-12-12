package org.xmlcml.ami.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.ParentNode;

import org.apache.log4j.Logger;
import org.xmlcml.ami.util.AMIUtil;

import boofcv.alg.feature.associate.EnsureUniqueAssociation;

/** entity (or Element) in context.
 * 
 * An object which wraps a document or text to be searched and accumulates some of the immediate context.
 * 
 * @author pm286
 *
 */
public class EIC {

	private final static Logger LOG = Logger.getLogger(EIC.class);

	private static final String PRE = "[[";
	private static final String POST = "]]";
	private static final String SEP = "|";
	private static final String DOTS = "...";

	private static final String POST_TAG = "post";
	private static final String PRE_TAG = "pre";
	public static final String VALUE_TAG = "value";

	private static final int DEFAULT_MAX_CHAR = 50;
	private static final int MAX_HITS = 50;

	/** parent (if any) of xmlElement */
	private ParentNode parent;
	private Element xmlElement;
	private int index;
	private int maxChar;
	private String stringValue;
	private List<String> postStrings;
	private List<String> preStrings;
	private String xPath;
	private Integer lineNumberFromZero = null;
	private String lineValue;
	private String remaining;

	private EIC() {
		setDefaults();
	}

	private void setDefaults() {
		index = -1;
		stringValue = "";
		postStrings = new ArrayList<String>();
		preStrings = new ArrayList<String>();
		maxChar = 80;
	}

	public EIC(Element element) {
		this();
		this.xmlElement = element;
		parent = element.getParent();
		if (parent != null) {
			index = parent.indexOf(element);
			computePrecedingOrFollowingStrings();
			setXPath(getXPathOfResultElement());
		} else {
			setDefaults();
		}
		this.stringValue = element.getValue();
	}

	public EIC(String value, int maxChar) {
		setDefaults();
		this.maxChar = maxChar;
		this.stringValue = value;
	}

	
	private void setXPath(String xPath) {
		this.xPath = xPath;
	}

	/**
	 * creates EIC with no wrapping strings.
	 * 
	 * @param stringValue
	 */
	public EIC(String stringValue) {
		setDefaults();
		this.stringValue = stringValue;
	}

	/** creates EIV from given line of list.
	 * 
	 * @param lineList
	 * @param iline line (count from zero)
	 */
	public EIC(List<String> lineList, int iline) {
		this(lineList.get(iline));
		this.setLineNumberFromZero(iline);
		this.setLineValue(stringValue);
	}

	private void setLineValue(String lineValue) {
		this.lineValue = lineValue;
	}

	public String getLineValue() {
		return lineValue;
	}

	private void setLineNumberFromZero(int iline) {
		this.lineNumberFromZero = iline;
	}

	/**
	 * get remaining string to search.
	 * 
	 * the remaining string should be the substring after the matched region.
	 * 
	 * @return empty string ("") if no further string to match
	 */
	public String getRemainingUnmatchedString() {
		if (remaining == null) {
			if (postStrings.size() > 0) {
				remaining = postStrings.get(0);
			}
		}
		return remaining;
	}

	/**
	 * used when EIC has been made by XPath and then there is further splitting.
	 * 
	 * Part of this is a copy constructor
	 * 
	 * // FIXME this is a mess -
	 * 
	 * @param eic
	 * @param value
	 * @param start
	 * @param end
	 * @return
	 */
	public EIC createNewEIC(String value, int start, int end) {
		EIC newEic = new EIC();
		newEic.remaining = ((value == null) ? null : value.substring(end));
		newEic.maxChar = this.maxChar;
		newEic.stringValue = value.substring(start, end);
		newEic.preStrings = new ArrayList<String>(this.preStrings);
		preTruncate(newEic.preStrings, this.maxChar);
		// append string snippet following matched region
		if (start > 0) {
			newEic.preStrings.add(value.substring(0, start));
		}
		newEic.postStrings = new ArrayList<String>(this.postStrings);
		postTruncate(newEic.postStrings, this.maxChar);
		// append string snippet preceeding matched region
		if (end < value.length() - 1) {
			newEic.postStrings.add(0, value.substring(end));
		}
		newEic.xPath = this.xPath;
		newEic.xmlElement = this.xmlElement;
		newEic.lineNumberFromZero = this.lineNumberFromZero;
		this.computePrecedingOrFollowingStrings();
		return newEic;
	}

	/** very crude since non-matches are repeatedly searched.
	 * 
	 * @param patternList
	 * @return
	 */
	public DocumentFragment findStrings(List<Pattern> patternList) {
		if (patternList == null) {
			throw new RuntimeException("null patternList");
		}
		DocumentFragment resultList = new DocumentFragment();
		Element eicelem = this.getResultElement();
		String value = eicelem == null ? this.getResultValue() : eicelem.getValue();
		boolean found = true;
		int i = 0;
		while (found && value != null) {
			Matcher matcher = findLowestMatcher(patternList, value);
			if (matcher == null) {
				break;
			}
			EIC newEic = this.createNewEIC(value, matcher.start(), matcher.end());
			LOG.trace("M "+matcher.start()+"; "+matcher.end());
			resultList.add(newEic);
			value = value.substring(matcher.end());
//			value = newEic.getRemainingUnmatchedString();
			LOG.trace("matched : " + newEic + ": " + value);
			if (i++ > MAX_HITS) {
				LOG.error("force break from infinite loop: "+value);
				break;
			}
		}
		LOG.trace("RESULT "+resultList.getEICList().size());
		return resultList;
	}

	/** matcher whose start position is lowest.
	 * 
	 * @param patternList patterns to compare
	 * @param value to test
	 * @return
	 */
	private Matcher findLowestMatcher(List<Pattern> patternList, String value) {
		// find lowest match
		Matcher matcher = null;
		int lowestStart = Integer.MAX_VALUE;
		for (Pattern pattern : patternList) {
			Matcher matcher0 = pattern.matcher(value);
			if (matcher0.find()) {
				if (matcher0.start() < lowestStart) {
					matcher = matcher0;
					lowestStart = matcher0.start();
				}
			}
		}
		return matcher;
	}

	public DocumentFragment findStrings(Pattern pattern) {
		DocumentFragment resultList = new DocumentFragment();
		Element eicelem = this.getResultElement();
		String value = eicelem == null ? this.getResultValue() : eicelem.getValue();
		boolean found = true;
		int i = 0;
		while (found && value != null) {
			Matcher matcher = pattern.matcher(value);
			if (!matcher.find()) {
				break;
			}
			EIC newEic = this.createNewEIC(value, matcher.start(), matcher.end());
			resultList.add(newEic);
			value = newEic.getRemainingUnmatchedString();
			LOG.trace("matched : " + newEic + ": " + value+" // "+matcher.start()+" // "+ matcher.end());
			if (i++ > MAX_HITS) {
				LOG.error("force break from infinite loop: "+value);
				break;
			}
		}
		LOG.trace("RESULT "+resultList.getEICList().size());
		return resultList;
	}

	private static void preTruncate(List<String> strings, int maxChar) {

		for (int i = 0; i < strings.size(); i++) {
			String s = strings.get(i);
			if (s.length() > maxChar) {
				s = DOTS + s.substring(s.length() - maxChar);
				strings.set(i, s);
			}
		}
		LOG.trace("PRE>"
				+ ((strings.size() == 0) ? "NULL"
						: strings.get(strings.size() - 1)));
	}

	private static void postTruncate(List<String> strings, int maxChar) {
		for (int i = 0; i < strings.size(); i++) {
			LOG.trace("POST>" + strings.get(i));
			String s = strings.get(i);
			if (s.length() > maxChar) {
				s = s.substring(0, maxChar) + DOTS;
				strings.set(i, s);
			}
		}
		LOG.trace("POST>" + ((strings.size() == 0) ? "NULL" : strings.get(0)));
	}

	public String getResultValue() {
		return stringValue;
	}

	/**
	 * compute strings either side of value.
	 * 
	 */
	private void computePrecedingOrFollowingStrings() {
		StringBuilder sb = new StringBuilder();
		createPrecedingSiblingNodeStrings();
		createFollowingSiblingNodeStrings();
	}

	private List<String> getOrCreatePrecedingSiblingNodeStrings() {
		// these are cached because copies may not carry parent information
		if (preStrings == null) {
			createPrecedingSiblingNodeStrings();
		}
		return preStrings;
	}

	private void createPrecedingSiblingNodeStrings() {
		preStrings = new ArrayList<String>();
		int charCount = 0;
		for (int i = index - 1; i >= 0; i--) {
			String value = parent == null ? xmlElement.getValue() : parent
					.getChild(i).getValue();
			charCount += value.length();
			int delta = charCount - maxChar;
			if (delta > 0) {
				value = value.substring(delta);
				value = DOTS + value;
			}
			preStrings.add(0, value);
			if (delta > 0)
				break;
		}
		LOG.trace("PRE: " + preStrings);
	}

	private List<String> getOrCreateFollowingSiblingNodeStrings() {
		if (postStrings == null) {
			createFollowingSiblingNodeStrings();
		}
		return postStrings;
	}

	private void createFollowingSiblingNodeStrings() {
		postStrings = new ArrayList<String>();
		int charCount = 0;
		int count = parent == null ? 0 : parent.getChildCount();
		for (int i = index + 1; i < count; i++) {
			String value = parent == null ? xmlElement.getValue() : parent
					.getChild(i).getValue();
			charCount += value.length();
			int delta = charCount - maxChar;
			if (delta > 0) {
				value = value.substring(0, value.length() - delta);
				value = value + DOTS;
			}
			postStrings.add(value);
			if (delta > 0)
				break;
		}
	}

	private final String getConcatenatedStrings(List<String> strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * private ParentNode parent; private Element xmlElement; private int index;
	 * private int maxChar; private String stringValue; private List<String>
	 * postStrings; private List<String> preStrings; private String xPath;
	 * private Integer lineNumberFromZero = null; private String lineValue;
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		// sb.append("parent:" + (parent == null ? "" : parent.get()));
		// sb.append("line:" + (lineNumberFromZero == null ? "" :
		// lineNumberFromZero));
		for (int i = 0; i < preStrings.size(); i++) {
			sb.append(preStrings.get(i));
			if (i < preStrings.size() - 1) {
				sb.append(SEP);
			}
		}
		sb.append(PRE);
		sb.append(stringValue);
		sb.append(POST);
		for (int i = 0; i < postStrings.size(); i++) {
			if (i > 0 && i < postStrings.size()) {
				sb.append(SEP);
			}
			sb.append(postStrings.get(i));
		}
		return sb.toString();
	}

    public String getXPathOfResultElement() {
		StringBuilder sb = new StringBuilder();
		addAncestors(sb, xmlElement);
		return sb.toString();
	}

	private void addAncestors(StringBuilder sb, Element element) {
		if (element == null)
			return;
		String name = element.getLocalName();
		StringBuilder sb1 = new StringBuilder();
		sb1.append("/" + name);
		Attribute attribute = getFirstUsefulAttribute(element);
		if (attribute != null) {
			sb1.append("[@" + attribute.getLocalName() + "='"
					+ attribute.getValue() + "']");
		} else {
			int ordinal = getOrdinalOfChildWithName(element, name);
			sb1.append("[" + ordinal + "]");
		}
		sb.insert(0, sb1.toString());
		ParentNode parent = element.getParent();
		if (parent != null && parent instanceof Element) {
			addAncestors(sb, (Element) parent);
		}
	}

	private int getOrdinalOfChildWithName(Element element, String name) {
		int ordinal = 1; // we count from 1 in XPath
		ParentNode parent = element.getParent();
		if (parent != null) {
			int position = parent.indexOf(element);
			for (int i = 0; i < position; i++) {
				Node sibling = parent.getChild(i);
				if (sibling instanceof Element
						&& name.equals(((Element) sibling).getLocalName())) {
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
		return attribute;
	}

	public void setResultValue(String value) {
		this.stringValue = value;
	}

	public Element getResultElement() {
		return xmlElement;
	}

	private String createPreConcatenatedString() {
		return getConcatenatedStrings(getOrCreatePrecedingSiblingNodeStrings());
	}

	private String createPostConcatenatedString() {
		return getConcatenatedStrings(getOrCreateFollowingSiblingNodeStrings());
	}

	public Element createPREElement() {
		Element pre = new Element(PRE_TAG);
		pre.appendChild(this.createPreConcatenatedString());
		return pre;
	}

	public Element createPOSTElement() {
		Element post = new Element(POST_TAG);
		post.appendChild(this.createPostConcatenatedString());
		return post;
	}

	private Element createVALUEElement() {
		Element value = new Element(VALUE_TAG);
		value.appendChild(stringValue);
		return value;
	}

	public Element createElement() {
		Element eic = new Element("eic");
		eic.addAttribute(new Attribute(AMIUtil.XPATH, this
				.getXPathOfResultElement()));
		eic.appendChild(createPREElement());
		eic.appendChild(createVALUEElement());
		eic.appendChild(createPOSTElement());
		return eic;
	}

	public Integer getLineNumber() {
		return lineNumberFromZero;
	}
}
