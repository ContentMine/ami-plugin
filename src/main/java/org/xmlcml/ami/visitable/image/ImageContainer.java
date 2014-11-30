package org.xmlcml.ami.visitable.image;

import java.awt.image.BufferedImage;
import java.io.File;

import nu.xom.Element;

import org.xmlcml.ami.visitable.VisitableContainer;

public class ImageContainer extends VisitableContainer {

	private BufferedImage image;

	public ImageContainer(File file, BufferedImage image) {
		this.file = file;
		this.image = image;
	}

	@Override
	public Element getElement() {
		return null;
	}

	/**
	 */
	@Override
	public String getDoi() {
		return doi;
	}
	
	@Override
	public String getName() {
		return file == null ? null : file.getAbsolutePath();
	}

	public BufferedImage getImage() {
		return image;
	}

}
