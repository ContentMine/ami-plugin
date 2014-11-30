package org.xmlcml.ami.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** tries to generate a URL from a DOI
 * 
 * Example: 
 * PLOSONE: doi:10137/journal.pone.0031314 =>=>
 * http://www.plosone.org/article/fetchObject.action?uri=info%3Adoi%2F10.1371%2Fjournal.pone.0031314&representation=PDF
 * 
 * Horribly tacky, but scholpub is horribly tacky
 * 
 * This should be subclassed RSN.
 * 
 * @author pm286
 *
 */
public class DOI2URL {

	private static final String HTTP_WWW_BIOMEDCENTRAL_COM = "http://www.biomedcentral.com/";

	private static final String REPRESENTATION_SUFFIX = "&representation=";

	public enum Publisher {
		BMC,
		PLOSONE,
	}
	
	public enum Representation {
		HTML,
		PDF,
		XML,
	}
	
	private final static String PLOS_PREFIX = 
			"http://www.plosone.org/article/fetchObject.action?uri=info%3Adoi%2F10.1371%2Fjournal.pone";

	private static final Pattern DOI_PATTERN = Pattern.compile("doi:(\\d{2}\\.\\d{4})/(\\d{4}\\-\\d{4}\\-(\\d+)\\-(\\d+)");
	private static final String PDF = "pdf";
	private static final String XML = "xml";
	
	private Publisher publication;
	private Representation representation;
	private String publisher;
	private String issn;
	private String article;
	private String contentType = "";
	
	public DOI2URL(Publisher journal) {
		this.publication = journal;
	}
	
	public void setRepresentation(Representation representation) {
		this.representation = representation;
	}
	
	public String createURL(String doi) {
		String url = null;
		if (Publisher.PLOSONE.equals(publication)) {
			url = createPlosOneURL(doi);
		} else if (Publisher.BMC.equals(publication)) {
			url = createBMCURL(doi);
		}
		return url;
	}

	/**
	 * doi:10.1186/1475-2875-12-430
	 * 
	 * 40 per month
	 * 
	 * 	 * doi:10.1186/1475-2875-12-43

	 * http://www.malariajournal.com/content/12/February/2013
	 * http://www.malariajournal.com/content/12/1/80 (these are article numbers)
	 * http://www.malariajournal.com/content/pdf/1475-2875-12-43.pdf
	 * http://www.malariajournal.com/content/12/1/43 (HTML)
	 * http://www.malariajournal.com/content/supplementary/1475-2875-12-43-s1.pdf (supp file)
	 * 
	 * http://worldcat.org/issn/1471-2148 may resolve journal
	 * 
	 * http://www.biomedcentral.com/1471-2148/10/36
	 * ==> 
	 * http://www.biomedcentral.com/content/pdf/1471-2148-10-36.pdf
	 * 
	 * @param doi
	 * @return
	 */
	private String createBMCURL(String doi) {
		String url = null;
		Matcher matcher = DOI_PATTERN.matcher(doi);
		if (matcher.matches()) {
			publisher = matcher.group(1);
			issn = matcher.group(2);
			article = matcher.group(3);
			String type = "";
			if (PDF.equalsIgnoreCase(representation.toString()) ||
					XML.equalsIgnoreCase(representation.toString())) {
				type = representation.toString().toLowerCase();
			}
			url = ("".equals(type)) ? HTTP_WWW_BIOMEDCENTRAL_COM+issn+"-"+article :
				HTTP_WWW_BIOMEDCENTRAL_COM+"/content/"+type+"/"+issn+"-"+article+"."+type;
		}
		return url;
	}

	private String createPlosOneURL(String doi) {
		int idx = doi.lastIndexOf(".");
		String articleNumber = doi.substring(idx); // retains dot
		return PLOS_PREFIX+articleNumber+REPRESENTATION_SUFFIX+representation;
	}
	
}
