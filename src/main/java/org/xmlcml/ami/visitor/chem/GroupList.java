package org.xmlcml.ami.visitor.chem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Elements;

import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLList;
import org.xmlcml.cml.element.CMLMolecule;

public class GroupList {

	private List<CMLMolecule> moleculeList;
	
	public GroupList(InputStream is) {
		try {
			moleculeList = new ArrayList<CMLMolecule>();
			buildList(is);
		} catch (Exception e) {
			throw new RuntimeException("Cannot read molecule groups. " + e.getMessage(), e);
		}
	}

	private void buildList(InputStream is) throws Exception {
		try {
			CMLList list = (CMLList) new CMLBuilder().build(is).getRootElement();
			Elements childElements = list.getChildElements();
			for (int i = 0; i < childElements.size(); i++) {
				CMLMolecule molecule = (CMLMolecule) childElements.get(i);
				moleculeList.add(molecule);
			}
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	public CMLMolecule get(String name) {
		if (name != null) {
			for (CMLMolecule molecule : moleculeList) {
				if (name.equals(molecule.getTitle())) {
					return molecule;
				}
			}
		}
		return null;
	}
}
