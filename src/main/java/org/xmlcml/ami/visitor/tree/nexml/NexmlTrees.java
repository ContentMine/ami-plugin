package org.xmlcml.ami.visitor.tree.nexml;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xml.XMLUtil;

public class NexmlTrees extends NexmlElement {

	private final static Logger LOG = Logger.getLogger(NexmlTrees.class);
	public final static String TAG = "trees";

	/** constructor.
	 * 
	 */
	public NexmlTrees() {
		super(TAG);
	}

	public void setOtus(String otus) {
		this.addAttribute(new Attribute("otus", otus));
	}

	public List<NexmlTree> getTreeList() {
		List<Element> elementList = XMLUtil.getQueryElements(this, "./*[local-name()='tree']");
		List<NexmlTree> treeList = new ArrayList<NexmlTree>();
		for (Element element : elementList) {
			treeList.add((NexmlTree) element);
		}
		return treeList;
	}
	
}
