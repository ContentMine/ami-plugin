package org.xmlcml.ami2.plugins.species;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.files.ResultElement;
import org.xmlcml.files.ResultsElement;
import org.xmlcml.xml.XMLUtil;

public class SpeciesResultsElement extends ResultsElement {

	private List<String> nameList;

	public SpeciesResultsElement(String title) {
		super(title);
	}

//	public SpeciesResultsElement(SpeciesResultsElement resultsElement) {
//		XMLUtil.copyAttributesFromTo(resultsElement, this);
//		for (ResultElement resultElement : resultsElement) {
//			this.appendChild(new SpeciesResultElement(resultElement));
//		}
//	}

//	public void resolveAbbreviations() {
//		getNameList();
//	}

	public List<String> getNameList() {
		if (nameList == null) {
			nameList = new ArrayList<String>();
			for (ResultElement resultElement : this) {
				SpeciesResultElement speciesResultElement = (SpeciesResultElement)resultElement;
				String name = speciesResultElement.getMatch();
				nameList.add(name);
			}
		}
		return nameList;
	}

	public void replaceMatches(List<String> nameList) {
		if (this.size() != nameList.size()) {
			throw new RuntimeException("name list wrong length ("+nameList.size()+") rather than ("+this.size()+")");
		}
		int i = 0;
		for (ResultElement resultElement : this) {
			resultElement.setMatch(nameList.get(i));
			// cosmetic - keeps attributes in natural order
			resultElement.setPost(resultElement.getPost());
			i++;
		}
	}

}
