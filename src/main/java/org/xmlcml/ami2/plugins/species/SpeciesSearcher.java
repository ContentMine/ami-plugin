package org.xmlcml.ami2.plugins.species;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.ami2.plugins.AMIArgProcessor;
import org.xmlcml.ami2.plugins.DefaultSearcher;
import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.html.HtmlP;

public class SpeciesSearcher extends DefaultSearcher {

	
	public static final Logger LOG = Logger.getLogger(SpeciesSearcher.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	private String speciesType;
	

	public SpeciesSearcher(AMIArgProcessor argProcessor, String speciesType, Pattern pattern) {
		super(argProcessor, pattern);
		this.speciesType = speciesType;
	}

	@Override
	public ResultsElement search(List<HtmlP> pElements) {
		ResultsElement resultsElement = new ResultsElement();
		for (HtmlP pElement : pElements) {
			// this is ucky, but since we know the HTML is normalized it's probably OK
			String xmlString = pElement.toXML().replaceAll("\\s+", " ");
			// some markup is of form <i>Foo</i>. <i>bar</i>
			xmlString = xmlString.replaceAll("</i>\\.\\s+<i>", ". ");
			xmlString = xmlString.replaceAll("<span[^>]*>", "[");
			xmlString = xmlString.replaceAll("</span[^>]*>", "]");
			xmlString = xmlString.replaceAll("<b>", "");
			xmlString = xmlString.replaceAll("</b>", "");
			List<ResultElement> resultElementList = this.search(xmlString);
			for (ResultElement resultElement : resultElementList) {
				resultsElement.appendChild(resultElement);
			}
		}
		return resultsElement;
	}

	@Override
	protected String flattenTags(String s) {
		s = s.replaceAll("<i>", "");
		s = s.replaceAll("</i>", "");
		s = s.replaceAll("<b[^>]*>", "");
		s = s.replaceAll("</b[^>]*>", "");
		return s;
	}



	public String getSpeciesType() {
		return speciesType;
	}
	
}
