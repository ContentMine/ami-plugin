package org.xmlcml.ami.visitor.tree.nexml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class NexmlTree extends NexmlElement {

	private static final String TYPE = "type";
	private final static Logger LOG = Logger.getLogger(NexmlTree.class);
	public final static String TAG = "tree";
	
	private List<NexmlNode> nodeList;
	private List<NexmlEdge> edgeList;
	private Map<String, NexmlNode> nodeByIdMap;
	private Multimap<String, NexmlEdge> edgeBySourceMap;
	private Multimap<String, NexmlEdge> edgeByTargetMap;
	private NexmlNode rootNode;
	private List<NexmlNode> rootList;
	private Set<NexmlNode> unusedNodeSet;
	private Set<NexmlNode> tipSet;
	private HashSet<NexmlNode> branchNodeSet;

	/** constructor.
	 * 
	 */
	public NexmlTree() {
		super(TAG);
	}

	public void setType(String type) {
		this.addAttribute(new Attribute(XSI+":"+TYPE, XSI_NS, type));
	}

	public List<NexmlNode> getNodeListAndMap() {
		if (nodeList == null) {
			nodeList = new ArrayList<NexmlNode>();
			nodeByIdMap = new HashMap<String, NexmlNode>();
			List<Element> nodeElements = XMLUtil.getQueryElements(this, "./*[local-name()='node']");
			unusedNodeSet = new HashSet<NexmlNode>();
			for (Element node : nodeElements) {
				NexmlNode nexmlNode = (NexmlNode) node;
				nodeList.add(nexmlNode);
				nodeByIdMap.put(nexmlNode.getId(), nexmlNode);
				unusedNodeSet.add(nexmlNode);
			}
		}
		return nodeList;
	}
	
	public List<NexmlEdge> getEdgeListAndMaps() {
		if (edgeList == null) {
			edgeList = new ArrayList<NexmlEdge>();
			List<Element> edgeElements = XMLUtil.getQueryElements(this, "./*[local-name()='edge']");
			edgeByTargetMap = ArrayListMultimap.create();
			edgeBySourceMap = ArrayListMultimap.create();
			for (Element edge : edgeElements) {
				NexmlEdge nexmlEdge = (NexmlEdge) edge;
				edgeList.add(nexmlEdge);
				edgeBySourceMap.put(nexmlEdge.getSourceId(), nexmlEdge);
				edgeByTargetMap.put(nexmlEdge.getTargetId(), nexmlEdge);
			}
		}
		return edgeList;
	}
	
	private void addChildEdges(NexmlNode node) {
		if (!tipSet.contains(node) && !branchNodeSet.contains(node)) {
			String nodeId = node.getId();
			LOG.debug("adding id "+nodeId);
			List<NexmlEdge> childEdges = getSourceEdges(nodeId);
			if (childEdges.size() == 0) {
				tipSet.add(node);
			} else {
				branchNodeSet.add(node);
			}
			for (NexmlEdge childEdge : childEdges) {
				String childId = childEdge.getTargetId();
				NexmlNode childNode = getNode(childId);
				childNode.setParentNexmlNode(node);
				node.addChildNexmlNode(childNode);
				this.addChildEdges(childNode);
			}
		}
	}

	NexmlNode getNode(String id) {
		return nodeByIdMap.get(id);
	}

	List<NexmlEdge> getSourceEdges(String sourceId) {
		List<NexmlEdge> edgeList = new ArrayList<NexmlEdge>(edgeBySourceMap.get(sourceId));
		LOG.trace("E> "+sourceId+"; "+edgeList);
		return new ArrayList<NexmlEdge>(edgeBySourceMap.get(sourceId));
	}

	List<NexmlEdge> getTargetEdges(String target) {
		return new ArrayList<NexmlEdge>(edgeByTargetMap.get(target));
	}

	/** checks relationship of nodes and edges and adds parent/child to nodes.
	 * 
	 */
	public void buildTree() {
		getNodeListAndMap();
		getEdgeListAndMaps();
		tipSet = new HashSet<NexmlNode>();
		branchNodeSet = new HashSet<NexmlNode>();
		while(unusedNodeSet.size() > 0) {
			NexmlNode nextNode = unusedNodeSet.iterator().next();
			unusedNodeSet.remove(nextNode);
			List<NexmlEdge> edges = getSourceEdges(nextNode.getId());
			if (edges.size() > 0) {
				addChildEdges(nextNode);
			}
		}
		LOG.debug(tipSet);
		LOG.debug(branchNodeSet);
		List<NexmlNode> rootNodes = getRootList();
		if (rootNodes.size() == 0) {
			LOG.error("NO Root nodes");
		} else if (rootNodes.size() == 1) {
			this.rootNode = rootNodes.get(0);
		} else {
			this.rootNode = rootNodes.get(0);
//			rootNodes.remove(rootNode);
			for (int i = 1; i < rootNodes.size(); i++) {
				NexmlTree newTree = new NexmlTree();
				newTree.rootNode = rootNodes.get(i);
				newTree.transferNodeAndDescendants(newTree.rootNode, nodeList);
			}
		}
	}

	private void transferNodeAndDescendants(NexmlNode rootNode,	List<NexmlNode> nodeList) {
		this.nodeList = new ArrayList<NexmlNode>();
		this.edgeList = new ArrayList<NexmlEdge>();
		List<NexmlNode> descendantNodeList = rootNode.createDescendantNodes();
		for (NexmlNode node : descendantNodeList) {
			this.nodeList.add(node);
		}
		List<NexmlEdge> descendantEdgeList = rootNode.createDescendantEdges();
		for (NexmlEdge edge : descendantEdgeList) {
			this.edgeList.add(edge);
		}
	}

	public String getNewick() {
		getRootNode();
		return rootNode.getNewick();
	}

	private NexmlNode getRootNode() {
		if (rootNode == null) {
			LOG.error("No root Node");
			getRootList();
		}
		return rootNode;
	}

	public List<NexmlNode> getRootList() {
		if (rootList == null) {
			rootList = new ArrayList<NexmlNode>();
			for (NexmlNode node : nodeList) {
				if (node.getParentNexmlNode() == null) {
					rootList.add(node);
				}
			}
			LOG.debug("rootList "+rootList.size());
		}
		return rootList;
	}

}
