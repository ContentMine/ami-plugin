package org.xmlcml.ami.tagger.bmc;

import java.io.File;
import java.util.List;

import nu.xom.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.tagger.JournalTagger;
import org.xmlcml.xml.XMLUtil;

public class BMCTaggerTest {

	public Element BMC_188_ROOT;
	
	@Before
	public void setup() {
		File file = new File(Fixtures.BMC_SECTIONS, "1471-2229.14.188.htm");
		BMC_188_ROOT = XMLUtil.parseQuietlyToDocument(file).getRootElement();
	}
	
	@Test
	public void testBMCSections() {
		List<Element> divs = XMLUtil.getQueryElements(BMC_188_ROOT, "//*[local-name()='div']");
		Assert.assertEquals("divs", 86, divs.size());
		List<Element> sections = XMLUtil.getQueryElements(BMC_188_ROOT, ""
				+ "//*[local-name()='div']/"
				+ "*[local-name()='section']");
		Assert.assertEquals("sections", 13, sections.size());
		List<Element> divs2 = XMLUtil.getQueryElements(BMC_188_ROOT, ""
				+ "//*[local-name()='div']/"
				+ "*[local-name()='section']/"
				+ "*[local-name()='div']");
		Assert.assertEquals("divs2", 13, divs2.size());
		List<Element> h3 = XMLUtil.getQueryElements(BMC_188_ROOT, ""
				+ "//*[local-name()='div']/"
				+ "*[local-name()='section']/"
				+ "*[local-name()='h3']");
		Assert.assertEquals("h3", 11, h3.size());
		List<Element> abs = XMLUtil.getQueryElements(BMC_188_ROOT, ""
				+ "//*[local-name()='div']/"
				+ "*[local-name()='section']/"
				+ "*[local-name()='h3' and .='Abstract']");
		Assert.assertEquals("abstract", 1, abs.size());
		List<Element> divs3 = XMLUtil.getQueryElements(BMC_188_ROOT, ""
				+ "//*[local-name()='div']/"
				+ "*[local-name()='section' and "
				+ "*[local-name()='h3' and .='Abstract']]/"
				+ "*[local-name()='div']");
		Assert.assertEquals("divs3", 1, divs3.size());
		List<Element> abstractPs = XMLUtil.getQueryElements(BMC_188_ROOT, ""
				+ "//*[local-name()='div']/"
				+ "*[local-name()='section' and "
				+ "*[local-name()='h3' and .='Abstract']]/"
				+ "*[local-name()='div']/"
				+ "*[local-name()='p']");
		Assert.assertEquals("abstractPs", 3, abstractPs.size());
	}
	
	@Test
	public void testMajorSections() {
		Assert.assertEquals("abs", 1,      XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='Abstract']]").size());
		Assert.assertEquals("back", 1,     XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='Background']]").size());
		Assert.assertEquals("res", 1,      XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='Results']]").size());
		Assert.assertEquals("disc", 1,     XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='Discussion']]").size());
		Assert.assertEquals("conc", 1,     XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='Conclusion']]").size());
		Assert.assertEquals("meth", 1,     XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='Methods']]").size());
		Assert.assertEquals("compete", 1,  XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='Competing interests']]").size());
		// can't get the Xpath to work with a &#39 - I must be rusty
		Assert.assertEquals("authors", 1,  XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and contains(.,'Authors') and contains(.,'contributions')]]").size());
		Assert.assertEquals("addfiles", 1, XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='Additional files']]").size());
		Assert.assertEquals("ack", 1,      XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='Acknowledgements']]").size());
		Assert.assertEquals("ref", 1,      XMLUtil.getQueryElements(BMC_188_ROOT, 
				"//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='References']]").size());
	}
	
	@Test
	public void testReadBMCSections() {
		Element sections = XMLUtil.parseQuietlyToDocument(BMCTagger.BMC_TAGDEFINITIONS_FILE).getRootElement();
		List<Element> sectionList = XMLUtil.getQueryElements(sections, "*[local-name()='"+JournalTagger.TAG+"' and @name='abstract']");
		Assert.assertEquals("sections", 1, sectionList.size());
		// check case sensitivity fails
		sections = XMLUtil.parseQuietlyToDocument(BMCTagger.BMC_TAGDEFINITIONS_FILE).getRootElement();
		sectionList = XMLUtil.getQueryElements(sections, "*[local-name()='section' and @name='Abstract']");
		Assert.assertEquals("sections", 0, sectionList.size());
	}

	@Test
	@Ignore // use HTML tagger
	public void testGetXpath() {
		Element sections = XMLUtil.parseQuietlyToDocument(BMCTagger.BMC_TAGDEFINITIONS_FILE).getRootElement();
		String xpath = XMLUtil.getQueryElements(sections, "*[local-name()='"+JournalTagger.TAG+"' and @name='abstract']").get(0).getValue();
		Assert.assertEquals("abstract", "//*[local-name()='div']/*[local-name()='section' and *[local-name()='h3' and .='Abstract']]/*[local-name()='div']", xpath);
	}

	@Test
	@Ignore // use HTML tagger
	public void testSearchBMCDocument() {
		JournalTagger bmcTagger = new BMCTagger();
		List<Element> abstracts = bmcTagger.findSectionsFromTagDefinitions(BMC_188_ROOT, "abstract");
		Assert.assertEquals("abs", 1, abstracts.size());
		Element abstractx = abstracts.get(0);
		List<Element> paras = XMLUtil.getQueryElements(abstractx, "*[local-name()='p']");
		Assert.assertEquals("paras", 3, paras.size());
	}

//	@Test
//	public void testSearchBMCDocumentForSections() {
//		JournalTagger bmcTagger = new BMCTagger();
//		List<Element> abstracts = bmcTagger.querySectionsIncluding(BMC_188_ROOT, new String[]{"abstract", "references", "methods"});
//		Assert.assertEquals("abs", 3, abstracts.size());
//		List<Element> paras = XMLUtil.getQueryElements(abstracts.get(0), "*[local-name()='div']/*[local-name()='p']");
//		Assert.assertEquals("paras", 3, paras.size());
//	}

//	@Test
//	public void testSearchBMCDocumentForNonSections() {
//		JournalTagger bmcTagger = new BMCTagger();
//		List<Element> abstracts = bmcTagger.querySectionsExcept(BMC_188_ROOT, new String[]{"abstract", "references", "methods"});
//		Assert.assertEquals("abs", 11, abstracts.size());
//		List<Element> paras = XMLUtil.getQueryElements(abstracts.get(0), "*[local-name()='div']/*[local-name()='p']");
//		Assert.assertEquals("paras", 3, paras.size());
//	}
	
	/**

(//*[local-name()='div']/*[local-name()='section' and *[not(local-name()='h3' and  .='Abstract' or .='References' or .='Methods']]))
	 */

//	@Test
//	public void testSearchBMCDocumentForBadSections() {
//		JournalTagger bmcTagger = new BMCTagger();
//		try {
//			List<Element> abstracts = bmcTagger.querySectionsIncluding(BMC_188_ROOT, new String[]{"abstract", "references", "mthods"});
//			Assert.fail("should throw exception");
//		} catch (Exception e) {
//			Assert.assertEquals("Cannot find xpath for: mthods", e.getMessage());
//		}
//	}
	
	@Test
	@Ignore // use HTML tagger
	public void testBMCTagger() {
		JournalTagger bmcTagger = new BMCTagger();
		String journal = bmcTagger.getJournalId();
		Assert.assertEquals("id", "bmc", journal);
		List<Element> sections = bmcTagger.findSectionsFromTagDefinitions(BMC_188_ROOT, JournalTagger.ABSTRACT);
		Assert.assertEquals("abstract", 1, sections.size());
		Assert.assertEquals(11, JournalTagger.TAGS.length);
		for (String tag : JournalTagger.TAGS) {
			System.out.println(tag);
		}
		Assert.assertEquals("abstract", 6, XMLUtil.getQueryElements(sections.get(0), "*" ).size());
		Assert.assertEquals("abstract", 3, XMLUtil.getQueryElements(sections.get(0), "*[local-name()='p']" ).size());
		Assert.assertEquals("abstract", 3, XMLUtil.getQueryElements(sections.get(0), "*[local-name()='h4']" ).size());

	}
	
	@Test
	@Ignore // use HTML tagger
	public void testTagSections() {
		JournalTagger bmcTagger = new BMCTagger();
		bmcTagger.addTagsToSections(BMC_188_ROOT);
		List<Element> sections = bmcTagger.findSectionsFromTagDefinitions(BMC_188_ROOT, JournalTagger.ABSTRACT);
		Assert.assertEquals("abstract", JournalTagger.ABSTRACT, sections.get(0).getAttributeValue(JournalTagger.ATTRIBUTE_TAG));
		for (int i = 0; i < JournalTagger.TAGS.length; i++) {
			String tag = JournalTagger.TAGS[i];
			sections = bmcTagger.findSectionsFromTagDefinitions(BMC_188_ROOT, tag);
			Assert.assertEquals(""+tag,  tag, sections.get(0).getAttributeValue(JournalTagger.ATTRIBUTE_TAG));
		}
	}
	
}
