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

public class PubmedLookup extends AbstractLookup {

	
	private static final Logger LOG = Logger.getLogger(PubmedLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public PubmedLookup() {
	}

	public String lookup(String genbankId) throws IOException {
//		LOG.error(" Pubmed lookup NYI");
		return null;
	}

		
}
