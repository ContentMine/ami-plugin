package org.xmlcml.ami.visitor.tree;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;

public class TreeActionOld {


	private final static Logger LOG = Logger.getLogger(TreeActionOld.class);
	
	private static final String NEXML_ATT = "nexml";
	static final String SVG_LINE = ".//svg:line";

	double eps = 0.01;
	private List<SVGXTree> treeList;

	private Integer minNodeCount;
	
//	public TreeAction(AbstractActionElement pageActionCommand) {
//		super(pageActionCommand);
//	}
	
//	@Override
	public void run() {
//		String xpath = getXPath();
//		List<SVGElement> elements = null;
//		SVGSVG svg = getSVGPage();
//		minNodeCount = getInteger(TreeElement.MIN_NODE_COUNT, 3);
//		if (xpath == null) {
//			elements = Arrays.asList(new SVGElement[]{svg});
//		} else {
//			elements = SVGUtil.getQuerySVGElements(svg, xpath);
//		}
//		makeTreeList(elements);
//		writeSVGFiles(elements);
//		String nexmlFilename = expandVariables(getActionCommandElement().getAttributeValue(NEXML_ATT));
//		if (nexmlFilename != null) {
//			if (nexmlFilename != null) {
//				for (SVGXTree tree : treeList) {
//					Element nexml = tree.getNEXML();
//					writeNEXMLFile(nexmlFilename, nexml);
//				}
//			}
//		}
	}

//	private void writeSVGFiles(List<SVGElement> elements) {
//		String filename = getFilename();
//		if (filename != null) {
//			for (SVGElement element : elements) {
//				String filename1 = filename+"."+element.getId()+PConstants.SVG;
//				GraphUtil.writeFileAsSVGSVGWithMouse(filename1, element);
//			}
//		}
//	}

	private void writeNEXMLFile(String filename, Element nexml) {
//		String filename1 = filename+"."+nexml.getAttributeValue(PConstants.ID)+PConstants.NEXML;
//		XMLUtil.outputQuietly(nexml, new File(filename1), 1);
	}

	private void makeTreeList(List<SVGElement> elements) {
		ensureTreeList();
		for (SVGElement svgElement : elements) {
			SVGXTree tree = makeTree((SVGG)svgElement);
			if (tree.getNodeList().size() > minNodeCount) {
				treeList.add(tree);
				LOG.trace(">>> "+tree.createNewick());
//				addMark(svgElement, getMark());
			}
		}
	}

//	private void addMark(SVGElement svgElement, String mark) {
//		if (mark != null) {
////			svgElement.addAttribute(new Attribute(AbstractActionElement.MARK, mark));
//		}
//	}

	private void ensureTreeList() {
		if (treeList == null) {
			treeList = new ArrayList<SVGXTree>();
		}
	}

	private SVGXTree makeTree(SVGG svgg) {
		SVGXTree tree = new SVGXTree(svgg);
		tree.createFromChildren(svgg, SVG_LINE, eps);
		tree.setId(svgg.getId());
		return tree;
	}

}
