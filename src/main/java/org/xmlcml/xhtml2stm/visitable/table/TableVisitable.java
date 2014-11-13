package org.xmlcml.xhtml2stm.visitable.table;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.xmlcml.html.HtmlTable;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.xhtml2stm.util.AMIUtil;
import org.xmlcml.xhtml2stm.visitable.AbstractVisitable;

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
		// no-op
	}
	
	@Override
	public void addURL(URL url) throws Exception {
		throw new RuntimeException("URLS for Table not yet implemented ");
	}
}
