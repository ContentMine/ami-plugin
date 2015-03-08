package org.xmlcml.ami.plugin.plugins.regex;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.IllegalNameException;

import org.xmlcml.ami.plugin.result.HitElement;
import org.xmlcml.ami.plugin.result.ResultElement;

/** the fields matched in a regex mapped to the field names.
 * 
 * @author pm286
 *
 */
public class NamedGroupList {

	private List<NamedGroup> namedGroupList;

	public NamedGroupList() {
		
	}

	public void add(NamedGroup namedGroup) {
		ensureNamedGroupList();
		namedGroupList.add(namedGroup);
			
	}

	private void ensureNamedGroupList() {
		if (namedGroupList == null) {
			namedGroupList = new ArrayList<NamedGroup>();
		}
	}
	
	@Override
	public String toString() {
		return namedGroupList.get(0).toString();
	}

	public ResultElement createResultElement() {
		ResultElement resultElement = new ResultElement();
		for (NamedGroup namedGroup : namedGroupList) {
			Attribute att = namedGroup.createAttribute();
			if (att != null) {
				resultElement.addAttribute(att);
			}
		}
		return resultElement;
	}

	public Element createElement() throws IllegalNameException {
		Element hit = new HitElement();
		for (NamedGroup namedGroup : namedGroupList) {
			Attribute att = namedGroup.createAttribute();
			if (att != null) {
				hit.addAttribute(att);
			}
		}
		return hit;
	}
}
