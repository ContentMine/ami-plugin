package org.xmlcml.xhtml2stm.visitor.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;

/** a component of a regular expression
 * 
 * So that people can edit bits without destroying the lot.
 * 
 * @author pm286
 *
 */
public class RegexComponent {

	private static final String RESULT = "result";

	private static final Logger LOG = Logger.getLogger(RegexComponent.class);

	private static final String WEIGHT = "weight";
	private static final String CASE = "case";
	public static final String INSENSITIVE = "insensitive";
	public static final String REQUIRED = "required";
	private static final String TITLE = "title";
	private static final String URL = "url";
	private static final String VERSION = "version";
	private static final Double DEFAULT_WEIGHT = 0.1;
	private static final String FIELDS = "fields";
	
	private Element regexElement;
	private Pattern pattern;
	private Double weight = null;
	private List<String> fieldList;
	private List<NamedGroup> namedGroupList;
	private Integer count;

	public RegexComponent() {
		
	}

	/** regex in XML
	 * 
	 * @param regexElement
	 * @return
	 */
	public static RegexComponent createRegexComponent(Element regexElement) {
		RegexComponent regexComponent = new RegexComponent();
		regexComponent.setElement(regexElement);
		regexComponent.createPatternAndFields();
		return regexComponent;
	}
	
	private void createPatternAndFields() {
		getPattern();
		getCase();
		getValue();
		getTitle();
		getURL();
		getFieldList();
		getWeight();
	}
	
	private void setElement(Element regexElement) {
		this.regexElement = regexElement;
	}

	public String getValue() {
		return regexElement == null ? null : regexElement.getValue();
	}

	public String getCase() {
		return regexElement == null ? null : regexElement.getAttributeValue(CASE);
	}

	public String getTitle() {
		return regexElement == null ? null : regexElement.getAttributeValue(TITLE);
	}

	public String getURL() {
		return regexElement == null ? null : regexElement.getAttributeValue(URL);
	}

	public List<String> getFieldList() {
		if (fieldList == null) {
			fieldList = new ArrayList<String>();
			if (regexElement != null) {
				String fields = regexElement.getAttributeValue(FIELDS);
				if (fields != null) {
					fieldList = Arrays.asList(fields.split("\\s+"));
				}
			}
		}
		return fieldList;
	}

	public double getWeight() {
		if (weight == null) {
			String w = regexElement == null ? null : regexElement.getAttributeValue(WEIGHT);
			if (w != null) {
				try {
					weight = new Double(w);
				} catch (Exception e) {
					throw new RuntimeException("bad weight: "+w);
				}
			} else {
				weight = DEFAULT_WEIGHT;
			}
		}
		return weight;
	}
	
	/** get or create Pattern.
	 * 
	 * if null, compiles Pattern, If case="insensitive", ignores case.
	 * 
	 * @return
	 */
	public Pattern getPattern() {
		if (pattern == null) {
			if (RegexComponent.INSENSITIVE.equals(getCase())) {
				pattern = Pattern.compile(getValue(), Pattern.CASE_INSENSITIVE);
			} else {
				pattern = Pattern.compile(getValue());
			}
		}
		return pattern;
	}

	int searchWithPatterns(String value) {
		Pattern pattern = getPattern();
		Matcher matcher = pattern.matcher(value);
		int start = 0;
		count = 0;
		while (matcher.find(start)) {
			captureGroups(matcher, getFieldList());
			start = matcher.end();
			count++;
		}
		return count;
	}
	
	public int getCount() {
		return count;
	}

	private void captureGroups(Matcher matcher, List<String> fieldList) {
		getFieldList();
		ensureNamedGroupList();
		List<String> groupList = extractGroupList(matcher);
		if (groupList.size() > 0 || fieldList.size() > 0) {
			if (groupList.size() != fieldList.size()) {
				LOG.debug("regComp: "+this.toString());
				throw new RuntimeException("groupList ("+groupList.size()+") does not match fieldList ("+fieldList.size()+")");
			} else {
				for (int i = 0; i < groupList.size(); i++) {
					NamedGroup namedGroup = new NamedGroup(fieldList.get(i), groupList.get(i));
					LOG.debug("namedgroup "+namedGroup);
					namedGroupList.add(namedGroup);
				}
			}
		}
	}

	private void ensureNamedGroupList() {
		if (namedGroupList == null) {
			namedGroupList = new ArrayList<NamedGroup>();
		}
	}

	private List<String> extractGroupList(Matcher matcher) {
		List<String> groupList = new ArrayList<String>();
		if (matcher.groupCount() > 0) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				groupList.add(matcher.group(i));
			}
			LOG.trace(groupList);
		}
		return groupList;
	}

	/**
	private Element regexElement;
	private Pattern pattern;
	private Double weight = null;
	private List<String> fieldList;
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(regexElement.toXML()+"; ");
		sb.append(((pattern == null) ? "NULL" : pattern.toString())+"; ");
		sb.append(((fieldList == null) ? "NULL" : fieldList.toString())+"; ");
		return sb.toString();
	}

	public Element toXML() {
		Element result = new Element(RESULT);
		for (NamedGroup namedGroup : namedGroupList) {
			result.addAttribute(new Attribute(namedGroup.getName(), namedGroup.getGroup()));
		}
		result.addAttribute(new Attribute("count", String.valueOf(count)));
		return result;
	}
}
