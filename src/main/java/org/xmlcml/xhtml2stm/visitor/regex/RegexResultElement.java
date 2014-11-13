package org.xmlcml.xhtml2stm.visitor.regex;

import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.result.AbstractResultElement;
import org.xmlcml.xhtml2stm.result.SimpleResultWrapper;
import org.xmlcml.xml.XMLUtil;

/** holds a regexComponent and the matcher results.
 * 
 * @author pm286
 *
 */
public class RegexResultElement extends AbstractResultElement {


	private final static Logger LOG = Logger.getLogger(RegexResultElement.class);
	public final static String TAG = "regex";

	private RegexComponent regexComponent;
	private MatcherResult matcherResult;

	
	/** 
	 * Constructor.
	 */
	public RegexResultElement() {
		super(TAG);
	}

	public RegexResultElement(String value) {
		this();
		appendChild(value);
	}
	
	public RegexResultElement(SimpleResultWrapper simpleResult) {
		this();
		addSimpleResultAsXML(simpleResult);
	}

	public RegexResultElement(RegexComponent regexComponent, MatcherResult matcherResult) {
		this();
		this.regexComponent = regexComponent;
		this.matcherResult = matcherResult;
		this.appendChild(regexComponent.createElement());
		this.appendChild(matcherResult.createElement());
		XMLUtil.debug(this, "RegexResult");
	}

	protected RegexResultElement createElement(SimpleResultWrapper simpleResult) {
		return new RegexResultElement(simpleResult);
	}

	public SimpleResultWrapper getSimpleResult() {
		SimpleResultWrapper simpleResult = new SimpleResultWrapper();
		simpleResult.setResultElement(this);
		return simpleResult;
	}

}
