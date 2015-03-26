package org.xmlcml.ami2.lookups;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import nu.xom.Element;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.euclid.IntArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class AbstractLookup {
	
	private static final Logger LOG = Logger.getLogger(AbstractLookup.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private HashMap<String, String> lookupRefByMatch;
	
	public AbstractLookup() {
		
	}
	
	public abstract String lookup(String key) throws IOException;

	protected IntArray getIdentifierArray(JsonElement jsonElement, String arrayName) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		JsonArray itemsArray = jsonObject.getAsJsonArray(arrayName);
		int size = itemsArray.size();
		IntArray intArray = new IntArray();
		for (int i = 0; i < size; i++) {
			intArray.addElement(((JsonElement)itemsArray.get(i)).getAsInt());
		}
		return intArray;
	}

	protected Element getXML(URL url) throws IOException {
		String content = this.getString(url);
		Element element = org.xmlcml.xml.XMLUtil.parseXML(content);
		return element;
	}

	/** gets content of URL as a string.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
    protected String getString(URL url) throws IOException {
    	
        URLConnection urlc = url.openConnection();
        //use post mode
        urlc.setDoOutput(true);
        urlc.setAllowUserInteraction(false);
        return IOUtils.toString(urlc.getInputStream());
    }

	public Map<String, String> getOrCreateLookupRefByMatch() {
		if (lookupRefByMatch == null) {
			lookupRefByMatch = new HashMap<String, String>();
		}
		return lookupRefByMatch;
	}

		
}
