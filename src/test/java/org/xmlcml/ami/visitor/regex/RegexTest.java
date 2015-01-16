package org.xmlcml.ami.visitor.regex;

//import junit.framework.Assert;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;

public class RegexTest {
	
	@Test
	@Ignore // file changes too often
	public void testReadRegexFile() {
		CompoundRegex regex = CompoundRegex.readAndCreateRegex(Fixtures.PHYLO_REGEX_XML);
		Assert.assertNotNull("regex", regex);
		Assert.assertEquals("value", 
//				"((acctran)|(align.*)|(apomorph.*)|(bayesian)|(bootstrap.*)|(branch.*)|"
//				+ "(bremer)|(cladist.*)|(consistency)|(deltran)|(dryad)|(garli)|(genbank)|(goloboff)|(gtr)|"
//				+ "(hennig)|(jackknif.*)|(likelihood)|(matrix)|(maximum)|(mega)|(mrbayes)|(multistate)|"
//				+ "(neighbor)|(node)|(nona)|(order.*)|(outgroup)|(parsimon.*)|(paup\\*?)|(phylip)|(phylog.*)|"
//				+ "(phyml)|(poy)|(raxml)|(support)|(swofford)|(tbr)|(tnt)|(tree)|(treebase)|(unord.*)|(winclada))", 
				"((acctran)|(align.*)|(apomorph.*)|(bayesian)|(bootstrap.*)|(branch.*)|(bremer)|(cladist.*)|(consistency)|(deltran)|(dryad)|(garli)|(genbank)|(goloboff)|(gtr)|(hennig)|(jackknif.*)|(likelihood)|(matrix)|(maximum)|(mega)|(mrbayes)|(multistate)|(neighbor)|(node)|(nona)|(order.*)|(outgroup)|(parsimon.*)|(PAUP\\*?)|(phylip)|(phylog.*)|(phyml)|(poy)|(raxml)|(support)|(swofford)|(tbr)|(tnt)|(tree)|(treebase)|(unord.*)|(winclada))",
				regex.getValue());
		
	}
	
	@Test
	@Ignore // file changes too often
	public void testGetRegexValues() {
		CompoundRegex regex = CompoundRegex.readAndCreateRegex(Fixtures.PHYLO_REGEX_XML);
		Assert.assertNotNull("regex", regex);
		Assert.assertEquals("value", 43, regex.getRegexValues().size());
		Assert.assertEquals("value0", "acctran", regex.getRegexValues().get(0));
		Assert.assertEquals("value28", "parsimon.*", regex.getRegexValues().get(28));
		Assert.assertEquals("value29", "PAUP\\*?", regex.getRegexValues().get(29));
		Assert.assertEquals("value42", "winclada", regex.getRegexValues().get(42));
	}
	
	@Test
	@Ignore // file changes too often
	public void testGetRegexComponentList() {
		CompoundRegex regex = CompoundRegex.readAndCreateRegex(Fixtures.PHYLO_REGEX_XML);
		Assert.assertNotNull("regex", regex);
		Assert.assertEquals("value", 43, regex.getRegexComponentList().size());
		Assert.assertEquals("value0", "acctran", regex.getRegexComponentList().get(0).getValue());
		Assert.assertEquals("value28", "parsimon.*", regex.getRegexComponentList().get(28).getValue());
		Assert.assertEquals("value29", "PAUP\\*?", regex.getRegexComponentList().get(29).getValue());
		Assert.assertEquals("value42", "winclada", regex.getRegexComponentList().get(42).getValue());
		
	}
	
	@Test
	@Ignore // file changes too often
	public void testGetRegexComponentURL() {
		CompoundRegex regex = CompoundRegex.readAndCreateRegex(Fixtures.PHYLO_REGEX_XML);
		RegexComponent paupComponent = regex.getRegexComponentList().get(29);
		Assert.assertEquals("case", "required", paupComponent.getCase());
		Assert.assertEquals("url29", "http://paup.csit.fsu.edu/", paupComponent.getURL());
		Assert.assertEquals("title", "Phylogenetic Analysis Using Parsimony", paupComponent.getTitle());
		RegexComponent outgroupComponent = regex.getRegexComponentList().get(27);
		Assert.assertNull("case", outgroupComponent.getCase());
		Assert.assertNull("url", outgroupComponent.getURL());
		Assert.assertNull("title", outgroupComponent.getTitle());
		
	}
	
}
