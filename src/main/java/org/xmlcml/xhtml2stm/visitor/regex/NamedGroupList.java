package org.xmlcml.xhtml2stm.visitor.regex;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

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

	public Element createElement() {
		Element hit = new Element("hit");
		for (NamedGroup namedGroup : namedGroupList) {
			Attribute att = namedGroup.createAttribute();
			if (att != null) {
				hit.addAttribute(att);
			}
		}
		return hit;
	}
}
