package org.xmlcml.ami.visitor.chem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.xmlcml.cml.base.CMLElement;
import org.xmlcml.cml.converters.molecule.mdl.SDF2CMLConverter;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMolecule;
import org.xmlcml.cml.tools.Morgan;

public class PubChemCommunicator {
	
	/*static {
		System.setProperty("org.apache.commons.logging.Log", value)
	}*/
	
	/*public void getPubChemInfo() throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://pubchem.ncbi.nlm.nih.gov/pug/pug.cgi");
		List<NameValuePair> params = new ArrayList<NameValuePair>(1);
		params.add(new BasicNameValuePair("test", "test"));
		httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpResponse response = httpClient.execute(httpPost);
		try {
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			
			EntityUtils.consume(entity);
		} finally {
			response.close();
		}
	}*/
	
	public static List<String> getMorgansFromPubChem(String search) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		URIBuilder uri = new URIBuilder();
		uri.setScheme("https");
		uri.setHost("pubchem.ncbi.nlm.nih.gov/");
		uri.setPath("rest/pug/compound/name/" + search + "/SDF");
		HttpGet httpGet = new HttpGet(uri.toString());//"https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/" + .setHost(host)encode(search, "UTF-8") + "/cids/TXT");
		CloseableHttpResponse response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		/*BufferedReader content = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line;
		List<String> lines = new ArrayList<String>();
		while ((line = content.readLine()) != null) {
			lines.add(line);
		}
		System.out.println(content.readLine());
		System.out.println(content.readLine());
		System.out.println(content.readLine());*/
		SDF2CMLConverter converter = new SDF2CMLConverter();
		CMLCml molecules = new CMLCml();
		try {
			// FIXME
//			molecules = (CMLCml) converter.convertToXML(entity.getContent());
			throw new RuntimeException("Problem loading class");
		} catch (RuntimeException e) {
			
		}
		EntityUtils.consume(entity);
		response.close();
		List<String> results = new ArrayList<String>();
		for (CMLElement e : molecules.getChildCMLElements()) {
			results.add(Morgan.createMorganStringFromMolecule((CMLMolecule) e));
		}
		return results;
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		System.out.println(getMorgansFromPubChem("cyclopiazonic acid").get(0));
	}
	
}
