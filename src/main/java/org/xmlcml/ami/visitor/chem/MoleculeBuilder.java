package org.xmlcml.ami.visitor.chem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLUtil;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;
import org.xmlcml.cml.element.CMLBondStereo;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.ConnectionTableTool;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.euclid.Real2;
import org.xmlcml.molutil.ChemicalElement;

/**
 * Joins groups onto a molecule.
 * <p>
 * Looks for identical atom labels (cml:label) in group and molecule and 
 * elides atoms and bonds and renumbers atoms.
 * <p>
 * At present cannot distinguish between orientations of groups and
 * always takes atom j1.
 *
 * @author pm286
 */
public class MoleculeBuilder {

	private static final String J1 = "j1";

	private final static Logger LOG = Logger.getLogger(MoleculeBuilder.class);
	
	private static final String JOIN = "join";
	private GroupList groupList;
	private CMLMolecule inputMol;
	private CMLMolecule outputMol;

	public MoleculeBuilder(GroupList groupList) {
		this.groupList = groupList;
	}
	
	/*<?xml version="1.0" encoding="UTF-8"?>
	<molecule xmlns="http://www.xml-cml.org/schema">
	  <atomArray>
	    <atom id="a1" elementType="R" x2="20.225" y2="11.616">
	      <label name="join" value="OMe"/>
	    </atom>
	    <atom id="a2" elementType="C" x2="8.469" y2="20.166"/>
	  </atomArray>
	  <bondArray>
	    <bond atomRefs2="a2 a1" order="S" id="a2_a1"/>
	  </bondArray>
	</molecule>*/
	
	public CMLMolecule buildOnto(CMLMolecule mol) {
		inputMol = mol;
		outputMol = new CMLMolecule(inputMol);
		List<CMLAtom> joinableAtomList = getJoinableAtomList(inputMol);
		for (CMLAtom joinableAtom : joinableAtomList) {
			findGroupAndJoinTo(joinableAtom);
		}
		return outputMol;
	}

	public CMLMolecule buildOnto(CMLMolecule mol, Map<String, String> rGroupSubstitution) {
		inputMol = mol;
		outputMol = new CMLMolecule(inputMol);
		List<CMLAtom> joinableAtomList = getJoinableAtomList(inputMol);
		for (CMLAtom joinableAtom : joinableAtomList) {
			CMLLabel label = getLabelList(joinableAtom).get(0);
			for (Entry<String, String> sub : rGroupSubstitution.entrySet()) {
				label.setStringContent(label.getValue().replace(sub.getKey(), sub.getValue()));
			}
			if (ChemicalElement.getChemicalElement(label.getValue()) != null) {
				joinableAtom.setElementType(label.getValue());
				joinableAtom.removeChild(label);
			} else {
				findGroupAndJoinTo(joinableAtom);
			}
		}
		return outputMol;
	}

	private void findGroupAndJoinTo(CMLAtom joinableAtomInMolecule) {
		CMLMolecule group = getGroup(joinableAtomInMolecule);
		if (group != null) {
			CMLAtom joinableAtomInGroup = getJoinableAtom(group);
			if (joinableAtomInGroup == null) {
				LOG.error("no joinable atom");
			} else {
				CMLMolecule copyGroup = new CMLMolecule(group);
				join(joinableAtomInMolecule, joinableAtomInGroup.getId(), copyGroup);
			}
		}
	}

	private void join(CMLAtom joinableAtomInMolecule, String joinableAtomInGroupId, CMLMolecule group) {
		MoleculeTool groupTool = MoleculeTool.getOrCreateTool(group);
		MoleculeTool inputMoleculeTool = MoleculeTool.getOrCreateTool(inputMol);

		CMLAtom joinableAtomInGroup = group.getAtomById(joinableAtomInGroupId);
		if (joinableAtomInGroup.getLigandAtoms().size() != 1) {
			throw new RuntimeException("Cannot join to multi-coordinate R-group");
		}
		
		CMLAtom realMolAtom = joinableAtomInMolecule.getLigandAtoms().get(0);
		CMLAtom realGroupAtom = joinableAtomInGroup.getLigandAtoms().get(0);
		realGroupAtom.setXY2(joinableAtomInMolecule.getXY2());
		CMLLabel atomLabel = joinableAtomInMolecule.getLabelElements().get(1);//TODO add label names
		if (atomLabel != null) {
			atomLabel.detach();
			realGroupAtom.appendChild(atomLabel);
		}
		for (CMLAtom a : group.getAtomArray().getAtomElements()) {
			if (a != realGroupAtom) {
				a.setXY2(joinableAtomInMolecule.getXY2().plus(new Real2(1, 1)));
			}
		}

		// renumber
		List<String> avoidMoleculeIdList = inputMoleculeTool.getAtomIdList();
		LOG.trace("avoid list" + avoidMoleculeIdList);
		groupTool.renumberToUniqueIds(avoidMoleculeIdList);
		
		// delete R groups
		CMLBondStereo stereo = joinableAtomInMolecule.getLigandBonds().get(0).getBondStereo();
		String order = joinableAtomInMolecule.getLigandBonds().get(0).getOrder();
		inputMol.deleteAtom(joinableAtomInMolecule);
		group.deleteAtom(joinableAtomInGroup);
		
		// merge
		ConnectionTableTool molCt = new ConnectionTableTool(inputMol);
		molCt.mergeMolecule(group);
		CMLBond joiningBond = new CMLBond(realMolAtom, realGroupAtom);
		joiningBond.setOrder(order);
		if (stereo != null) {
			joiningBond.setBondStereo(stereo);
		}
		inputMoleculeTool.getMolecule().addBond(joiningBond);
	}

	private CMLAtom getJoinableAtom(CMLMolecule group) {
		List<CMLAtom> atomList = getJoinableAtomList(group);
		CMLAtom joinableAtom = null;
		if (atomList.size() == 1) {
			CMLAtom atom0 = atomList.get(0);
			CMLLabel label = (CMLLabel) CMLUtil.getQueryElements(
					atom0, "./cml:label", CMLUtil.CML_XPATH).get(0);
			if (label != null && J1.equals(label.getValue())) {
				joinableAtom = atom0;
			}
		}
		return joinableAtom;
	}

	public CMLMolecule getGroup(CMLAtom joinableAtom) {
		List<CMLLabel> labelValueList = getLabelList(joinableAtom);
		String labelValue = (labelValueList.size() == 1 ? labelValueList.get(0).getValue().trim() : null);
		CMLMolecule group = groupList.get(labelValue);
		if (group == null) {
			String[] bits = labelValue.split("(\\s)+");
			String longestString = "";
			int longestStringLength = 0;
			for (String bit : bits) {
				if (bit.length() > longestStringLength) {
					longestString = bit;
				}
			}
			group = groupList.get(longestString);
		}
		return group;
	}

	public List<CMLLabel> getLabelList(CMLAtom joinableAtom) {
		List<Element> labelElements = CMLUtil.getQueryElements(
				joinableAtom, "./cml:label[@name='"+JOIN+"']", CMLUtil.CML_XPATH);
		List<CMLLabel> labels = new ArrayList<CMLLabel>();
		for (Element element : labelElements) {
			CMLLabel label = (CMLLabel) element;
			labels.add(label);
		}
		return labels;
	}

	public List<CMLAtom> getJoinableAtomList(CMLMolecule mol) {
		List<Element> elementList = CMLUtil.getQueryElements(
				mol, "./cml:atomArray/cml:atom[cml:label[@name='"+JOIN+"']]", CMLUtil.CML_XPATH);
		List<CMLAtom> atomList = new ArrayList<CMLAtom>();
		for (Element element : elementList) {
			atomList.add((CMLAtom) element);
		}
		return atomList;
	}
	
}