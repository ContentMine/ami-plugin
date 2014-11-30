package org.xmlcml.ami.visitor.chem;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import nu.xom.Attribute;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.euclid.Real2Range;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGUtil;
import org.xmlcml.html.HtmlA;
import org.xmlcml.html.HtmlBody;
import org.xmlcml.html.HtmlDiv;
import org.xmlcml.html.HtmlHtml;
import org.xmlcml.html.HtmlImg;
import org.xmlcml.html.HtmlSpan;
import org.xmlcml.svg2xml.page.PageIO;

public class ChemAnnotator {
	
	private final static Logger LOG = Logger.getLogger(ChemAnnotator.class);
	
	private static final String REACTION_SUFFIX = ".reaction";
	private static final String MOLECULE_SUFFIX = ".molecule";

	private static String HIDE_CHARACTER = "  ҉  ";
	
	static void copyImageFilesFromDirectoryToDirectory(File from, File to) {
		try {
			FileUtils.copyDirectory(from, to, new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().matches(".*\\.i\\d*\\.png");
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addLink(HtmlDiv div, String href, String content) {
		HtmlA pngLink = new HtmlA();
		div.appendChild(pngLink);
		pngLink.setHref(href);
		pngLink.setContent(content);
	}

	private static void addStrokes(SVGSVG topSVGElementCopy) {
		List<SVGElement> allElements = SVGUtil.getQuerySVGElements(topSVGElementCopy, "//*");
		for (SVGElement el : allElements) {
			el.addAttribute(new Attribute("stroke", "black"));
			el.addAttribute(new Attribute("stroke-width", "0.2"));
		}
	}

	public static void createClickableHTML(File output, MoleculeCreator cmlCreator) throws FileNotFoundException {
		String inputName = cmlCreator.getInputCopy().getFile().getName();
		
		ChemOutput chemOutput = new ChemOutput(output.getParentFile());
		chemOutput.outputReactions(cmlCreator.getReactions(), inputName + REACTION_SUFFIX);
		chemOutput.outputMolecules(cmlCreator.getMolecules(), inputName + MOLECULE_SUFFIX);
		
		LOG.debug("Creating clickable HTML");
		SVGSVG inputElementCopy = (SVGSVG) cmlCreator.getInputCopy().getElement();
		try {
			SVGElement topG = SVGUtil.getQuerySVGElements((SVGElement) inputElementCopy, "svg:g").get(0);
			topG.removeAttribute(topG.getAttribute("transform"));
		} catch (NullPointerException e) {
			
		}
		addStrokes(inputElementCopy);
		SVGUtil.debug(inputElementCopy, new FileOutputStream(output.getPath() + ".svg"), 0);
		//w.write("<html><body><img " + (svgContainer.getElement().getAttributeValue("width") != null ? "style=\"width: " + Double.parseDouble(svgContainer.getElement().getAttributeValue("width")) * 2 + "px\" " : "") + "src=\"" + svgContainer.getFile().getName().replace(".svg", "forhtml.svg") + "\" />");
		HtmlHtml page = new HtmlHtml();
		HtmlBody body = new HtmlBody();
		page.appendChild(body);
		HtmlImg image = new HtmlImg();
		body.appendChild(image);
		try {
			image.setAttribute("style", "width: " + Double.parseDouble(inputElementCopy.getAttributeValue("width")) * 2 + "px");
		} catch (Exception e) {
			String viewBox = inputElementCopy.getAttributeValue("viewBox");
			String[] coords = viewBox.split(" ");
			image.setAttribute("style", "width: " + (Double.parseDouble(coords[0]) + Double.parseDouble(coords[2])) * 2 + "px");
		}
		image.setSrc(output.getName() + ".svg");
		createMoleculeLabels(cmlCreator, inputName, body);
		createReactionLabels(cmlCreator, inputName, body);
		PageIO.outputFile(page, output);
	}

	private static void createMoleculeLabels(MoleculeCreator cmlCreator, String name, HtmlBody body) {
		int i = 0;
		for (Entry<Real2Range, CMLMolecule> molecule : cmlCreator.moleculeLocations.entrySet()) {
			HtmlDiv div = new HtmlDiv();
			body.appendChild(div);
			div.setId("molecule" + i);
			double left = molecule.getKey().getXMin() * 2;
			double top = molecule.getKey().getYMin() * 2 - 15;
			div.setAttribute("style", "background: white; border: 1px solid blue; position: absolute; left: " + left + "; top: " + top);
			addLink(div, name + MOLECULE_SUFFIX + i + ".cml.png", "PNG");
			div.appendChild(" ");
			addLink(div, name + MOLECULE_SUFFIX + i + ".cml.svg", "SVG");
			div.appendChild(" ");
			addLink(div, name + MOLECULE_SUFFIX + i + ".cml", "CML");
			div.appendChild(" ");
			if (molecule.getValue().getLabelElements().get(0) != null) {
				addLink(div, "http://www.ncbi.nlm.nih.gov/pccompound?term=" + molecule.getValue().getLabelElements().get(0).getCMLValue().replace("^", "").replace("_", ""), "PubChem");
				div.appendChild(" ");
			}
			HtmlSpan hide = new HtmlSpan();
			div.appendChild(hide);
			hide.setAttribute("onmouseout", "document.getElementById('molecule" + i + "').style.opacity = '1';");
			hide.setAttribute("onmouseover", "document.getElementById('molecule" + i + "').style.opacity='0.1'");
			hide.setContent(HIDE_CHARACTER);
			i++;
		}
	}

	private static void createReactionLabels(MoleculeCreator cmlCreator, String name, HtmlBody body) {
		int i = 0;
		for (Real2Range reaction : cmlCreator.arrowLocations.keySet()) {
			HtmlDiv div = new HtmlDiv();
			body.appendChild(div);
			div.setId("reaction" + i);
			double left = reaction.getCentroid().getX() * 2 - 20;
			double top = reaction.getCentroid().getY() * 2 - 8;
			div.setAttribute("style", "background: white; border: 1px solid red; position: absolute; left: " + left + "; top: " + top);
			addLink(div, name + REACTION_SUFFIX + i + ".cml", "CML");
			div.appendChild(" ");
			HtmlSpan hide = new HtmlSpan();
			div.appendChild(hide);
			hide.setAttribute("onmouseout", "document.getElementById('reaction" + i + "').style.opacity = '1';");
			hide.setAttribute("onmouseover", "document.getElementById('reaction" + i + "').style.opacity='0.1'");
			hide.setContent(HIDE_CHARACTER);
			i++;
		}
	}

}