package org.xmlcml.ami2.lookups;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntArray;
import org.xmlcml.xml.XMLUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GenbankLookup extends AbstractLookup {

	
	private static final Logger LOG = Logger.getLogger(GenbankLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public GenbankLookup() {
	}

	/*
http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nuccore&id=34577062,24475906&rettype=fasta&retmode=text
http://www.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=gene&term="+genbank_id+"GENBANK_ID	 */
	
	public String lookup(String genbankId) throws IOException {
//		LOG.error(" Genbank lookup NYI");
		return null;
	}

		
}
