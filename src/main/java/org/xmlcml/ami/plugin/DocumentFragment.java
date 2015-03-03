package org.xmlcml.ami.plugin;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** Dummy needs refactoring
 * 
 * @author pm286
 *
 */
public class DocumentFragment {

	
	private static final Logger LOG = Logger.getLogger(DocumentFragment.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public void add(EIC newEic) {
		LOG.error("addEIC NYI");
	}

	public List<String> getEICList() {
		LOG.error("getEICList NYI");
		return null;
	}

}
