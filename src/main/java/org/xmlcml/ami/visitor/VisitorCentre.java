package org.xmlcml.ami.visitor;

import java.util.ArrayList;
import java.util.List;

/** organizes the visitors.
 * 
 * @author pm286
 *
 */
public class VisitorCentre {

	private List<AbstractVisitor> visitorList;

	public VisitorCentre() {
		
	}
	
	public void add(AbstractVisitor visitor) {
		ensureVisitorList();
		this.visitorList.add(visitor);
	}

	private void ensureVisitorList() {
		if (visitorList == null) {
			visitorList = new ArrayList<AbstractVisitor>();
		}
	}
}
