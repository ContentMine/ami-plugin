package org.xmlcml.ami.visitable;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.ami.AbstractAMIElement;

/** element describing/containing the object/s in the visitable.
 * 
 * contains reference to an abstractContainer.
 * 
 * @author pm286
 *
 */
public class SourceElement extends AbstractAMIElement {

	private final static Logger LOG = Logger.getLogger(SourceElement.class);
	
	public final static String TAG = "source";
	private static final String DOCUMENT_ID = "documentId";
	private static final String NAME = "name";

	private VisitableContainer abstractContainer;

	/** constructor.
	 * 
	 */
	public SourceElement() {
		super(TAG);
	}

	public SourceElement(VisitableContainer abstractContainer) {
		this();
		this.abstractContainer = abstractContainer;
		this.addAttributes();
	}

	public void addAttributes() {
		addName();
		addDOI();

	}

	private void addName() {
		if (abstractContainer != null) {
			String name = abstractContainer.getName();
			if (name != null) {
				this.setName(name);
			}
		}
	}
	
	private void addDOI() {
		if (abstractContainer != null) {
			String doi = abstractContainer.getDoi();
			if (doi != null) {
				this.setName(doi);
			}
		}
	}
	
	public void setDocumentId(String documentId) {
		if (documentId != null) {
			this.addAttribute(new Attribute(DOCUMENT_ID, documentId));
		}
	}

	public String getDocumentId() {
		return this.getAttributeValue(DOCUMENT_ID);
	}

	public void setName(String name) {
		if (name != null) {
			this.addAttribute(new Attribute(NAME, name));
		}
	}

	public String getName() {
		return this.getAttributeValue(NAME);
	}

	public Element getElement() {
		return abstractContainer == null ? null : abstractContainer.getElement();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(abstractContainer == null ? "No container\n" : abstractContainer.toString()+"\n");
		return sb.toString();
	}

}
