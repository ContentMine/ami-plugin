package org.xmlcml.ami.visitable.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.xmlcml.ami.visitable.AbstractVisitable;


public class ImageVisitable extends AbstractVisitable {
	
	private final static Logger LOG = Logger.getLogger(ImageVisitable.class);
	
	private static final String BMP = "bmp";
	private static final String JPG = "jpg";
	private static final String JPEG = "jpeg";
	private static final String PNG = "png";
	private static final String TIF = "tif";
	private static final String TIFF = "tiff";
	
	private List<ImageContainer> imageContainerList;

	@Override
	public String[] getExtensions() {
		return new String[] {"png", "bmp", "jpg", "gif"};
	}
	
	@Override
	public void getMetadata() {
	}

	public static boolean hasSuffix(String suffix) {
		return 
				PNG.equalsIgnoreCase(suffix) ||
				BMP.equalsIgnoreCase(suffix) ||
				TIF.equalsIgnoreCase(suffix) ||
				TIFF.equalsIgnoreCase(suffix) ||
				JPEG.equalsIgnoreCase(suffix) ||
			JPG.equalsIgnoreCase(suffix);
	}

	private void ensureImageContainerList() {
		if (imageContainerList == null) {
			imageContainerList = new ArrayList<ImageContainer>();
		}
	}

	@Override
	public void addFile(File file) throws Exception {
		checkFile(file);
		BufferedImage bufferedImage = ImageIO.read(file);
		addImage(file, bufferedImage);
	}

	@Override
	public void downloadParseAndAddURL(URL url) throws Exception {
		super.addURL(url);
		BufferedImage bufferedImage = ImageIO.read(url.openStream());
		LOG.error("URL not implemented");
	}

	public void addImage(File file, BufferedImage image) {
		ensureImageContainerList();
		ImageContainer imageContainer = new ImageContainer(file, image);
		imageContainerList.add(imageContainer);
	}

	public List<ImageContainer> getImageContainerList() {
		ensureImageContainerList();
		return imageContainerList;
	}

}
