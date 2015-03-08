package org.xmlcml.ami.plugin.plugins.regex;

import org.apache.log4j.Logger;
import org.xmlcml.ami.plugin.AbstractAMIElement;
import org.xmlcml.ami.plugin.result.SimpleResultWrapper;

/** holds a regexComponent and the matcher results.
 * 
 * @author pm286
 *
 */
public class RegexResultElement extends AbstractAMIElement {


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
	
	public RegexResultElement(RegexComponent regexComponent, MatcherResult matcherResult) {
		this();
		this.regexComponent = regexComponent;
		this.matcherResult = matcherResult;
		this.appendChild(regexComponent.createElement());
		this.appendChild(matcherResult.createElement());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("regex: "+regexComponent.toString()+"; "+matcherResult.toString());
		return sb.toString();
	}

	public SimpleResultWrapper getSimpleResult() {
		throw new RuntimeException("NYI");
	}


}
