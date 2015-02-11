package org.xmlcml.ami.visitor.tree.nexml;

import java.util.List;

import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

public class NexmlNEXML extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlNEXML.class);
	public final static String TAG = "nexml";
	private List<NexmlNode> rootList;
	private List<NexmlTree> treeList;

	/** constructor.
	 * 
	 */
	public NexmlNEXML() {
		super(TAG);
		this.addNamespaceDeclaration(NEX, NEXML_NS);
		this.addNamespaceDeclaration(XSI, XSI_NS);
	}

	public String createNewick() {
		StringBuilder sb = new StringBuilder();
		List<NexmlTree> treeList = getTreeList();
		for (NexmlTree tree : treeList) {
			sb.append(tree.getNewick());
		}
		sb.append(";");
		return sb.toString();
	}

	private void splitTrees() {
		List<NexmlTree>  treeList = getTreeList();
		for (NexmlTree nexmlTree : treeList) {
			List<NexmlNode> rootNodes = nexmlTree.getRootList();
			if (rootNodes.size() > 1) {
				LOG.error("splitTrees not yet written");
			}
		}
	}

	private List<NexmlTree> getTreeList() {
		NexmlTrees nexmlTrees = getTreesElement();
		treeList = nexmlTrees.getTreeList();
		return treeList;
	}

	private NexmlTrees getTreesElement() {
		List<Element> elementList = XMLUtil.getQueryElements(this, "./*[local-name()='trees']");
		return elementList.size() != 1 ? null : (NexmlTrees) elementList.get(0);
	}

	void buildTrees() {
		getTreeList();
		for (NexmlTree tree : treeList) {
			tree.buildTree();
		}
	}
	
}
