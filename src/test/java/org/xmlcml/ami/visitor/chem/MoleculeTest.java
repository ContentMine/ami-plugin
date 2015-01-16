package org.xmlcml.ami.visitor.chem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitable.svg.SVGContainer;
import org.xmlcml.cml.base.CMLBuilder;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLFormula;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLMolecule.HydrogenControl;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.tools.MoleculeTool;
import org.xmlcml.cml.tools.StereochemistryTool;
import org.xmlcml.euclid.Real2;
import org.xmlcml.graphics.svg.SVGCircle;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.SVGUtil;

import com.google.caliper.Benchmark;
import com.google.caliper.runner.CaliperMain;

//@Ignore
//FIXME fails on Junctions
public class MoleculeTest {

	private static final double ATOM_RAD = 6.0;
	public final static double N_WIDTH = 500.0;
	public final static double O_WIDTH = 500.0;
	
	private static Real2 N_CENTRE = new Real2(100.0, 100.0);
	private static Real2 O_CENTRE = new Real2(150.0, 100.0);
	
	private static final Logger LOG = Logger.getLogger(MoleculeTest.class);
	
	public static void main(String[] args) throws FileNotFoundException {
		ChemistryBuilder chemistryBuilder = new ChemistryBuilder(new SVGContainer(new File(args[0]), SVGElement.readAndCreateSVG(new File(args[0]))));
		MoleculeCreator moleculeCreator = new MoleculeCreator(chemistryBuilder);
		Collection<CMLReaction> reactions = moleculeCreator.createReactions();
		if (reactions.size() != 0 ) {
			moleculeCreator.drawReactions();
		} else {
			moleculeCreator.drawMolecules();
		}
		/*for (CMLReaction reaction : reactions) {
			SVGUtil.debug(reaction, new FileOutputStream("target/andy.cml"), 0);
		}*/
		int i = 0;
		for (CMLMolecule molecule : moleculeCreator.createMolecules()) {
			SVGUtil.debug(molecule, new FileOutputStream("target/chem/andy" + i++ + ".cml"), 0);
		}
	}

	@Test
	@Ignore("Waiting for SVG to CML building including labels")
	public void testMethoxymethane() {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "methoxymethane.svg")));
		MoleculeCreator creator = new MoleculeCreator(geometryBuilder);
		CMLMolecule mol = creator.createMolecule();
		Assert.assertEquals(8, mol.getAtomCount());
		for (int i = 0; i < mol.getAtomCount(); i++) {
			Assert.assertNotEquals("R", mol.getAtom(i).getElementType());
		}
	}
	
	@Test
	public void testNO() {

		SVGG g = new SVGG();

		double fontSize = 10.0;
		SVGText nText = new SVGText(N_CENTRE, "N");
		SVGUtil.setSVGXAttribute(nText, "width", String.valueOf(N_WIDTH));
		Real2 nOrigin = calculateOriginOfText(N_CENTRE, fontSize, nText);
		g.appendChild(new SVGCircle(N_CENTRE, ATOM_RAD));
		g.appendChild(nText);
		
		SVGText oText = new SVGText(O_CENTRE, "O");
		SVGUtil.setSVGXAttribute(oText, "width", String.valueOf(O_WIDTH));
		Real2 oOrigin = calculateOriginOfText(O_CENTRE, fontSize, oText);
		g.appendChild(new SVGCircle(O_CENTRE, ATOM_RAD));
		g.appendChild(oText);
		
		SVGLine line = new SVGLine(N_CENTRE.plus(new Real2(ATOM_RAD, 0.0)), O_CENTRE.plus(new Real2(-ATOM_RAD, 0.0)));
		g.appendChild(line);
		
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/chem/atomn.svg"));
		
	}
	
	@Test
	@Ignore("Waiting for SVG to CML")
	public void testNOJunctions() {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "no.svg")));
		//, 2, 1, 2, 2); 
		//Assert.assertEquals("junction", 2, geometryBuilder.getHigherPrimitives().getRawJunctionList().size());
		Assert.assertEquals("junctions", 2, geometryBuilder.getHigherPrimitives().getJunctionList().size());
		Assert.assertEquals("texts", 2, geometryBuilder.getRawPrimitives().getTextList().size());
		Assert.assertEquals("lines", 1, geometryBuilder.getHigherPrimitives().getLineList().size());
	}

	@Test
	@Ignore("Waiting for SVG to CML")
	public void testNO2Junctions() {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "no2.svg")));
		Assert.assertEquals("junctions", 3, geometryBuilder.getHigherPrimitives().getJunctionList().size());
		Assert.assertEquals("texts", 3, geometryBuilder.getRawPrimitives().getTextList().size());
		Assert.assertEquals("lines", 2, geometryBuilder.getHigherPrimitives().getLineList().size());
		
	}

	@Test
	@Ignore("Waiting for SVG to CML")
	public void testNO2Chemistry() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "no2.svg")));
		Assert.assertEquals("junctions", 3, geometryBuilder.getHigherPrimitives().getJunctionList().size());
		Assert.assertEquals("texts", 3, geometryBuilder.getRawPrimitives().getTextList().size());
		Assert.assertEquals("lines", 2, geometryBuilder.getHigherPrimitives().getLineList().size());
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		CMLMolecule molecule = moleculeCreator.createMolecule();
		//Order of IDs is not stable
		SVGUtil.debug(molecule, new FileOutputStream("target/chem/no2.cml"), 0);
		Assert.assertEquals("atoms", 3, molecule.getAtomCount());
		Assert.assertEquals("bonds", 2, molecule.getBondCount());
		
	}

	@Test
	public void test2_11() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.2.11.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		CMLMolecule molecule = moleculeCreator.createMolecule();
		SVGUtil.debug(molecule, new FileOutputStream("target/chem/2.11.cml"), 0);
		Assert.assertEquals("atoms", 13, molecule.getAtomCount());
		Assert.assertEquals("bonds", 12, molecule.getBondCount()); // something went wrong
	}

	@Test
	public void test2_11a() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.2.11a.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		CMLMolecule molecule = moleculeCreator.createMolecule();
		for (Junction Junction : geometryBuilder.getHigherPrimitives().getJunctionList()) {
			LOG.trace(Junction);
		}
		SVGUtil.debug(molecule, new FileOutputStream("target/chem/2.11.cml"), 0);
		Assert.assertEquals("atoms", 13, molecule.getAtomCount());
		Assert.assertEquals("bonds", 12, molecule.getBondCount()); 
	}

	@Test
	public void test2_13() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.2.13.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		CMLMolecule molecule = moleculeCreator.createMolecule();
		SVGUtil.debug(molecule, new FileOutputStream("target/chem/2.13.cml"), 0);
		Assert.assertEquals("atoms", 16, molecule.getAtomCount());
		Assert.assertEquals("bonds", 16, molecule.getBondCount()); // something went wrong
	}

	@Test
	@Ignore("Waiting for complete stereo handling")
	public void test2_15() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.2.15.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		CMLMolecule molecule = moleculeCreator.createMolecule();
		SVGUtil.debug(molecule, new FileOutputStream("target/chem/2.15.cml"), 0);
		Assert.assertEquals("atoms", 10, molecule.getAtomCount());
		Assert.assertEquals("bonds", 10, molecule.getBondCount()); // something went wrong
	}

	@Test
	public void test2_21() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.2.21.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		Collection<CMLMolecule> molecules = moleculeCreator.createMolecules();
		Iterator<CMLMolecule> it = molecules.iterator();
		CMLMolecule mol1 = it.next();
		//CMLMolecule mol2 = it.next();
		//SVGUtil.debug(mol, new FileOutputStream("target/2.21.0.cml"), 0);
		//drawFromChemistryBuilderAndMolecules(geometryBuilder, molecules);
		Assert.assertEquals("atoms", 73, mol1.getAtomCount());
		Assert.assertEquals("bonds", 76, mol1.getBondCount());
		//Assert.assertTrue("atoms", mol1.getAtomCount() == 73 || mol2.getAtomCount() == 73);
		//Assert.assertTrue("bonds", mol1.getBondCount() == 76 || mol2.getBondCount() == 76);
	}

	@Test
	@Ignore
	// FIXME fails test on atoms
	public void test2_22() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.2.22.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		Collection<CMLMolecule> molecules = moleculeCreator.createMolecules();
		CMLMolecule mol = molecules.iterator().next();
		SVGUtil.debug(mol, new FileOutputStream("target/chem/2.22.0.cml"), 0);
		/*SVGUtil.debug(molecules.get(1), new FileOutputStream("target/2.22.1.cml"), 0);
		SVGUtil.debug(molecules.get(2), new FileOutputStream("target/2.22.2.cml"), 0);
		SVGUtil.debug(molecules.get(3), new FileOutputStream("target/2.22.3.cml"), 0);
		SVGUtil.debug(molecules.get(4), new FileOutputStream("target/2.22.4.cml"), 0);
		SVGUtil.debug(molecules.get(5), new FileOutputStream("target/2.22.5.cml"), 0);
		SVGUtil.debug(molecules.get(6), new FileOutputStream("target/2.22.6.cml"), 0);
		SVGUtil.debug(molecules.get(7), new FileOutputStream("target/2.22.7.cml"), 0);*/
		Assert.assertEquals("atoms", 66, mol.getAtomCount());
		Assert.assertEquals("bonds", 70, mol.getBondCount());
	}

	@Test
	public void test2_23() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.2.23.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		CMLMolecule molecule = moleculeCreator.createMolecule();
		SVGUtil.debug(molecule, new FileOutputStream("target/chem/2.23.cml"), 0);
		Assert.assertEquals("atoms", 38, molecule.getAtomCount());
		Assert.assertEquals("bonds", 37, molecule.getBondCount()); // something went wrong
	}
	
	//TODO
	@Test
	@Ignore("Needs assertion(s)")
	public void test3_15() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.3.15.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		Collection<CMLReaction> reactions = moleculeCreator.createReactions();
		Iterator<CMLReaction> it = reactions.iterator();
		CMLReaction r1 = it.next();
		CMLReaction r2 = it.next();
		CMLReaction r3 = it.next();
		SVGUtil.debug(r1, new FileOutputStream("target/chem/3.15.0.cml"), 0);
		SVGUtil.debug(r2, new FileOutputStream("target/chem/3.15.1.cml"), 0);
		SVGUtil.debug(r3, new FileOutputStream("target/chem/3.15.2.cml"), 0);
		Assert.assertTrue(false);
		//Assert.assertTrue((r1.getReactantList().getChildCount() == 1 && r1.getProductList() == null && r2.getReactantList().getChildCount() == 1 && r2.getProductList().getChildCount() == 1) || (r2.getReactantList().getChildCount() == 1 && r2.getProductList() == null && r1.getReactantList().getChildCount() == 1 && r1.getProductList().getChildCount() == 1));
	}
	
	//TODO
	@Test
	@Ignore("Needs assertion(s)")
	public void test6_5() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "02.00100.g.6.5.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		Collection<CMLReaction> reactions = moleculeCreator.createReactions();
		Iterator<CMLReaction> it = reactions.iterator();
		CMLReaction r1 = it.next();
		CMLReaction r2 = it.next();
		CMLReaction r3 = it.next();
		SVGUtil.debug(r1, new FileOutputStream("target/chem/6.5.0.cml"), 0);
		SVGUtil.debug(r2, new FileOutputStream("target/chem/6.5.1.cml"), 0);
		SVGUtil.debug(r3, new FileOutputStream("target/chem/6.5.2.cml"), 0);
		Assert.assertTrue(false);
		//Assert.assertTrue((r1.getReactantList().getChildCount() == 1 && r1.getProductList() == null && r2.getReactantList().getChildCount() == 1 && r2.getProductList().getChildCount() == 1) || (r2.getReactantList().getChildCount() == 1 && r2.getProductList() == null && r1.getReactantList().getChildCount() == 1 && r1.getProductList().getChildCount() == 1));
	}

	@Test
	@Ignore("Fails jitter threshold twice")
	public void test8_3() throws Exception {
		ChemistryBuilder chemistryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.8.3.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(chemistryBuilder);
		Collection<CMLReaction> reactions = moleculeCreator.createReactions();
		moleculeCreator.drawReactions();
		Iterator<CMLReaction> it = reactions.iterator();
		CMLReaction r1 = it.next();
		CMLReaction r2 = it.next();
		SVGUtil.debug(r1, new FileOutputStream("target/chem/8.3.0.cml"), 0);
		SVGUtil.debug(r2, new FileOutputStream("target/chem/8.3.1.cml"), 0);
		Assert.assertTrue((r1.getReactantList().getChildCount() == 1 && r1.getProductList() == null && r2.getReactantList().getChildCount() == 1 && r2.getProductList().getChildCount() == 1) || (r2.getReactantList().getChildCount() == 1 && r2.getProductList() == null && r1.getReactantList().getChildCount() == 1 && r1.getProductList().getChildCount() == 1));
		/*List<CMLMolecule> molecule = moleculeCreator.createMolecules();
		SVGUtil.debug(molecule.get(0), new FileOutputStream("target/8.3.0.cml"), 0);
		SVGUtil.debug(molecule.get(1), new FileOutputStream("target/8.3.1.cml"), 0);
		SVGUtil.debug(molecule.get(2), new FileOutputStream("target/8.3.2.cml"), 0);
		SVGUtil.debug(molecule.get(3), new FileOutputStream("target/8.3.3.cml"), 0);
		SVGUtil.debug(molecule.get(4), new FileOutputStream("target/8.3.4.cml"), 0);
		SVGUtil.debug(molecule.get(5), new FileOutputStream("target/8.3.5.cml"), 0);
		SVGUtil.debug(molecule.get(6), new FileOutputStream("target/8.3.6.cml"), 0);
		SVGUtil.debug(molecule.get(7), new FileOutputStream("target/8.3.7.cml"), 0);
		Assert.assertEquals("atoms", 10, molecule.get(0).getAtomCount());
		Assert.assertEquals("bonds", 10, molecule.get(0).getBondCount());*/
	}

	//TODO
	@Test
	@Ignore("Needs assertion(s)")
	public void test8_3DoubleArrow() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.8.3.doublearrow.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		Collection<CMLReaction> reactions = moleculeCreator.createReactions();
		Iterator<CMLReaction> it = reactions.iterator();
		CMLReaction r1 = it.next();
		//CMLReaction r2 = it.next();
		SVGUtil.debug(r1, new FileOutputStream("target/chem/8.3.0.cml"), 0);
		//SVGUtil.debug(r2, new FileOutputStream("target/8.3.1.cml"), 0);
		//Assert.assertTrue((r1.getReactantList().getChildCount() == 1 && r1.getProductList() == null && r2.getReactantList().getChildCount() == 1 && r2.getProductList().getChildCount() == 1) || (r2.getReactantList().getChildCount() == 1 && r2.getProductList() == null && r1.getReactantList().getChildCount() == 1 && r1.getProductList().getChildCount() == 1));
		Assert.assertTrue(false);
	}

	//TODO
	@Test
	@Ignore("Needs assertion(s)")
	public void test8_3TripleArrow() throws Exception {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.8.3.triplearrow.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		Collection<CMLReaction> reactions = moleculeCreator.createReactions();
		Iterator<CMLReaction> it = reactions.iterator();
		CMLReaction r1 = it.next();
		//CMLReaction r2 = it.next();
		SVGUtil.debug(r1, new FileOutputStream("target/chem/8.3.0.cml"), 0);
		//SVGUtil.debug(r2, new FileOutputStream("target/8.3.1.cml"), 0);
		//Assert.assertTrue((r1.getReactantList().getChildCount() == 1 && r1.getProductList() == null && r2.getReactantList().getChildCount() == 1 && r2.getProductList().getChildCount() == 1) || (r2.getReactantList().getChildCount() == 1 && r2.getProductList() == null && r1.getReactantList().getChildCount() == 1 && r1.getProductList().getChildCount() == 1));
		Assert.assertTrue(false);
	}
	
	@Test
	public void test9_6() throws FileNotFoundException {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "image.g.9.6.svg")));
		MoleculeCreator moleculeCreator = new MoleculeCreator(geometryBuilder);
		Collection<CMLMolecule> molecules = moleculeCreator.createMolecules();
		Iterator<CMLMolecule> it = molecules.iterator();
		moleculeCreator.drawMolecules();
		SVGUtil.debug(it.next(), new FileOutputStream("target/chem/9.6.0.cml"), 0);
		SVGUtil.debug(it.next(), new FileOutputStream("target/chem/9.6.1.cml"), 0);
		SVGUtil.debug(it.next(), new FileOutputStream("target/chem/9.6.2.cml"), 0);
		//SVGUtil.debug(it.next(), new FileOutputStream("target/chem/9.6.3.cml"), 0);
		//Assert.assertEquals("atoms", 66, mol.getAtomCount());
		//Assert.assertEquals("bonds", 70, mol.getBondCount());
	}

	public static class ReactionsBenchmark extends Benchmark {
		public void time(int reps) {
			int g = 8;
			g *= 6;
		}
		
		public static void main(String[] args) {
			CaliperMain.main(ReactionsBenchmark.class, args);
		}
	}
	
	// ===================================================================
	
	private Real2 calculateOriginOfText(Real2 centre, double fontSize, SVGText text) {
		text.setFontSize(fontSize);
		double xFontScale = 1.5;
		double yFontScale = 0.7;
		Real2 nOffset = new Real2(-text.getSVGXFontWidth() * 0.001 * text.getFontSize() * xFontScale/ 2.0, text.getFontSize()  * yFontScale / 2.0);
		Real2 nOrigin = centre.plus(nOffset);
		//LOG.debug("orig "+nOrigin);
		text.setXY(nOrigin);
		return nOrigin;
	}

	@Test
	@Ignore ("Needs expansion of SVG to CMLLabel")
	public void testMethoxymethane1() {
		ChemistryBuilder geometryBuilder = new ChemistryBuilder(SVGElement.readAndCreateSVG(new File(Fixtures.MOLECULES_DIR, "methoxymethane1.svg")));
		MoleculeCreator creator = new MoleculeCreator(geometryBuilder);
		CMLMolecule mol = creator.createMolecule();
		mol.debug("mol");
		Assert.assertEquals(2, mol.getAtomCount());
	}

	@Test
	public void testMethoxymethane1Label() throws Exception {
		CMLMolecule molecule = (CMLMolecule) new CMLBuilder().build(Fixtures.METHOXYMETHANE1_LABEL_CML).getRootElement();
		Assert.assertNotNull("mol", molecule);
	}

	@Test
	public void testReadGroups() throws Exception {
		GroupList groupList = new GroupList(new FileInputStream(Fixtures.GROUPS_CML));
		CMLMolecule oMe = groupList.get("OMe");
		Assert.assertNotNull("ome", oMe);
		Assert.assertEquals("atoms",  6, oMe.getAtomCount());
		Assert.assertNull(groupList.get("OPr"));
		CMLMolecule oh = groupList.get("OH");
		Assert.assertNotNull("OH", oh);
		Assert.assertEquals("atoms", 3, oh.getAtomCount());
		CMLMolecule ho = groupList.get("HO");
		Assert.assertNotNull("HO", ho);
		Assert.assertEquals("atoms", 3, oh.getAtomCount());
	}
	
	@Test
	public void testFindGroupMeOMe() throws Exception {
		CMLMolecule meOMe = (CMLMolecule) new CMLBuilder().build(Fixtures.METHOXYMETHANE1_LABEL_CML).getRootElement();
		GroupList groupList = new GroupList(new FileInputStream(Fixtures.GROUPS_CML));
		MoleculeBuilder molBuilder = new MoleculeBuilder(groupList);
		CMLAtom atom0 = meOMe.getAtom(0);
		Assert.assertEquals("value", "OMe", atom0.getValue().trim());
		CMLMolecule group = molBuilder.getGroup(atom0);
		Assert.assertEquals("mol",  "ome", group.getId());
	}

	@Test
	public void testFindGroupKojicOHHO() throws Exception {
		CMLMolecule kojic = (CMLMolecule) new CMLBuilder().build(Fixtures.KOJIC_LABEL_CML).getRootElement();
		GroupList groupList = new GroupList(new FileInputStream(Fixtures.GROUPS_CML));
		MoleculeBuilder molBuilder = new MoleculeBuilder(groupList);
		CMLAtom atom1 = kojic.getAtom(1);
		Assert.assertEquals("value", "HO", atom1.getValue().trim());
		CMLMolecule group = molBuilder.getGroup(atom1);
		Assert.assertEquals("mol",  "ho", group.getId());
		CMLAtom atom5 = kojic.getAtom(5);
		Assert.assertEquals("value", "OH", atom5.getValue().trim());
		group = molBuilder.getGroup(atom5);
		Assert.assertEquals("mol",  "oh", group.getId());
	}

	@Test
	public void testFindGroupAndJoinMeOMe() throws Exception {
		CMLMolecule meOMe = (CMLMolecule) new CMLBuilder().build(Fixtures.METHOXYMETHANE1_LABEL_CML).getRootElement();
		GroupList groupList = new GroupList(new FileInputStream(Fixtures.GROUPS_CML));
		MoleculeBuilder molBuilder = new MoleculeBuilder(groupList);
		molBuilder.buildOnto(meOMe);
		//meOMe.debug("new");
	}
	
	@Test
	public void testFindGroupAndJoinKojic() throws Exception {
		CMLMolecule kojic = (CMLMolecule) new CMLBuilder().build(Fixtures.KOJIC_LABEL_CML).getRootElement();
		GroupList groupList = new GroupList(new FileInputStream(Fixtures.GROUPS_CML));
		MoleculeBuilder molBuilder = new MoleculeBuilder(groupList);
		molBuilder.buildOnto(kojic);
		//kojic.debug("new");
	}
	
	@Test
	public void testAddHydrogensAndComputeFormula() throws Exception {
		CMLMolecule kojic = (CMLMolecule) new CMLBuilder().build(Fixtures.KOJIC_LABEL_CML).getRootElement();
		MoleculeTool kojicTool = MoleculeTool.getOrCreateTool(kojic);
		kojicTool.adjustHydrogenCountsToValency(HydrogenControl.ADD_TO_HYDROGEN_COUNT);
		CMLFormula kojicFormula = new CMLFormula(kojic);
		Assert.assertEquals("form", "count: 1.0; charge: 0: C(6.0)H(4.0)O(2.0)R(2.0)", kojicFormula.toFormulaString());
	}
	
	@Test
	public void testAddHydrogensBuildLabels() throws Exception {
		CMLMolecule kojic = (CMLMolecule) new CMLBuilder().build(Fixtures.KOJIC_LABEL_CML).getRootElement();
		MoleculeTool kojicTool = MoleculeTool.getOrCreateTool(kojic);
		kojicTool.adjustHydrogenCountsToValency(HydrogenControl.ADD_TO_HYDROGEN_COUNT);
		GroupList groupList = new GroupList(new FileInputStream(Fixtures.GROUPS_CML));
		MoleculeBuilder molBuilder = new MoleculeBuilder(groupList);
		molBuilder.buildOnto(kojic);
		CMLFormula kojicFormula = new CMLFormula(kojic);
		Assert.assertEquals("form", "count: 1.0; charge: 0: C(6.0)H(6.0)O(4.0)", kojicFormula.toFormulaString());
	}
	
	@Test
	public void testAddHydrogensBuildLabelsAsperfuran() throws Exception {
		CMLMolecule molecule = (CMLMolecule) new CMLBuilder().build(Fixtures.ASPERFURAN_LABEL_CML).getRootElement();
		MoleculeTool moleculeTool = MoleculeTool.getOrCreateTool(molecule);
		moleculeTool.adjustHydrogenCountsToValency(HydrogenControl.ADD_TO_HYDROGEN_COUNT);
		GroupList groupList = new GroupList(new FileInputStream(Fixtures.GROUPS_CML));
		MoleculeBuilder molBuilder = new MoleculeBuilder(groupList);
		//molecule.debug("before");
		molBuilder.buildOnto(molecule);
		//molecule.debug("after");
		// add stereochem to this atom
		CMLAtom atom12 = molecule.getAtom(12);
		//atom12.debug("stereo");
		StereochemistryTool stereoTool = new StereochemistryTool(molecule);
		stereoTool.addCalculatedAtomParityForPointyAtoms();
		//molecule.debug("stereo");
		CMLFormula formula = new CMLFormula(molecule);
		Assert.assertEquals("form", "count: 1.0; charge: 0: C(14.0)H(16.0)O(3.0)", formula.toFormulaString());
	}
	
}