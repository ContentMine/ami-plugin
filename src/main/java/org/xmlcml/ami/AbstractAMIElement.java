/**
 *    Copyright 2013 Peter Murray-Rust.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.xmlcml.ami;

import java.io.IOException;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.SourceElement;
import org.xmlcml.xml.XMLConstants;
import org.xmlcml.xml.XMLUtil;

/** base class for elements returned by searches
 * 
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public abstract class AbstractAMIElement extends Element implements XMLConstants {

	private final static Logger LOG = Logger.getLogger(AbstractAMIElement.class);

	private static final String ID = "id";
	private static final String TITLE = "title";

	private static final String AMI_NS = "http://www.xml-cml.org/ami";

	public static String[] tags = {
		"searchResults", 
		"result", 
	};
	
	public static Set<String> TAGSET;
	static {
		TAGSET = new HashSet<String>();
		for (String tag : tags) {
			TAGSET.add(tag);
		}
	};
	
	/** constructor.
	 * 
	 * @param name
	 * @param namespace
	 */
	public AbstractAMIElement(String name) {
		super(name, AMI_NS);
	}
	
	public static AbstractAMIElement create(Element element) {
		AbstractAMIElement searchElement = null;
		String tag = element.getLocalName();
		String namespaceURI = element.getNamespaceURI();
		if (!XHTML_NS.equals(namespaceURI)) {
			// might be SVG
			throw new RuntimeException("Multiple Namespaces NYI "+namespaceURI);
		} else if(SourceElement.TAG.equals(tag)) {
			searchElement = new SourceElement();
		} else {
			throw new RuntimeException("Unknown html tag "+tag);
		}
		XMLUtil.copyAttributes(element, searchElement);
		for (int i = 0; i < element.getChildCount(); i++) {
			Node child = element.getChild(i);
			if (child instanceof Element) {
				AbstractAMIElement htmlChild = AbstractAMIElement.create((Element)child);
				searchElement.appendChild(htmlChild);
			} else {
				searchElement.appendChild(child.copy());
			}
		}
		return searchElement;
		
	}
	
	public void setAttribute(String name, String value) {
		this.addAttribute(new Attribute(name, value));
	}

	public void setContent(String content) {
		this.appendChild(content);
	}
	
	public void setId(String value) {
		this.setAttribute(ID, value);
	}

	public void output(OutputStream os) throws IOException {
		XMLUtil.debug(this, os, 1);
	}

	public void debug(String msg) {
		XMLUtil.debug(this, msg);
	}

	public void setValue(String value) {
		this.removeChildren();
		this.appendChild(value);
	}

	public String getId() {
		return this.getAttributeValue(ID);
	}

	public String getTitle() {
		return this.getAttributeValue(TITLE);
	}

	public static List<AbstractAMIElement> getSelfOrDescendants(AbstractAMIElement root, String tag) {
		tag = tag.toLowerCase();
		Nodes nodes = root.query(".//*[local-name()='"+tag+"'");
		List<AbstractAMIElement> elements = new ArrayList<AbstractAMIElement>();
		for (int i = 0; i < nodes.size(); i++) {
			elements.add((AbstractAMIElement)nodes.get(i));
		}
		return elements;
	}

	public static AbstractAMIElement getSingleSelfOrDescendant(AbstractAMIElement root, String tag) {
		List<AbstractAMIElement> elements = getSelfOrDescendants(root, tag);
		return (elements.size() != 1) ? null : elements.get(0);
	}

	public static List<AbstractAMIElement> getChildElements(AbstractAMIElement root, String tag) {
		tag = tag.toLowerCase();
		Nodes nodes = root.query("./*[local-name()='"+tag+"']");
		List<AbstractAMIElement> elements = new ArrayList<AbstractAMIElement>();
		for (int i = 0; i < nodes.size(); i++) {
			elements.add((AbstractAMIElement)nodes.get(i));
		}
		return elements;
	}

	public static AbstractAMIElement getSingleChildElement(AbstractAMIElement root, String tag) {
		List<AbstractAMIElement> elements = getChildElements(root, tag);
		return (elements.size() != 1) ? null : elements.get(0);
	}



}
