package org.xmlcml.ami.util;

import java.util.List;

import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.text.TextStructurer;

/** 
 * Extracts words from SVG.
 * <p>
 * Mainly used for diagrams.
 * 
 * @author pm286
 */
public class WordExtractor {

	private SVGElement svgElement;
	private List<SVGText> textList;
	private TextStructurer textStructurer;

	public WordExtractor() {
		
	}
	
	public void setSVGElement(SVGElement element) {
		svgElement = element;
		setTextList(SVGText.extractSelfAndDescendantTexts(element));
	}
	
	public void setTextList(List<SVGText> textList) {
		this.textList = textList;
		this.textStructurer = new TextStructurer(textList);
	}
	
	
}