package org.xmlcml.ami.plugin.plugins.regex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.files.EuclidSource;

/** Container for many smaller regexes.
 * 
 * CompoundRegex holds many smaller regexes which can either be combined into a humungous regex
 * or exposed as a list for iteration
 * 
 * @author pm286
 *
 */
public class CompoundRegex {

	private static final String TITLE = "title";

	private final static Logger LOG = Logger.getLogger(CompoundRegex.class);
	
	private static final String XML = ".xml";
	private static final String REGEX_OR = "|";
	private static final String REGEX_CLOSE = ")";
	private static final String REGEX_OPEN = "(";
	private static final String COMPOUND_REGEX = "compoundRegex";
	private static final String REGEX = "regex";
	
	private StringBuilder stringBuilder;
	private List<RegexComponent> regexComponentList;
	private Element root;
	private String title;

	public CompoundRegex(Element rootElement) {
		this.root = rootElement;
	}

	/** creates a regex from file if possible
	 * 
	 * Looks at *.xml
	 * 
	 * @param file
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	public static CompoundRegex readAndCreateRegex(File file) throws IOException {
		return CompoundRegex.readAndCreateRegex(new FileInputStream(file));
	}
	
	/** creates a regex from URL if possible
	 * 	 * 
	 * @param file
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	public static CompoundRegex readAndCreateRegex(URL url) throws IOException{
		return CompoundRegex.readAndCreateRegex(url.openStream());
	}

	/** creates a regex from name if possible
	 * 	 * 
	 * @param name could be  resourceName, file or URL
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	public static CompoundRegex readAndCreateRegex(String name) {
		return CompoundRegex.readAndCreateRegex(EuclidSource.getInputStream(name));
	}

	/** creates a regex from InputStream if possible
	 * 	 * 
	 * @param file
	 * @return null if not a regex file
	 * @exception RuntimeException if cannot read/parse
	 */
	public static CompoundRegex readAndCreateRegex(InputStream is) {
		CompoundRegex compoundRegex = null;
		Element rootElement = null;
		try {
			Document doc = new Builder().build(is);
			rootElement = doc.getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Cannot read or parse regexInputStream", e);
		}
		compoundRegex = addCompoundRegex(rootElement);
		return compoundRegex;
	}

	private static CompoundRegex addCompoundRegex(Element rootElement) {
		CompoundRegex compoundRegex;
		compoundRegex = new CompoundRegex(rootElement);
		List<RegexComponent> regexComponentList = compoundRegex.createRegexComponentList();
		if (regexComponentList.size() > 0) {
			compoundRegex.add(regexComponentList);
		}
		LOG.trace("read Compound Regex: "+rootElement.getAttributeValue(TITLE)+"\n"+compoundRegex);
		return compoundRegex;
	}

	private void add(List<RegexComponent> regexComponentList) {
		this.regexComponentList = regexComponentList;
		if (regexComponentList != null && regexComponentList.size() > 0) {
			stringBuilder = new StringBuilder(REGEX_OPEN);
			int serial = 0;
			for (RegexComponent regexComponent : regexComponentList) {
				add(regexComponent, serial);
				serial++;
			}
			stringBuilder.append(REGEX_CLOSE);
		}
	}

	private void add(RegexComponent regexComponent, int serial) {
		String value = regexComponent.getValue();
		if (serial >0) {
			stringBuilder.append(REGEX_OR);
		}
		stringBuilder.append(REGEX_OPEN+value+REGEX_CLOSE);
	}

	private List<RegexComponent> createRegexComponentList() {
		List<RegexComponent> regexComponentList = new ArrayList<RegexComponent>();
		if (COMPOUND_REGEX.equals(root.getLocalName())) {
			for (int i = 0; i < root.getChildElements().size(); i++) {
				Element regexElement = root.getChildElements().get(i);
				RegexComponent regexComponent = RegexComponent.createRegexComponent(regexElement);
				if (regexComponent != null) {
					regexComponentList.add(regexComponent);
				}
			}
		}
		
		return regexComponentList;
	}

	public String getValue() {
		return stringBuilder == null ? null : stringBuilder.toString();
	}

	public List<String> getRegexValues() {
		List<String> valueList = new ArrayList<String>();
		for (RegexComponent regexComponent : regexComponentList) {
			valueList.add(regexComponent.getValue());
		}
		return valueList;
	}

	/** a list of each subRegex
	 * 
	 * @return
	 */
	public List<RegexComponent> getRegexComponentList() {
		return regexComponentList;
	}

	public String getTitle() {
		return root == null ? null : root.getAttributeValue(TITLE);
	}

	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (RegexComponent component : regexComponentList) {
			sb.append(component.toString()+" ");
		}
		return sb.toString();
	}
	
}
