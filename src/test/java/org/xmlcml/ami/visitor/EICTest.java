package org.xmlcml.ami.visitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.ami.visitor.regex.RegexVisitor;
import org.xmlcml.xml.XMLUtil;

public class EICTest {

	private final static Logger LOG = Logger.getLogger(EIC.class);
	
	@Test
	public void testCreate() {
		String xmlString = ""
				+ "<foo>"
				+ "<bar>string0</bar>"
				+ "<baz>string1</baz>"
				+ "<xyz>string2</xyz>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		EIC eic = new EIC(element);
		Assert.assertEquals("toString", "[[string0string1string2]]", eic.toString());
	}
	
	@Test
	public void testCreate0() {
		String xmlString = ""
				+ "<foo>"
				+ "<bar>string0</bar>"
				+ "<baz>string1</baz>"
				+ "<xyz>string2</xyz>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		Element bar = element.getChildElements().get(0);
		EIC eic = new EIC(bar);
		Assert.assertEquals("toString", "[[string0]]string1|string2", eic.toString());
	}
	
	@Test
	public void testCreate1() {
		String xmlString = ""
				+ "<foo>"
				+ "<bar>string0</bar>"
				+ "<baz>string1</baz>"
				+ "<xyz>string2</xyz>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		Element bar = element.getChildElements().get(1);
		EIC eic = new EIC(bar);
		Assert.assertEquals("toString", "string0[[string1]]string2", eic.toString());
	}
	
	@Test
	public void testCreate2() {
		String xmlString = ""
				+ "<foo>"
				+ "<bar>string0</bar>"
				+ "<baz>string1</baz>"
				+ "<xyz>string2</xyz>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		Element bar = element.getChildElements().get(2);
		EIC eic = new EIC(bar);
		Assert.assertEquals("toString", "string0|string1[[string2]]", eic.toString());
	}

	// ===========================
	
	@Test
	public void testCreateB() {
		String xmlString = ""
				+ "<foo>"
				+ "<bar>string0</bar>"
				+ "<baz>string1</baz>"
				+ "<xyz>string2</xyz>"
				+ "<abc>string3</abc>"
				+ "<lmn>string4</lmn>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		EIC eic = new EIC(element);
		Assert.assertEquals("toString", "[[string0string1string2string3string4]]", eic.toString());
	}
	
	@Test
	public void testCreate0B() {
		String xmlString = ""
				+ "<foo>"
				+ "<bar>string0</bar>"
				+ "<baz>string1</baz>"
				+ "<xyz>string2</xyz>"
				+ "<abc>string3</abc>"
				+ "<lmn>string4</lmn>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		Element bar = element.getChildElements().get(0);
		EIC eic = new EIC(bar);
		Assert.assertEquals("toString", "[[string0]]string1|string2|string3|string4", eic.toString());
	}
	
	@Test
	public void testCreate1B() {
		String xmlString = ""
				+ "<foo>"
				+ "<bar>string0</bar>"
				+ "<baz>string1</baz>"
				+ "<xyz>string2</xyz>"
				+ "<abc>string3</abc>"
				+ "<lmn>string4</lmn>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		Element bar = element.getChildElements().get(1);
		EIC eic = new EIC(bar);
		Assert.assertEquals("toString", "string0[[string1]]string2|string3|string4", eic.toString());
	}
	
	@Test
	public void testCreate2B() {
		String xmlString = ""
				+ "<foo>"
				+ "<bar>string0</bar>"
				+ "<baz>string1</baz>"
				+ "<xyz>string2</xyz>"
				+ "<abc>string3</abc>"
				+ "<lmn>string4</lmn>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		Element bar = element.getChildElements().get(2);
		EIC eic = new EIC(bar);
		Assert.assertEquals("toString", "string0|string1[[string2]]string3|string4", eic.toString());
	}
	
	@Test
	public void testCreate2C() {
		String xmlString = ""
				+ "<foo>"
				+ "<bar>string0</bar>"
				+ "<baz>string1</baz>"
				+ "<xyz>string2</xyz>"
				+ "<abc>string3</abc>"
				+ "<lmn>string4</lmn>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		Element bar = element.getChildElements().get(3);
		EIC eic = new EIC(bar);
		Assert.assertEquals("toString", "string0|string1|string2[[string3]]string4", eic.toString());
	}
	
	@Test
	public void testCreate2D() {
		String xmlString = ""
				+ "<foo>"
				+ "<bar>string0</bar>"
				+ "<baz>string1</baz>"
				+ "<xyz>string2</xyz>"
				+ "<abc>string3</abc>"
				+ "<lmn>string4</lmn>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		Element bar = element.getChildElements().get(4);
		EIC eic = new EIC(bar);
		Assert.assertEquals("toString", "string0|string1|string2|string3[[string4]]", eic.toString());
	}
	
	@Test
	public void testFindStringsPussyCat() {
		Pattern pattern = Pattern.compile("and");
		String xmlString = ""
				+ "<foo>"
				+ "<bar>The owl and the pussycat</bar>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		Element bar = element.getChildElements().get(0);
		EIC eic = new EIC(bar);
		eic.findStrings(pattern);
		DocumentFragment resultFragment = eic.findStrings(pattern);
		assertResultList(new String[]{
				"The owl [[and]] the pussycat"}, resultFragment);
	}
	
	@Test
	public void testFindStringsTomorrow() {
		Pattern pattern = Pattern.compile("and|AND");
		String xmlString = ""
				+ "<foo>"
				+ "<bar>Tomorrow0 and tomorrow1 AND tomorrow2</bar>"
				+ "</foo>";
		Element element = XMLUtil.parseXML(xmlString);
		Element bar = element.getChildElements().get(0);
		EIC eic = new EIC(bar);
		DocumentFragment resultFragment = eic.findStrings(pattern);
		assertResultList(new String[]{
			"Tomorrow0 [[and]] tomorrow1 AND tomorrow2", " tomorrow1 [[AND]] tomorrow2"}, resultFragment);
	}

	private void assertResultList(String[] testStrings, DocumentFragment resultFragment) {
		LOG.trace("RES "+resultFragment);
		List<EIC> resultList = resultFragment.getEICList();
		Assert.assertEquals("results", testStrings.length, resultList.size());
		for (int i = 0; i < testStrings.length; i++) {
			Assert.assertEquals("result"+i, testStrings[i], resultList.get(i).toString());
		}
	}
	
	
	@Test
	public void testFindStringsInline() {
		Pattern pattern = Pattern.compile("and|AND");
		String line = "Tomorrow0 and tomorrow1 AND tomorrow2";
		EIC eic = new EIC(line);
		eic.findStrings(pattern);
		DocumentFragment resultFragment = eic.findStrings(pattern);
		assertResultList(new String[]{
			"Tomorrow0 [[and]] tomorrow1 AND tomorrow2", " tomorrow1 [[AND]] tomorrow2"}, resultFragment);
	}
	
	@Test
	public void testFindMultipleStringsInline() {
		List<Pattern> patternList = new ArrayList<Pattern>();
		patternList.add(Pattern.compile("be"));
		patternList.add(Pattern.compile("to"));
		patternList.add(Pattern.compile("question"));
		String line = "to be or not to be that is the question";
		EIC eic = new EIC(line);
		DocumentFragment resultFragment = eic.findStrings(patternList);
		assertResultList(new String[]{
			"[[to]] be or not to be that is the question", // this is not right...
			" [[be]] or not to be that is the question",
			" or not [[to]] be that is the question",
			" [[be]] that is the question",
			" that is the [[question]]"},
			resultFragment);
	}
	
	@Test
	public void testPhyloTreeRegex() throws Exception {
		File outputDir = new File("target/");
		File outputFile = new File(new File(outputDir.getParentFile(), "1471-2148-14-70.xml"), AbstractVisitor.RESULTS_XML);
		FileUtils.deleteQuietly(outputFile);
		Assert.assertFalse("should have deleted "+outputFile, outputFile.exists());
		String[] args = new String[] {
				"-i", "./docs/regex/1471-2148-14-70.xml",
				"-o", outputDir.toString(),
				"-g", "regex/phylotree.xml",
		};
		RegexVisitor.main(args);
		
	}
}
