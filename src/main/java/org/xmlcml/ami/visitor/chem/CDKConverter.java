package org.xmlcml.ami.visitor.chem;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import nu.xom.Element;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.silent.ChemFile;
import org.xmlcml.graphics.svg.SVGUtil;

import uk.ac.cam.ch.ucc.svggraphics2d.SVGRenderer;

public class CDKConverter {

	public static IAtomContainer cmlToMolecule(Element cmlMol) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(cmlMol.toXML().getBytes());
		CMLReader r = new CMLReader(bais);
		IChemFile cf = (IChemFile) r.read(new ChemFile());
		r.close();
		IAtomContainer mol = cf.getChemSequence(0).getChemModel(0).getMoleculeSet().getAtomContainer(0);
		//new HydrogenAdder().addImplicitHydrogensToSatisfyValency(mol);
		return mol;
	}

	public static void outputSVGAndPNG(Element mol, String filename) throws Exception {
		int outputImageWidth = 500;
		int outputImageHeight = 500;
		
	
		IAtomContainer cdkMol = CDKConverter.cmlToMolecule(mol);
	
		Rectangle drawArea = new Rectangle(outputImageWidth, outputImageHeight);
		BufferedImage image = new BufferedImage(outputImageWidth, outputImageHeight, BufferedImage.TYPE_INT_RGB);
	
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		sdg.setMolecule(cdkMol);
		try {
			sdg.generateCoordinates();
		} catch (Exception e) {
			
		}
		cdkMol = sdg.getMolecule();
	
		List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();
		generators.add(new BasicSceneGenerator());
		generators.add(new BasicBondGenerator());
		generators.add(new BasicAtomGenerator());
	
		RendererModel model = new RendererModel();
		model.registerParameters(generators.get(0));
		model.registerParameters(generators.get(1));
		model.registerParameters(generators.get(2));
		//model.set(ShowExplicitHydrogens.class, false);
		AtomContainerRenderer renderer = new AtomContainerRenderer(model, generators, new AWTFontManager());
		renderer.setup(cdkMol, drawArea);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		SVGRenderer svgR = new SVGRenderer(g2);
		Rectangle bounds = renderer.calculateDiagramBounds(cdkMol);
		renderer.setZoomToFit(drawArea.width, drawArea.height, bounds.width, bounds.height);
		renderer.paint(cdkMol, new AWTDrawVisitor(svgR));
		g2.fillRect(0, 0, outputImageWidth, outputImageHeight);
		SVGUtil.debug(svgR.getSVG(), new FileOutputStream(filename + ".svg"), 0);
		renderer.paint(cdkMol, new AWTDrawVisitor(g2));
		ImageIO.write(image, "png", new FileOutputStream(filename + ".png"));
	}

}
