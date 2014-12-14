package org.xmlcml.ami.visitable.svg;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.AbstractVisitable;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGImage;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGShape;
import org.xmlcml.graphics.svg.SVGText;

public class SVGVisitable extends AbstractVisitable {

	private final static Logger LOG = Logger.getLogger(SVGVisitable.class);

	private static final String SVG = "svg";
	
	private List<SVGContainer> svgContainerList;

	public void addSVGElement(File file, SVGElement svgElement) {
		ensureSVGContainerList();
		SVGContainer svgContainer = new SVGContainer(file, svgElement);
		svgContainerList.add(svgContainer);
	}

	/*@Override
	public void addURL(URL url) throws Exception {
		ensureHtmlContainerList();
		HtmlContainer htmlContainer = new HtmlContainer(url, HtmlUtil.readAndCreateElement(url));
		htmlContainerList.add(htmlContainer);
	}*/

	private void ensureSVGContainerList() {
		if (svgContainerList == null) {
			svgContainerList = new ArrayList<SVGContainer>();
		}
	}

	public void addSVGFile(File file) {
		ensureFileList();
		fileList.add(file);
		addSVGElement(file, SVGElement.readAndCreateSVG(file));
	}

	@Override
	public void addFile(File file) {
		checkFile(file);
		addSVGFile(file);
	}

	public List<File> getFileList() {
		return fileList;
	}

	public void /*List<SVGElement>*/ createSVGFromFiles() {
		//svgList = new ArrayList<SVGElement>();
		if (fileList != null) {
			for (File file : fileList) {
				try {
					SVGElement svgElement = SVGElement.readAndCreateSVG(file);
					addSVGElement(file, svgElement);
					//svgList.add(svgElement);
				} catch (Exception e) {
					LOG.error("not an SVG file: "+file);
				}
			}
		}
		//return svgList;
	}

	@Override
	public String[] getExtensions() {
		return new String[] {"svg"};
	}

	@Override
	//FIXME need to abstract this for all Visitables
	public void getMetadata() {
		//getSVGElementList();
		ensureSVGContainerList();
		for (SVGContainer svgConteiner : svgContainerList) {
			SVGElement svg = (SVGElement) svgConteiner.getElement();
			getMetadata(svg);
		}
	}

	private void getMetadata(SVGElement svgElement) {
		List<SVGImage> imageList = SVGImage.extractSelfAndDescendantImages(svgElement);
		List<SVGPath> pathList = SVGPath.extractSelfAndDescendantPaths(svgElement);
		List<SVGShape> shapeList = SVGShape.extractSelfAndDescendantShapes(svgElement);
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svgElement);
		LOG.trace("images: "+imageList.size()+" ; "+"paths: "+pathList.size()+" ; "+"shapes: "+shapeList.size()+" ; "+"texts: "+textList.size());
	}
	
	public static boolean hasSuffix(String suffix) {
		return SVG.equalsIgnoreCase(suffix);
	}

	@Override
	public void downloadParseAndAddURL(URL url) throws Exception {
		super.addURL(url);
		throw new RuntimeException("URLS for SVG not yet implemented ");
	}

	public List<SVGContainer> getSVGContainerList() {
		return svgContainerList;
	}

	public void addSVGElement(File svgFile) {
		addSVGElement(svgFile, SVGElement.readAndCreateSVG(svgFile));
	}
	
}
