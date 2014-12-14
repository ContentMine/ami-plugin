package org.xmlcml.ami.visitable.table;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.xmlcml.ami.visitable.AbstractVisitable;
import org.xmlcml.html.HtmlTable;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.ami.util.AMIUtil;

public class TableVisitable extends AbstractVisitable {

	private List<HtmlTable> tableList;

	protected TableVisitable(PageAnalyzer pageAnalyzer) {
		super();
	}

	@Override
	public String[] getExtensions() {
		return new String[] {AMIUtil.HTM, AMIUtil.HTML};
	}
	
	@Override
	// FIXME - need to abstract this for all Visitables
	public void getMetadata() {
		for (HtmlTable table : tableList) {
			getMetadata(table);
		}
	}

	private void getMetadata(HtmlTable table) {
		// TODO Auto-generated method stub
		
	}

	public static boolean hasSuffix(String suffix) {
		return false;
	}

	@Override
	public void addFile(File file) throws Exception {
		checkFile(file);
		// no-op
	}
	
	@Override
	public void downloadParseAndAddURL(URL url) throws Exception {
		super.addURL(url);
		throw new RuntimeException("URLS for Table not yet implemented ");
	}
}
