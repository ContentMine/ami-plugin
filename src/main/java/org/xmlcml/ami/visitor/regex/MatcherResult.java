package org.xmlcml.ami.visitor.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import nu.xom.Element;
import nu.xom.IllegalNameException;

import org.apache.log4j.Logger;
import org.xmlcml.ami.result.HitsElement;

/** holds immediate result of match.
 * 
 * @author pm286
 *
 */
public class MatcherResult {
	
	private final static Logger LOG = Logger.getLogger(MatcherResult.class);
	
	private List<String> groupList;
	private List<String> fieldList;
	private RegexComponent regexComponent;
	private List<NamedGroupList> namedGroupListList;

	
	public MatcherResult(List<String> fieldList) {
		this.fieldList = fieldList;
	}

	public void add(String group) {
		ensureGroupList();
		groupList.add(group);
	}

	private void ensureGroupList() {
		if (groupList == null) {
			groupList = new ArrayList<String>();
		}
	}

	public int size() {
		ensureNamedGroupListList();
		return namedGroupListList.size();
	}

	public String get(int i) {
		ensureGroupList();
		return groupList.get(i);
	}

	NamedGroupList mapFieldsToGroups() {
		NamedGroupList namedGroupList = null;
		int gsize = groupList.size();
		if (gsize > 0 || fieldList.size() > 0) {
			if (gsize != fieldList.size()) {
				LOG.error(
					"groupList ("+gsize+"; "+groupList+") does not match fieldList ("
				    +fieldList.size()+";"+fieldList+")");
			} else {
				namedGroupList = new NamedGroupList();
				for (int i = 0; i < gsize; i++) {
					NamedGroup namedGroup = new NamedGroup(fieldList.get(i), get(i));
					LOG.trace("namedgroup "+namedGroup);
					namedGroupList.add(namedGroup);
				}
			}
		}
		return namedGroupList;
	}

	private void ensureNamedGroupListList() {
		if (namedGroupListList == null) {
			namedGroupListList = new ArrayList<NamedGroupList>();
		}
	}

	MatcherResult extractMatcherResult(Matcher matcher) {
		if (matcher.groupCount() > 0) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				add(matcher.group(i));
			}
			LOG.trace("matcherResult: "+this);
		}
		return this;
	}

	void captureNextMatch(Matcher matcher) {
		groupList = new ArrayList<String>();
		extractMatcherResult(matcher);
		NamedGroupList namedGroupList = mapFieldsToGroups();
		if (namedGroupList != null) {
			ensureNamedGroupListList();
			namedGroupListList.add(namedGroupList);
			LOG.trace("added NamedGroupList "+namedGroupList);
		}

	}

	public Element createElement() {
		ensureNamedGroupListList();
		Element hits = new HitsElement();
		for (NamedGroupList namedGroupList : namedGroupListList) {
			try {
				hits.appendChild(namedGroupList.createElement());
			} catch (IllegalNameException e) {
				LOG.error("Illegal attribute name "+e);
			}
		}
		return hits;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("MatcherResult\n");
		ensureNamedGroupListList();
		for (NamedGroupList namedGroupList : namedGroupListList) {
			sb.append(namedGroupList.toString()+"\n");
		}
		return sb.toString();
	}

}
