package org.xmlcml.xhtml2stm.visitable;

import nu.xom.Attribute;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractXHTML2STMElement;

/** element describing/containing the object/s in the visitable.
 * 
 * contains reference to an abstractContainer.
 * 
 * @author pm286
 *
 */
public class SourceElement extends AbstractXHTML2STMElement {

	private final static Logger LOG = Logger.getLogger(SourceElement.class);
	
	public final static String TAG = "source";
	private static final String DOCUMENT_ID = "documentId";
	private static final String NAME = "name";

	private String key;
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

	private void ensureKey() {
		if (key == null) {
			key = String.valueOf(getDocumentId());
		}
	}

	public String getKey() {
		ensureKey();
		return key;
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


}
