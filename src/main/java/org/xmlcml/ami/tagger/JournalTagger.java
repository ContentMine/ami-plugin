package org.xmlcml.ami.tagger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

/** reads a set of tag definitions and uses them to tag an Element and later search it.
 * 
 * @author pm286
 *
 */
public abstract class JournalTagger {

	public static final String ATTRIBUTE_TAG= "tag";
	public static final String JOURNAL = "journal";
	public static final String TAG_DEFINITION_NAME = "name";

	private final static Logger LOG = Logger.getLogger(JournalTagger.class);
	
	protected static final File AMI_DIR = new File("src/main/resources/org/xmlcml/ami");
	
	private static final String OR = " | ";
	private static final String OR1 = " or ";
	private static final String AND = " and ";
	private static final String NOT = "not";
	public static final String TAG = "tag";

	/** all the sections we might tag.
	 * 
	 */
	public static final String ABSTRACT = "abstract";
	public static final String ACKNOWLEDGEMENTS = "acknowledge";
	public static final String ADDITONALFILES = "additionfiles";
	public static final String AUTHORCONTRIB = "authorcontrib";
	public static final String BACKGROUND = "background";
	public static final String COMPETING = "competing";
	public static final String CONCLUSION = "conclusion";
	public static final String DISCUSSION = "discussion";
	public static final String METHODS = "methods";
	public static final String REFERENCES = "references";
	public static final String RESULTS = "results";

	public static final String[] TAGS = {
		ABSTRACT,
		ACKNOWLEDGEMENTS,
		ADDITONALFILES,
		AUTHORCONTRIB,
		BACKGROUND,
		COMPETING,
		CONCLUSION,
		DISCUSSION,
		METHODS,
		REFERENCES,
		RESULTS,
		
		
		};
	protected static final File TAGGER_DIR = new File(AMI_DIR, "tagger");


	protected File tagDefinitionFile;
	private Element tagDefinitionsElement;
	public Element tagDefinitions;
	
	protected JournalTagger(File tagDefinitionFile) {
		readAndParseTagDefinitionFile(tagDefinitionFile);
	}

	private void readAndParseTagDefinitionFile(File tagDefinitionFile) {
		this.tagDefinitionFile = tagDefinitionFile;
		tagDefinitionsElement = XMLUtil.parseQuietlyToDocument(tagDefinitionFile).getRootElement();
	}

	protected String getXpathForTag(String tagName) {
		List<Element> tagElements = getTagDefinitionForName(tagName);
		return (tagElements.size() != 1) ? null : tagElements.get(0).getValue();
	}

	private List<Element> getTagDefinitionList() {
		List<Element> tagElements = XMLUtil.getQueryElements(tagDefinitionsElement, "*[local-name()='"+TAG+"']");
		return tagElements;
	}

	/** return all tags with given name.
	 * 
	 * should only be one or none.
	 * 
	 * @param tagName
	 * @return
	 */
	private List<Element> getTagDefinitionForName(String tagName) {
		List<Element> tagElements = XMLUtil.getQueryElements(tagDefinitionsElement, "*[local-name()='"+TAG+"' and @name='"+tagName+"']");
		return tagElements;
	}
	
	protected List<String> getAllTagNames() {
		List<Element> tagElements = getTagDefinitionList();
		List<String> tagNames = new ArrayList<String>();
		for (Element tagElement : tagElements) {
			String tagName = tagElement.getAttributeValue(TAG_DEFINITION_NAME);
			tagNames.add(tagName);
		}
		return tagNames;
	}
	
//	/** query a set of sections.
//	 * 
//	 * @param element
//	 * @param sectionNameList
//	 * @param not
//	 * @return
//	 */
//	private List<Element> queryDocument(Element element,String[] sectionNames, String not) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < sectionNames.length; i++) {
//			String xpath = getXpathForTag(sectionNames[i]);
//			if (i > 0) {
//				sb.append(AND);
//			}
//			sb.append("not (");
//			sb.append(xpath);
//			sb.append(")");
//		}
//		String xpath = sb.toString();
//		LOG.debug("xpath "+xpath);
//		List<Element> elements = XMLUtil.getQueryElements(element, xpath);
//		return elements;
//	}

	/** query with a single section name.
	 * 
	 * @param element
	 * @param tagName
	 * @return list of sections
	 */
	public List<Element> findSectionsFromTagDefinitions(Element element, String tagName) {
		String xpath = getXpathForTag(tagName);
		if (xpath == null) {
			throw new RuntimeException("Cannot find xpath definition for: "+tagName);
		}
		List<Element> elements = XMLUtil.getQueryElements(element, xpath);
		return elements;
	}
	
	public void addTagsToSections(Element element) {
		List<String> tagNames = getAllTagNames();
		for (String tagName : tagNames) {
			List<Element> sections = findSectionsFromTagDefinitions(element, tagName);
			for (Element section : sections) {
				addTag(tagName, section);
			}
		}
	}

	private void addTag(String tagName, Element element) {
		Attribute attribute = new Attribute(ATTRIBUTE_TAG, /*getJournalId()+":"+*/tagName);
		element.addAttribute(attribute);
	}

	public String getJournalId() {
		return tagDefinitionsElement == null ? null : tagDefinitionsElement.getAttributeValue(JOURNAL);
	}
	
//	/** return all sections in list.
//	 * 
//	 * @param element
//	 * @param sectionNames
//	 * @return
//	 */
//	public List<Element> querySectionsIncluding(Element element, String[] sectionNames) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < sectionNames.length; i++) {
//			String xpath = getXpathForTag(sectionNames[i]);
//			if (i > 0) {
//				sb.append(OR);
//			}
//			sb.append("(");
//			sb.append(xpath);
//			sb.append(")");
//		}
//		String xpath = sb.toString();
//		LOG.debug("xpath "+xpath);
//		List<Element> elements = XMLUtil.getQueryElements(element, xpath);
//		return elements;
//	}

//	/** return all sections except those in list.
//	 * 
//	 * @param element
//	 * @param sectionNames
//	 * @return
//	 */
//	@Deprecated // doesn't work yet
//	public List<Element> querySectionsExcept(Element element, String[] sectionNames) {
//		StringBuilder sb = new StringBuilder();
////		sb.append("*[not(");
//		for (int i = 0; i < sectionNames.length; i++) {
//			String xpath = getXpathForTag(sectionNames[i]);
//			if (i > 0) {
//				sb.append(OR1);
//			}
//			sb.append("(");
//			sb.append(xpath);
//			sb.append(")");
//		}
////		sb.append(")]");
//		String xpath = sb.toString();
////		xpath = "(//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and  not (.='Abstract' or .='References' or .='Methods')]])";
//		LOG.debug("xpath "+xpath);
//		List<Element> elements = XMLUtil.getQueryElements(element, xpath);
//		return elements;
//	}

}
