package org.xmlcml.ami.visitor.regex;

//import junit.framework.Assert;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.files.EuclidSource;

public class RegexTest {
	
	@Test
	public void testReadRegexFile() throws Exception {
		CompoundRegex regex = CompoundRegex.readAndCreateRegex(Fixtures.TEST_PHYLO_REGEX_SMALL_XML);
		Assert.assertNotNull("regex", regex);
		Assert.assertEquals("value", 
				"(((ACCTRAN))|((align[^\\W]*))|((apomorph[^\\W]*))|((bayesian))|((PAUP\\*?\\s*(\\d+\\.?\\d*)))|((outgroup))|((BEAST)))",
				regex.getValue());
		
	}
	
	@Test
	public void testGetRegexValues() throws Exception {
		CompoundRegex regex = CompoundRegex.readAndCreateRegex(
				EuclidSource.getInputStream("src/test/resources/org/xmlcml/ami/regex/phylotree_small.xml"));
		Assert.assertNotNull("regex", regex);
		Assert.assertEquals("value", 7, regex.getRegexValues().size());
		Assert.assertEquals("value0", "(ACCTRAN)", regex.getRegexValues().get(0));
	}
	
	@Test
	public void testGetRegexComponentList() throws Exception {
		CompoundRegex regex = CompoundRegex.readAndCreateRegex(Fixtures.TEST_PHYLO_REGEX_SMALL_XML);
		Assert.assertNotNull("regex", regex);
		Assert.assertEquals("value", 7, regex.getRegexComponentList().size());
		Assert.assertEquals("value0", "(ACCTRAN)", regex.getRegexComponentList().get(0).getValue());
		Assert.assertEquals("value29", "(PAUP\\*?\\s*(\\d+\\.?\\d*))", regex.getRegexComponentList().get(4).getValue());
	}
	
	@Test
	public void testGetRegexComponentURL() throws Exception {
		CompoundRegex regex = CompoundRegex.readAndCreateRegex(Fixtures.TEST_PHYLO_REGEX_SMALL_XML);
		RegexComponent paupComponent = regex.getRegexComponentList().get(4);
//		Assert.assertEquals("case", "required", paupComponent.getCase());
		Assert.assertEquals("url29", "http://paup.csit.fsu.edu/", paupComponent.getURL());
		Assert.assertEquals("title", "Phylogenetic Analysis Using Parsimony", paupComponent.getTitle());
		RegexComponent outgroupComponent = regex.getRegexComponentList().get(5);
		Assert.assertNull("case", outgroupComponent.getCase());
		Assert.assertNull("url", outgroupComponent.getURL());
		Assert.assertNull("title", outgroupComponent.getTitle());
		
	}
	
}
