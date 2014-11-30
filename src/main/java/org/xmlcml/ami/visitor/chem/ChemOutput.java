package org.xmlcml.ami.visitor.chem;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLAtomParity;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.element.CMLReaction;
import org.xmlcml.cml.element.CMLScalar;
import org.xmlcml.graphics.svg.SVGUtil;

public class ChemOutput {
	
	private static final String PRODUCT_SUFFIX = ".product";
	private static final String REACTANT_SUFFIX = ".reactant";
	private File outputDirectory;

	public ChemOutput(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	private final static Logger LOG = Logger.getLogger(ChemOutput.class);

	private void flipParities(Element object) {
		if (((CMLMolecule) object).getAtomArray() != null) {
			for (CMLAtom a : ((CMLMolecule) object).getAtomArray().getAtoms()) {
				try {	
					double oldParity = ((CMLAtomParity) a.getChildCMLElements("atomParity").get(0)).getXMLContent();
					((CMLAtomParity) a.getChildCMLElements("atomParity").get(0)).setXMLContent(-oldParity);
				} catch (IndexOutOfBoundsException e) {
					
				}
			}
		}
	}

	public void outputReactions(Collection<CMLReaction> reactions, String baseName) {
		Iterator<CMLReaction> reactionsIt = reactions.iterator();
		for (int i = 0; i < reactions.size(); i++) {
			CMLReaction r = reactionsIt.next();
			LOG.debug("Reaction with " + (r.getReactantList() == null ? "0" : r.getReactantList().getReactantElements().size()) + " reactants and " + (r.getProductList() == null ? "0" : r.getProductList().getProductElements().size()) + " products");
			String reactionLabels = "";
			if (r.getConditionListElements().size() > 0) {
				for (CMLElement e : r.getConditionListElements().get(0).getChildCMLElements()) {
					reactionLabels += (reactionLabels.equals("") ? "" : ", ") + ((CMLScalar) e).getString();
				}
			}
			String reactantString = (r.getReactantList() != null && r.getReactantList().getReactantElements().get(0).getLabelElements().size() > 0 ? r.getReactantList().getReactantElements().get(0).getLabelElements().get(0).getCMLValue() : "");
			String productString = (r.getProductList() != null && r.getProductList().getProductElements().get(0).getLabelElements().size() > 0 ? r.getProductList().getProductElements().get(0).getLabelElements().get(0).getCMLValue() : "");
			LOG.trace(reactantString + "-(" + reactionLabels + ")->" + productString);
			saveCMLSVGAndPNG(r, new File(outputDirectory, baseName + i + ".cml"));
		}
	}

	public void outputMolecules(Collection<CMLMolecule> molecules, String baseName) {
		Iterator<CMLMolecule> moleculeIt = molecules.iterator();
		LOG.trace("Saving molecules");
		for (int i = 0; i < molecules.size(); i++) {
			CMLMolecule m = moleculeIt.next();
			//m.addName(svgContainer.getFile().getAbsolutePath());
			saveCMLSVGAndPNG(m, new File(outputDirectory, baseName + i + ".cml"));
		}
	}

	private void saveCMLSVGAndPNG(Element object, File file) {
		try {
			LOG.debug("Writing CML to " + file);
			SVGUtil.debug(object, new FileOutputStream(file), 0);
			
			if (object instanceof CMLMolecule) {
				LOG.debug("Writing CMLMolecule SVG and PNG");
				try {
					flipParities(object);
					String fileName = file.getAbsolutePath();
					CDKConverter.outputSVGAndPNG(object, fileName);
				} catch (Throwable t) {
					throw new RuntimeException("Unexpected exception: ", t);
				}
				
			} else if (object instanceof CMLReaction) {
				LOG.trace("Writing CMLReaction SVG and PNG");
				try {
					CMLMolecule reactant = ((CMLReaction) object).getReactantList().getReactantElements().get(0).getMolecule();
					flipParities(reactant);
					String fileName = file.getAbsolutePath() + REACTANT_SUFFIX;
					LOG.trace("Writing reactant files");
					CDKConverter.outputSVGAndPNG(reactant, fileName);
				} catch (Throwable t) {
					//TODO handle real errors (normal error is that there is no reactant); here and below
				}
				try {
					CMLMolecule product = ((CMLReaction) object).getProductList().getProductElements().get(0).getMolecule();
					flipParities(product);
					String fileName = file.getAbsolutePath() + PRODUCT_SUFFIX;
					LOG.trace("Writing product files");
					CDKConverter.outputSVGAndPNG(product, fileName);
				} catch (Throwable t) {
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}