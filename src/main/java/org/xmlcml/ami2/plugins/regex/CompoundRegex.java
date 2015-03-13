package org.xmlcml.ami2.plugins.regex;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;

/** Container for many smaller regexes.
 * 
 * CompoundRegex holds many smaller regexes which can either be combined into a humungous regex
 * or exposed as a list for iteration
 * 
 * @author pm286
 *
 */
public class CompoundRegex {

	
	private static final Logger LOG = Logger.getLogger(CompoundRegex.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String COMPOUND_REGEX = "compoundRegex";
	private static final String REGEX_OR = "|";
	private static final String REGEX_CLOSE = ")";
	private static final String REGEX_OPEN = "(";
	private static final String TITLE = "title";
	
	private List<RegexComponent> regexComponentList;
	private Element root;
	private String title;
	private AMIArgProcessor regexArgProcessor;

	public CompoundRegex(AMIArgProcessor regexArgProcessor, Element rootElement) {
		this.root = rootElement;
		this.regexArgProcessor = regexArgProcessor;
		this.title = rootElement.getAttributeValue(TITLE);
	}

	public List<String> getRegexValues() {
		getOrCreateRegexComponentList();
		List<String> valueList = new ArrayList<String>();
		for (RegexComponent regexComponent : regexComponentList) {
			valueList.add(regexComponent.getOrCreateValue());
		}
		return valueList;
	}

	public String getTitle() {
		return title;
	}

	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder();
		getOrCreateRegexComponentList();
		for (RegexComponent component : regexComponentList) {
			sb.append(component.toString()+"\n");
		}
		return sb.toString();
	}

	// make combined patternString (experimental still) 

	List<RegexComponent> getOrCreateRegexComponentList() {
		if (regexComponentList == null) {
			regexComponentList = new ArrayList<RegexComponent>();
			if (COMPOUND_REGEX.equals(root.getLocalName())) {
				createRegexElementsAndAddToThis();
			} else {
				throw new RuntimeException("regex file does not have root: "+COMPOUND_REGEX);
			}
		}
		return regexComponentList;
	}

	private void createRegexElementsAndAddToThis() {
		for (int i = 0; i < root.getChildElements().size(); i++) {
			Element regexElement = root.getChildElements().get(i);
			RegexComponent regexComponent = this.createRegexComponent(regexArgProcessor, regexElement);
			if (regexComponent != null) {
				regexComponentList.add(regexComponent);
			}
		}
	}

	/** experimental generation of complete pattern
	 * 
	 * @return
	 */
	// FIXME - needs calling
	public String generateCombinedPatternString() {
		getOrCreateRegexComponentList();
		StringBuilder stringBuilder = null;
		if (regexComponentList.size() > 0) {
			stringBuilder = new StringBuilder(REGEX_OPEN);
			int serial = 0;
			for (RegexComponent regexComponent : regexComponentList) {
				stringBuilder.append(addRegexToCombinedPattern(regexComponent, serial));
				serial++;
			}
			stringBuilder.append(REGEX_CLOSE);
		}
		return stringBuilder == null ? null : stringBuilder.toString();
	}

	private String addRegexToCombinedPattern(RegexComponent regexComponent, int serial) {
		String valuex = (serial == 0) ? "" :REGEX_OR;
		valuex += REGEX_OPEN + regexComponent.getOrCreateValue() + REGEX_CLOSE;
		return valuex;
	}

	/** regex in XML
	 * 
	 * @param regexArgProcessor TODO
	 * @param regexElement
	 * @return
	 */
	public RegexComponent createRegexComponent(AMIArgProcessor regexArgProcessor, Element regexElement) {
		RegexComponent regexComponent = null;
		if (regexElement != null) {
			regexComponent = new RegexComponent(this, regexArgProcessor);
			regexComponent.setElement(regexElement);
			regexComponent.createPatternAndFields();
			regexComponent.expandAddDefaultsAndVerifyRegex();
		}
		return regexComponent;
	}


}
