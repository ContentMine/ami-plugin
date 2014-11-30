package org.xmlcml.ami.visitor.species;

import java.util.regex.Pattern;

/** a species of form "Genus species" or "G. species"
 * 
 * @author pm286
 *
 */
public class LinneanName {

	public static final String GENUS_REGEX_S = "[A-Z]([a-z]+|\\.)\\s*";
	public final static Pattern GENUS_PATTERN = Pattern.compile(GENUS_REGEX_S);
	public static final String BINOMIAL_REGEX_S = "[A-Z]([a-z]+|\\.)\\s+[a-z][a-z]+";
	public final static Pattern BINOMIAL_PATTERN = Pattern.compile(BINOMIAL_REGEX_S);


	private String genus;
	private String speciesString;
	
	public LinneanName(String genus, String species) {
		this.genus = genus;
		this.speciesString = species;
	}
	
	/** is genus of form "X."
	 * 
	 * @return
	 */
	public boolean isSingleCharacterGenus() {
		return genus.length() == 2 &&
				Character.isUpperCase(genus.charAt(0)) &&
				genus.charAt(1) == '.';
	}

	protected String getGenus() {
		return genus;
	}

	protected String getSpeciesString() {
		return speciesString;
	}
	
	@Override
	public String toString() {
		return genus+" "+speciesString;
	}
}
