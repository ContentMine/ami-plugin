package org.xmlcml.ami.visitable.txt;

import java.io.File;
import java.net.URL;
import java.util.List;

import nu.xom.Element;
import nu.xom.Nodes;

import org.xmlcml.ami.visitable.VisitableContainer;

public class TextContainer extends VisitableContainer {

	List<String> lineList;

	public TextContainer(File file, List<String> lineList) {
		this.file = file;
		this.lineList = lineList;
	}

	public TextContainer(URL url, List<String> lineList) {
		this.url = url;
		this.lineList = lineList;
	}

	public List<String> getLineList() {
		return lineList;
	}
	
	@Override
	public String getName() {
		return file == null ? null : file.getAbsolutePath();
	}

	@Override
	public String getDoi() {
		return null;
	}

}
