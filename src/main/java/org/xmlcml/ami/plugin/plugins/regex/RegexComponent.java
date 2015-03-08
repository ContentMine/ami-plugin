package org.xmlcml.ami.plugin.plugins.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Text;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** a component of a regular expression
 * 
 * So that people can edit bits without destroying the lot.
 * 
 * @author pm286
 *
 */
public class RegexComponent {

	private static final Logger LOG = Logger.getLogger(RegexComponent.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String COUNT = "count";
	private static final String FIELDS = "fields";
	private static final String WEIGHT = "weight";
	private static final String PATTERN = "pattern";
	private static final String REGEX = "regex";
	private static final String RESULT = "result";
	private static final String RESULTS = "results";
	private static final String CASE = "case";
	public static final String INSENSITIVE = "insensitive";
	public static final String REQUIRED = "required";
	private static final String TITLE = "title";
	private static final String URL = "url";
	private static final String VERSION = "version";
	private static final Double DEFAULT_WEIGHT = 0.5;

	private static final int QUERY_FIELD = 0;
	private static final int PRE_FIELD = 1;
	private static final int WORD_FIELD = 2;
	private static final int POST_FIELD = 3;

	private static final String START_CONTEXT = "(.{0,";
	private static final String END_CONTEXT = "})";

	// ((.{1,50})( ... )\s+(.{1,50}))
	private static String PRE_POST = "\\(\\.\\{\\d+,\\d+\\}\\)(.*)\\\\s\\+\\(\\.\\{\\d+,\\d+\\}\\)";
//	private static String PRE_POST = "\\(\\(\\.\\{\\d+,\\d+\\}\\)(.*)\\\\s\\+\\(\\.\\{\\d+,\\d+\\}\\)\\)";
	private static final Pattern PRE_POST_PATTERN = Pattern.compile(PRE_POST);
	// (...)
	private static String SINGLE_BRACKET = "\\(.*\\)";
	private static final Pattern SINGLE_BRACKET_PATTERN = Pattern.compile(SINGLE_BRACKET);

	private static final String QUERY_FIELD_NAME = "query";
	private static final String PRE_FIELD_NAME = "pre";
	private static final String POST_FIELD_NAME = "post";
	private static final List<String> RESERVED_FIELD_NAMES = new ArrayList<String>(Arrays.asList(new String[]{QUERY_FIELD_NAME, PRE_FIELD_NAME, POST_FIELD_NAME}));
	private static final String[] FIELD_NAMES = {QUERY_FIELD_NAME, PRE_FIELD_NAME, "word", POST_FIELD_NAME};
	
	private Element regexElement;
	private Pattern pattern;
	private Double weight = null;
	private List<String> fieldList;
	private List<NamedGroup> namedGroupList;
	private Integer count;
	private String value;
	private String casex;
	private RegexArgProcessor regexArgProcessor;
	private String title;
	private String word;
	private String fieldsString;
	private CompoundRegex compoundRegex;
	private String centralRegex;

	public RegexComponent(CompoundRegex compoundRegex, RegexArgProcessor regexArgProcessor) {
		this.regexArgProcessor = regexArgProcessor;
		this.compoundRegex = compoundRegex;
		
	}

	void createPatternAndFields() {
		getPattern();
		getOrCreateCase();
		getOrCreateValue();
		getURL();
	}
	
	private void getOrCreateTitleAndUpdateXML() {
		if (title == null) {
			title = regexElement.getAttributeValue(TITLE);
			if (title == null) {
				getOrCreateFieldList();
				List<String> novelFields = new ArrayList<String>();
				for (String field : fieldList) {
					if (!RESERVED_FIELD_NAMES.contains(field)) {
						novelFields.add(field);
					}
				}
				String novel = novelFields.toString();
				novel = novel.replaceAll("[\\[\\]\\s]", "");
				title = novel.replaceAll(",", "_");
			}
			regexElement.addAttribute(new Attribute(TITLE, title));
		}
	}

	void expandAddDefaultsAndVerifyRegex() {
		getOrCreateValue();
		addBracketsAndContexts();
		getOrCreateFieldList();
		getOrCreateTitleAndUpdateXML();
		getOrCreateWeight();
	}


	private void addBracketsAndContexts() {
		if (PRE_POST_PATTERN.matcher(value).matches()) {
			return;
		} else if (SINGLE_BRACKET_PATTERN.matcher(value).matches()) {
			value = addPrePost(value);
		} else {
			value = addPrePost(addSingle(value));
		}
		Text childText = (Text) regexElement.getChild(0);
		childText.setValue(value);
		pattern = null; // reset 
	}

	private String addPrePost(String value) {
		Integer[] contextCounts = regexArgProcessor.getContextCount();
//		return "("+START_CONTEXT+contextCounts[0]+END_CONTEXT+value+"\\s+"+START_CONTEXT+contextCounts[1]+END_CONTEXT+")";
		return START_CONTEXT+contextCounts[0]+END_CONTEXT+value+"\\s+"+START_CONTEXT+contextCounts[1]+END_CONTEXT;
	}

	private String addSingle(String value) {
		return "("+value+")";
	}

	void setElement(Element regexElement) {
		this.regexElement = regexElement;
	}

	public String getOrCreateValue() {
		if (value == null) {
			value = regexElement.getValue();
		}
		return value;
	}

	public String getOrCreateCase() {
		return regexElement.getAttributeValue(CASE);
	}

	public String getCase() {
		if (casex == null) {
			casex = regexElement.getAttributeValue(CASE);
			if (casex == null) {
				casex = INSENSITIVE;
				regexElement.addAttribute(new Attribute(CASE, casex));
			}
		}
		return casex;
	}

	public String getURL() {
		return regexElement.getAttributeValue(URL);
	}

	public List<String> getOrCreateFieldList() {
		if (fieldList == null) {
			fieldList = new ArrayList<String>();
			String fields = regexElement.getAttributeValue(FIELDS);
			boolean hasWord = true;
			if (fields != null) {
				fieldList = new ArrayList<String>(Arrays.asList(fields.split("\\s+")));
				if (fieldList.size() == 4) {
					if (!FIELD_NAMES[1].equals(fieldList.get(1)) ||
						!FIELD_NAMES[3].equals(fieldList.get(3))) {
							throw new RuntimeException("Fields should be [<query_name>]["+FIELD_NAMES[1]+"][<word_name>]["+FIELD_NAMES[3]+"]");
					}
				} else if (fieldList.size() == 1) {
					fieldList.add(FIELD_NAMES[3]);
					fieldList.add(0, FIELD_NAMES[1]);
					fieldList.add(0, FIELD_NAMES[0]);
					LOG.trace(fieldList);
				} else {
//					LOG.debug(value);
					LOG.debug("Unusual fieldList: "+fieldList+" in "+compoundRegex.getTitle()+"; found: "+regexElement.toXML());
					hasWord = false;
				}
			} else {
				createFieldList(createNameFromRegexString());
				LOG.trace(">>>"+fieldList);
			}
			createFieldsFromValueAndUpdateXML();
//			createFieldStringAttributeValue();
		}
		return fieldList;
	}

	private void createFieldStringAttributeValue() {
		fieldsString = fieldList.toString();
		fieldsString = fieldsString.replaceAll("[\\[\\]\\s]", "");
		fieldsString = fieldsString.replaceAll(",", " ");
		LOG.trace("fields=\""+fieldsString+"\"");
	}

	private void createFieldList(String fieldname) {
		fieldList = new ArrayList<String>();
		fieldList.add(FIELD_NAMES[0]);
		fieldList.add(FIELD_NAMES[1]);
		fieldList.add(fieldname);
		fieldList.add(FIELD_NAMES[3]);
	}

	private String createNameFromRegexString() {
		getOrCreateCentralRegex();
		String name = centralRegex.replaceAll("[\\(\\)]", "");
		name = name.replaceAll("(\\\\W|\\s(\\+)?|\\d(\\+)?)", "");
		name=name.toLowerCase();
		return name;
	}

	private List<String> createFieldsFromValueAndUpdateXML() {
		getOrCreateCentralRegex();
		createFieldStringAttributeValue();
		regexElement.addAttribute(new Attribute(FIELDS, fieldsString));
		LOG.trace("???"+regexElement.toXML());
		return fieldList;
	}

	private void getOrCreateCentralRegex() {
		getOrCreateValue();
		if (centralRegex == null) {
			Matcher matcher = PRE_POST_PATTERN.matcher(value);
			if (matcher.matches()) {
				LOG.trace(PRE_POST_PATTERN);
				LOG.trace(matcher.groupCount()+"; "+value);
				centralRegex = matcher.group(1);
				LOG.trace(centralRegex);
			} else {
				throw new RuntimeException("Cannot parse regex as : "+value+"; "+PRE_POST_PATTERN);
			}
		}
	}

	public double getOrCreateWeight() {
		if (weight == null) {
			String w = regexElement.getAttributeValue(WEIGHT);
			if (w != null) {
				try {
					weight = new Double(w);
				} catch (Exception e) {
					throw new RuntimeException("bad weight: "+w);
				}
			} else {
				weight = DEFAULT_WEIGHT;
				regexElement.addAttribute(new Attribute(WEIGHT, String.valueOf(weight)));
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
			if (RegexComponent.INSENSITIVE.equals(getOrCreateCase())) {
				pattern = Pattern.compile(getOrCreateValue(), Pattern.CASE_INSENSITIVE);
			} else {
				pattern = Pattern.compile(getOrCreateValue());
			}
		}
		return pattern;
	}

	MatcherResult searchWithPattern(String value) {
		Pattern pattern = getPattern();
		Matcher matcher = pattern.matcher(value);
		int start = 0;
		count = 0;
		MatcherResult matcherResult = new MatcherResult(fieldList);
		while (matcher.find(start)) {
			matcherResult.captureNextMatch(matcher);
			start = matcher.end();
			count++;
		}
		return matcherResult;
	}
	
	public int getCount() {
		return count;
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
//		sb.append(((pattern == null) ? "NULL" : pattern.toString())+"; ");
//		sb.append(((fieldList == null) ? "NULL" : fieldList.toString())+"; ");
		return sb.toString();
	}

	public Element createElement() {
		/**
		private Element regexElement;
		private Pattern pattern;
		private Double weight = null;
		private List<String> fieldList;
		private List<NamedGroup> namedGroupList;
		private Integer count;
		*/
		
		Element regex = new Element(REGEX);
		if (pattern != null) {
			Element patternElement = new Element(PATTERN);
			patternElement.appendChild(pattern.toString());
			regex.appendChild(patternElement);
		}
		if (weight != null) {
			regex.addAttribute(new Attribute(WEIGHT, String.valueOf(weight)));
		}
		if (fieldList != null) {
			regex.addAttribute(new Attribute(FIELDS, String.valueOf(fieldList)));
		}
		return regex;

	}
	private Element toXMLOld() {
		Element results = new Element(RESULTS);
		LOG.debug("namedGroup size: "+namedGroupList.size());
		for (NamedGroup namedGroup : namedGroupList) {
			Element result = new Element(RESULT);
			String namedGroupValue =namedGroup.getGroup();
			if (namedGroupValue != null) { 
				LOG.debug("named group value: "+namedGroup.getName()+"="+namedGroupValue);
				result.addAttribute(new Attribute(namedGroup.getName(), namedGroupValue));
				results.appendChild(result);
			} else {
				LOG.trace("null named group: "+namedGroup.getName());
			}
		}
		results.addAttribute(new Attribute(COUNT, String.valueOf(count)));
		return results;
	}
}
