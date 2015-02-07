package org.xmlcml.ami.visitor.tree.nexml;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;

import org.apache.log4j.Logger;
import org.xmlcml.euclid.Real2;

public class NexmlNode extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlNode.class);
	public final static String TAG = "node";
	
	private static final String ROOT = "root";
	private static final String OTU = "otu";
	private static final String X = "x";
	private static final String Y = "y";
	
	public static int DECIMAL_PLACES = 0;
	
	private List<NexmlEdge> nexmlChildEdges;
	private List<NexmlNode> nexmlChildNodes;
	private NexmlTree nexmlTree;
	private NexmlNode parentNexmlNode;

	public NexmlNode() {
		super(TAG);
	}

	/** constructor from reading nexml.
	 * 
	 */
	public NexmlNode(NexmlTree nexmlTree) {
		super(TAG);
		this.nexmlTree = nexmlTree;
	}

	public void setRoot(String bool) {
		this.addAttribute(new Attribute(ROOT, bool));
	}

	public void setOtu(String otuId) {
		this.addAttribute(new Attribute(OTU, otuId));
	}

	public void setXY2(Real2 xy) {
		if (xy != null) {
			this.addAttribute(new Attribute(X, String.valueOf(xy.getX())));
			this.addAttribute(new Attribute(Y, String.valueOf(xy.getY())));
		}
	}

	public Real2 getXY2() {
		Real2 r2 = null;
		String x = this.getAttributeValue(X);
		String y = this.getAttributeValue(Y);
		if (x != null && y != null) {
			r2 = Real2.createFromString("("+x+","+y+")");
			r2.format(DECIMAL_PLACES);
		}
		return r2;
	}

	public boolean isRoot() {
		return getAttributeValue(ROOT) != null;
	}

	public void addChildEdge(NexmlEdge childEdge) {
		ensureChildEdgesAndNodes();
		this.nexmlChildEdges.add(childEdge);
		NexmlNode childNode = nexmlTree.getNode(childEdge.getTargetId());
		if (childNode != null) {
			childNode.setParentNexmlNode(this);
			this.addChildNexmlNode(childNode);
		}
	}

	void addChildNexmlNode(NexmlNode childNode) {
		ensureChildEdgesAndNodes();
		nexmlChildNodes.add(childNode);
	}

	void setParentNexmlNode(NexmlNode nexmlNode) {
		this.parentNexmlNode = nexmlNode;
	}
	
	NexmlNode getParentNexmlNode() {
		return parentNexmlNode;
	}

	private void ensureChildEdgesAndNodes() {
		if (this.nexmlChildEdges == null) {
			this.nexmlChildEdges = new ArrayList<NexmlEdge>();
			this.nexmlChildNodes = new ArrayList<NexmlNode>();
		}
	}

	public String getNewick() {
		LOG.debug(this.getId());
		StringBuilder sb = new StringBuilder();
		getNexmlChildNodes();
		if (nexmlChildNodes.size() > 0) {
			sb.append("(");
			for (int i = 0; i < nexmlChildNodes.size(); i++) {
				NexmlNode childNode = nexmlChildNodes.get(i);
				sb.append(childNode.getNewick());
				if (i < nexmlChildNodes.size() - 1) {
					sb.append(",");
				}
			}
			sb.append(")");
		}
		sb.append(this.getId());
		if (parentNexmlNode != null) {
			Double distance = this.getDistance(parentNexmlNode);
			if (distance != null) {
				sb.append(":"+distance);
			}
		}
		return sb.toString();
	}

	private Double getDistance(NexmlNode parentNexmlNode) {
		Double d = null;
		Real2 xy2 = this.getXY2();
		if (parentNexmlNode != null && xy2 != null) {
			Real2 parentXy2 = parentNexmlNode.getXY2();
			d = parentXy2 == null ? null : xy2.getX() - parentXy2.getX();
		}
		return d;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getId());
		sb.append("; p: "+getParentNexmlId()+"; ");
		ensureChildEdgesAndNodes();
		if (nexmlChildNodes.size() > 0) {
			sb.append("[");
			for (int i = 0; i < nexmlChildNodes.size(); i++) {
				sb.append(nexmlChildNodes.get(i).getId()+" ");
			}
			sb.append("]");
		}
		return sb.toString();
	}

	private String getParentNexmlId() {
		return parentNexmlNode == null ? null : parentNexmlNode.getId(); 
	}

	public List<NexmlNode> createDescendantNodes() {
		List<NexmlNode> nodes = new ArrayList<NexmlNode>();
		addNodes(this, nodes);
		return nodes;
	}

	public List<NexmlEdge> createDescendantEdges() {
		List<NexmlEdge> edges = new ArrayList<NexmlEdge>();
		addEdges(this, edges);
		return edges;
	}

	private void addNodes(NexmlNode node, List<NexmlNode> nodes) {
		List<NexmlNode> childNodes = node.getNexmlChildNodes();
		for (NexmlNode childNode : childNodes) {
			nodes.add(childNode);
			addNodes(childNode, nodes);
		}
	}

	private void addEdges(NexmlNode node, List<NexmlEdge> edges) {
		List<NexmlEdge> childEdges = node.getNexmlChildEdges();
		for (NexmlEdge childEdge : childEdges) {
			edges.add(childEdge);
		}
		List<NexmlNode> childNodes = node.getNexmlChildNodes();
		for (NexmlNode childNode : childNodes) {
			addEdges(childNode, edges);
		}
	}

	private List<NexmlNode> getNexmlChildNodes() {
		ensureChildEdgesAndNodes();
		return nexmlChildNodes;
	}
	
	private List<NexmlEdge> getNexmlChildEdges() {
		ensureChildEdgesAndNodes();
		return nexmlChildEdges;
	}
}
