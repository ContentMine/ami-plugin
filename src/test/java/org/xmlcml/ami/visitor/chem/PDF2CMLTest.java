package org.xmlcml.ami.visitor.chem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Builder;
import nu.xom.Element;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.ami.Fixtures;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Real2Array;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.euclid.Vector2;
import org.xmlcml.graphics.svg.SVGElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGPath;
import org.xmlcml.graphics.svg.SVGPathPrimitive;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.path.ClosePrimitive;
import org.xmlcml.graphics.svg.path.PathPrimitiveList;
import org.xmlcml.xml.XMLUtil;

public class PDF2CMLTest {

	private static Logger LOG = Logger.getLogger(PDF2CMLTest.class);
	private double xoffset;
	private double yoffset;

	@Test
	public void testSpectrum61() throws Exception {
//		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page6.svg"), 11, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page6.svg"), 12, 300., 300.);
//		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page6.svg"), 13, 300., 300.);
	}
	
	@Test
	@Ignore
	public void testSpectrum62() throws Exception {
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page6.svg"), 18, 537, 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page6.svg"), 19, 537, 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page6.svg"), 20, 537, 300.);
	}
	
	@Test
	@Ignore
	public void testSpectrum11() throws Exception {
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page11.svg"), 19, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page11.svg"), 20, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page11.svg"), 22, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page11.svg"), 23, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page11.svg"), 24, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page11.svg"), 25, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page11.svg"), 26, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page11.svg"), 27, 300., 300.);
	}
	
	@Test
	@Ignore
	public void testSpectrum12() throws Exception {
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page12.svg"), 39, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page12.svg"), 40, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page12.svg"), 42, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page12.svg"), 43, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page12.svg"), 44, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page12.svg"), 45, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page12.svg"), 46, 300., 300.);
		analyzeSpectrum(new File(Fixtures.MATERIALS_DIR, "materials-05-00027-page12.svg"), 47, 300., 300.);
	}
	
	private void analyzeSpectrum(File file, int path, double xoffset, double yoffset) throws Exception {
		this.xoffset = xoffset;
		this.yoffset = yoffset;
		Element element = new Builder().build(new FileInputStream(file)).getRootElement();
		SVGSVG svg = (SVGSVG) SVGElement.readAndCreateSVG(element);
		List<Element> elementList = XMLUtil.getQueryElements(svg, "//*[local-name()='path']");
		SVGPath curve11 = (SVGPath) elementList.get(path);
//		createSpectrum(createArray(curve11), filename+"spectrum"+path+".cml");
		
	}

//	private CMLCml createSpectrum(Real2Array real2Array, String filename) throws Exception {
//		CMLCml cml = new CMLCml();
//		CMLSpectrum spectrum = new CMLSpectrum();
//		spectrum.setConvention("JSpecView");
//		spectrum.setType("VIS");
//		cml.appendChild(spectrum);
//		CMLSpectrumData spectrumData = new CMLSpectrumData();
//		spectrum.addSpectrumData(spectrumData);
//		CMLAxis xAxis = new CMLXaxis();
//		addAxis(xAxis, -1.0, xoffset, real2Array, spectrumData);
//		CMLAxis yAxis = new CMLYaxis();
//		addAxis(yAxis, 1.0, 200., real2Array, spectrumData);
//		FileOutputStream fos = new FileOutputStream(filename);
//		cml.debug(fos, 2);
//		return cml;
//	}

//	private void addAxis(CMLAxis axis, double multiplier, double constant, Real2Array real2Array, CMLSpectrumData spectrumData) {
//		AxisType axisType = axis.getAxisType();
//		axis.setMultiplierToData(multiplier);
//		axis.setConstantToData(constant);
//		RealArray xyarray = (axisType.equals(AxisType.X)) ? real2Array.getXArray() : real2Array.getYArray();
//		if (xyarray == null) {
//			LOG.trace("null axis"); 
//		} else {
//			xyarray = xyarray.multiplyBy(-1.0);
//			xyarray = xyarray.addScalar(xoffset);
//			xyarray.reverse();
//			CMLArray array = new CMLArray(xyarray);
//			axis.addArray(array);
//			spectrumData.addXaxis((CMLXaxis)axis);
//		}
//	}
	
	private Real2Array createArray(SVGPath path)  {
		PathPrimitiveList primitives = path.ensurePrimitives();
		Real2Array real2Array = new Real2Array();
		int size = primitives.size();
		List<Integer> closeEndList = new ArrayList<Integer>();
		for (int j = 0; j < size; j++) {
			SVGPathPrimitive primitive = primitives.get(j);
			if (primitive instanceof ClosePrimitive) {
				closeEndList.add(j);
			}
		}
		closeEndList.add(size-1);
		LOG.trace("closeEnd: "+closeEndList.size());
		int closeStart = 0;
		List<SVGPath> closedPathList = new ArrayList<SVGPath>();
		int jj = 0;
		for (Integer closeEnd : closeEndList) {
				PathPrimitiveList primitiveList = new PathPrimitiveList();
			for (int i = closeStart; i <= closeEnd; i++) {
				primitiveList.add(primitives.get(i));
			}
			LOG.trace("... "+primitiveList.size());
			String d = SVGPath.constructDString(primitiveList);
			LOG.trace("d "+d);
			LOG.trace("d "+d.replaceAll("[0-9\\. ]*", ""));
			SVGPath svgPath = new SVGPath(d);
			SVGSVG svg = new SVGSVG();
			SVGG g = new SVGG();
			svg.appendChild(g);
			Transform2 t = new Transform2();
			Transform2 translate = new Transform2(new Vector2(primitiveList.get(0).getCoordArray().elementAt(0).multiplyBy(-1.)));
			t = translate.concatenate(t);
			Transform2 scale =Transform2.applyScale(20.);
			t = scale.concatenate(t);
			Transform2 move = new Transform2(new Vector2(100., 100.));
			t = move.concatenate(t);
			g.setTransform(t);
			g.appendChild(svgPath);
			svgPath.setStrokeWidth(0.05);
			FileOutputStream fos = null;
			try {
				jj++;
				fos = new FileOutputStream("target/junk"+jj+".svg");
				LOG.trace("====="+jj+"======");
				XMLUtil.debug(svg, fos, 1);
			} catch (IOException ee) {
				// TODO Auto-generated catch block
				ee.printStackTrace();
			}
			outputDirections(primitiveList);
			closeStart = closeEnd+1;
		}
		return real2Array;
	}

	private void outputDirections(PathPrimitiveList primitiveList) {

		SVGPathPrimitive.setFirstPoints(primitiveList);
		for(int i = 1; i < primitiveList.size(); i++) {
			SVGPathPrimitive primitive = primitiveList.get(i);
			Real2 t = primitive.getTranslation();
			LOG.trace("dir "+primitive.getTag()+" "
			    +primitive.getAngle()+" "
				+((t == null) ? " ... " : t.getLength()) +" "+primitive.getTranslation());
		}
	}
}
