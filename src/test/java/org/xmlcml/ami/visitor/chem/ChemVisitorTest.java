package org.xmlcml.ami.visitor.chem;

import java.io.File;
import java.util.List;

import nu.xom.Nodes;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.ami.visitable.html.HtmlVisitable;
import org.xmlcml.ami.visitable.svg.SVGVisitable;
import org.xmlcml.cml.element.CMLLabel;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.Morgan;
import org.xmlcml.graphics.svg.SVGUtil;

@Ignore
//FIXME ChemVisitor needs to split creation and output

public class ChemVisitorTest {

	@Test
	public void testHtmlVisitable() throws Exception {
		HtmlVisitable htmlVisitable = new HtmlVisitable();
		htmlVisitable.addFile(Fixtures.METABOLITE_00039_HTML);
		htmlVisitable.addFile(new File(Fixtures.HTML_DIR, "pb1.html"));
		
		ChemVisitor chemVisitor = new ChemVisitor();
		chemVisitor.visit(htmlVisitable);
	}

	@Test
	public void testSVGVisitable() throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		svgVisitable.addSVGElement(new File(Fixtures.MOLECULES_DIR, "image.g.2.11.svg"));
		svgVisitable.addSVGElement(new File(Fixtures.MOLECULES_DIR, "image.g.2.13.svg"));
		
		ChemVisitor chemVisitor = new ChemVisitor();
		chemVisitor.visit(svgVisitable);
	}

	
	@Test
	@Ignore
	public void forMartin1() throws Exception {
		ChemVisitor chemVisitor = new ChemVisitor();
		SVGVisitable svgVisitable = new SVGVisitable();
		svgVisitable.addSVGFile(new File(Fixtures.MOLECULES_DIR, "image.g.2.13.svg"));
		svgVisitable.addSVGFile(new File(Fixtures.MOLECULES_DIR, "image.g.2.16.svg"));
		chemVisitor.visit(svgVisitable);
		SVGUtil.debug(chemVisitor.getMolecules().get(0), "target/chem/image.g.2.13.cml", 0);
		SVGUtil.debug(chemVisitor.getMolecules().get(1), "target/chem/image.g.2.16.cml", 0);
	}

	@Test
	@Ignore
	public void testSVGVisitableDir() throws Exception {
		List<CMLMolecule> molecules = testSVGVisitableDir(Fixtures.MOLECULES_DIR);//new File("C:/workspace/ami2-poc/target/output/firstformartin"));
		for (CMLMolecule molecule : molecules) {
			Nodes label = molecule.cmlQuery("cml:label");
			if (label.size() == 1) {
				List<String> morganOfName = PubChemCommunicator.getMorgansFromPubChem(((CMLLabel) label.get(0)).getCMLValue().replace("_", "").replace("^", ""));
				String morganOfDiagram = Morgan.createMorganStringFromMolecule(molecule);
				//Assert.assertTrue("Morgan check " + ((CMLLabel) label.get(0)).getCMLValue().replace("_", "").replace("^", ""), morganOfName.get(0).equals(morganOfDiagram) || morganOfName.get(1).equals(morganOfDiagram));
				System.out.println(molecule.getNameElements().get(0).getValue() + ": " + ((CMLLabel) label.get(0)).getCMLValue().replace("_", "").replace("^", "") + " " + (morganOfName.size() == 0 ? "not found" : morganOfName.get(0).equals(morganOfDiagram) || (morganOfName.size() == 2 && morganOfName.get(1).equals(morganOfDiagram))));
			}
		}
	}


	@Test
	public void testChemVisitor() throws Exception {
		String[] args = new String[] {
		};
		ChemVisitor.main(args);
	}

	@Test
	public void testChemVisitorArgs() throws Exception {
		String[] args = new String[] {
				"-i", new File(Fixtures.MOLECULES_DIR, "image.g.2.13.svg").toString(),
//				"-o", new File(Fixtures.AMI_OUT,"/png/ijs.0.014126-0-000.xml").toString(),
		};
		ChemVisitor.main(args);
	}

// ===============================
	
	
	public static List<CMLMolecule> testSVGVisitableDir(File dir) throws Exception {
		SVGVisitable svgVisitable = new SVGVisitable();
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.toString().endsWith(".svg")) {
					svgVisitable.addSVGFile(file);
				}
			}
		}
		ChemVisitor chemVisitor = new ChemVisitor();
		chemVisitor.visit(svgVisitable);
		return chemVisitor.getMolecules();
	}

}