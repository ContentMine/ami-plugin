package org.xmlcml.xhtml2stm.visitor;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.xmlcml.xhtml2stm.util.Util;
import org.xmlcml.xhtml2stm.visitable.VisitableInput;

/** manages the output.
 * 
 * Decides whether to create files or directories. May map the structure onto the input structure.
 * 
 * @author pm286
 *
 */
public class VisitorOutput {

	private static final Logger LOG = Logger.getLogger(VisitorOutput.class);
	
	private static final String DEFAULT_OUTPUT_LOCATION = "target/";
	private static final String DEFAULT_BASENAME = "dummy";
	private static final String DEFAULT_OUTPUT_SUFFIX = ".xml";

	private String outputLocation;
	private VisitableInput visitableInput;
	private String extension;
	private boolean isDirectory;
	private File outputDirectory; 

	/** reads outputLocation and ducktypes the type (File, Directory, etc.).
	 * 
	 * @param outputLocation
	 */
	public VisitorOutput(String outputLocation) {
		setDefaults();
		this.outputLocation = outputLocation;
		generateOutputDirectoryName();
	}

	/** this creates a default outputLocation
	 * 
	 */
	public VisitorOutput() {
		setDefaults();
	}

	private void setDefaults() {
		outputLocation = DEFAULT_OUTPUT_LOCATION;
		extension = DEFAULT_OUTPUT_SUFFIX;
		outputDirectory = new File(DEFAULT_OUTPUT_LOCATION); 
}

	/** not yet used
	 * 
	 * @param visitableInput
	 */
	public void setVisitableInput(VisitableInput visitableInput) {
		this.visitableInput = visitableInput;
	}
	
	private void generateOutputDirectoryName() {
		if (outputLocation.startsWith(Util.HTTP)) {
			throw new RuntimeException("Cannot output to URL: "+outputLocation);
		}
		if (outputLocation.startsWith(Util.DOI)) {
			throw new RuntimeException("Cannot output to DOI: "+outputLocation);
		}
		if (outputLocation == null) {
			LOG.info("No explicit output location");
		} else {
			outputLocation = FilenameUtils.normalize(new File(outputLocation).getAbsolutePath());
			extension = FilenameUtils.getExtension(outputLocation);
			isDirectory = Util.endsWithSeparator(outputLocation) || extension == null || extension.equals("");
			outputDirectory = new File(outputLocation);
		}
	}

	protected String getOutputLocation() {
		return outputLocation;
	}

	protected String getExtension() {
		return extension;
	}

	protected boolean isDirectory() {
		return isDirectory;
	}

	public File getOutputDirectoryFile() {
		if (outputDirectory != null) {
			LOG.trace("outputDirectory: "+outputDirectory);
			if (outputDirectory.exists() && !outputDirectory.isDirectory()) {
				LOG.info("existing file is not a directory: "+outputDirectory);
			} else {
				ifNotEndsWithSlashUseParentAsOutputDirectory();
				outputDirectory.mkdirs();
				String baseName = (visitableInput == null) ? DEFAULT_BASENAME : visitableInput.getBaseName();
				LOG.trace("basename "+baseName);
				outputDirectory = new File(outputDirectory, baseName+"."+extension);
			}
		} else {
			throw new RuntimeException("Null output directory");
		}
		return outputDirectory;
	}

	private void ifNotEndsWithSlashUseParentAsOutputDirectory() {
		if (!outputDirectory.toString().endsWith("/")) {
			File parent = outputDirectory.getParentFile();
			outputDirectory = (parent == null) ? outputDirectory : parent;
		}
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
}
