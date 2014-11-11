package org.xmlcml.xhtml2stm.visitor.sequence;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.Type;
import org.xmlcml.xhtml2stm.result.ResultList;
import org.xmlcml.xhtml2stm.visitable.SourceElement;
import org.xmlcml.xhtml2stm.visitable.html.HtmlContainer;
import org.xmlcml.xhtml2stm.visitable.xml.XMLContainer;
import org.xmlcml.xhtml2stm.visitor.AbstractVisitor;
import org.xmlcml.xhtml2stm.visitor.AbstractSearcher;

public class SequenceSearcher extends AbstractSearcher {

	private final static Logger LOG = Logger.getLogger(SequenceSearcher.class);

	private static final String ALL_TEXT_PATH = "//text()";
	private static final String ALL_ELEMENTS_PATH = "//*[not(*)]";
	private static final String ALL_PARA_PATH = "//*[local-name()='p']";
	private static final String ALL_DIV_PATH = "//*[local-name()='div']";

	public enum SequenceType implements Type {
		MIXED,
		CARBOHYDRATE,
		DNA,
		PROTEIN,
		RNA,
	}

	public static final String DNA_REGEX_S = "\\s*[ACGT]{6,}\\s*";
	public final static Pattern DNA_PATTERN = Pattern.compile(DNA_REGEX_S);
	public static final String PROTEIN_REGEX_S = "\\s*[ACDEFGHIKLMNPQRSTVWY]{6,}\\s*";
	public final static Pattern PROTEIN_PATTERN = Pattern.compile(PROTEIN_REGEX_S);
	public static final String RNA_REGEX_S = "\\s*[ACGU]{6,}\\s*";
	public final static Pattern RNA_PATTERN = Pattern.compile(RNA_REGEX_S);

	private SequenceType sequenceType;

	public SequenceSearcher(AbstractVisitor visitor) {
		super(visitor);
		setDefaults();
	}

	private void setDefaults() {
		this.setSequenceType(SequenceType.DNA);
//		this.addXPath(ALL_TEXT_PATH);
        this.addXPath(ALL_ELEMENTS_PATH);
//		this.addXPath(ALL_PARA_PATH); // doesn't work?
//		this.addXPath(ALL_DIV_PATH); // doesn't work?
	}

	/** sets sequenceType.
	 * 
	 * clears all previous ones.
	 * 
	 * @param type
	 */
	void setSequenceType(SequenceType type) {
		this.patternList = new ArrayList<Pattern>();
		addSequenceType0(type);
	}

	void addSequenceType(SequenceType type) {
		ensurePatternList();
		addSequenceType0(type);
	}

	private void addSequenceType0(SequenceType type) {
		if (SequenceType.DNA.equals(type)) {
			this.addPattern(DNA_PATTERN);
		} else if (SequenceType.DNA.equals(type)) {
			this.addPattern(RNA_PATTERN);
		} else {
			throw new RuntimeException("Type not supported: "+type);
		}
		this.sequenceType = (patternList.size() == 1) ? type : SequenceType.MIXED;
	}

	@Override
	protected SequenceListElement createListElement(ResultList resultSet) {
		return new SequenceListElement(sequenceType, resultSet);
	}
	
	@Override 
	protected void search(HtmlContainer htmlContainer) {
		searchXPathPatternAndCollectResults(new SourceElement(htmlContainer));
	}
	
	@Override 
	protected void search(XMLContainer xmlContainer) {
		searchXPathPatternAndCollectResults(new SourceElement(xmlContainer));
	}
	

}
