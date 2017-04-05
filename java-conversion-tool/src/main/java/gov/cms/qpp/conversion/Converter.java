package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.decode.XmlInputDecoder;
import gov.cms.qpp.conversion.decode.XmlInputFileException;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.encode.EncodeException;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Converter provides the command line processing for QRDA III to QPP json.
 * Expects a list of filenames as CLI parameters to be processed
 * Supports wild card characters in paths
 */
public class Converter {

	protected static final String SKIP_VALIDATION = "--skip-validation";
	protected static final String SKIP_DEFAULTS = "--skip-defaults";
	private static final Logger LOG = LoggerFactory.getLogger(Converter.class);

	private static final String NO_INPUT_FILE_SPECIFIED = "No input filename was specified.";
	private static final  String CANNOT_LOCATE_FILE_PATH = "Cannot locate file path {}{}";
	private static final String FILE_DOES_NOT_EXIST = "() does not exist.";
	private static final String TOO_MANY_WILD_CARDS = "Too many wild cards in {}";
	private static final String MISSING_INPUT_FILE = "The input file {} doesn't exist ";
	final private static final String NOT_VALID_XML_DOCUMENT = "The file is not a valid XML document";

	private static boolean doDefaults = true;
	private static boolean doValidation = true;
	private final File inFile; //The file being decoded

	/**
	 * Constructor for the CLI Converter application
	 *
	 * @param inFile File
	 */
	public Converter(File inFile) {
		this.inFile = inFile;
	}

	/**
	 * The Converter main entry point
	 *
	 * @param args Command Line Arguments list of file names and flags
	 */
	public static void main(String[] args) {
		Collection<File> filenames = validArgs(args);
		filenames.parallelStream().forEach(
				(filename) -> new Converter(filename).transform());
	}

	/**
	 * validArgs checks the command line parameters and verifies the existence of Files.
	 *
	 * @param args String[] Command line parameters
	 * @return Collection<File> list of file(s) that are to be transformed
	 */
	protected static Collection<File> validArgs(String[] args) {
		if (args.length < 1) {
			LOG.error(NO_INPUT_FILE_SPECIFIED);
			return new LinkedList<>();
		}
		Collection<File> validFiles = new LinkedList<>();
		for (String arg : args) {
			if (SKIP_VALIDATION.equals(arg)) {
				doValidation = false;
				continue;
			}
			if (SKIP_DEFAULTS.equals(arg)) {
				doDefaults = false;
				continue;
			}
			validFiles.addAll(checkPath(arg));
		}
		return validFiles;
	}

	/**
	 * checkPath determines if path contains wild cards and ensures path exists
	 *
	 * @param path String file location
	 * @return Collection<File> list of files at the file location
	 */
	protected static Collection<File> checkPath(String path) {
		Collection<File> existingFiles = new LinkedList<>();

		if (path == null || path.trim().length() == 0) {
			return existingFiles;
		}
		if (path.contains("*")) {
			return manyPath(path);
		}
		File file = new File(path);
		if (file.exists()) {
			existingFiles.add(file);
		} else {
			LOG.error(FILE_DOES_NOT_EXIST, path);
		}
		return existingFiles;
	}

	/**
	 * manyPath parses the supplied path for file directories
	 *
	 * @param path String file folder location to look for files
	 * @return Collection<File> List of files found in path.
	 */
	protected static Collection<File> manyPath(String path) {
		File inDir = new File(extractDir(path));
		String fileRegex = wildCardToRegex(path);
		try {
			Collection<File> existingFiles = FileUtils.listFiles(inDir, new RegexFileFilter(fileRegex),
					DirectoryFileFilter.DIRECTORY);
			return existingFiles;
		} catch (Exception e) {
			LOG.error(CANNOT_LOCATE_FILE_PATH, inDir, fileRegex);
			return new LinkedList<>();
		}
	}

	/**
	 * extractDir parses the input path for wild card characters.
	 *
	 * @param path String folder path to search
	 * @return String up to the wild card character
	 */
	protected static String extractDir(String path) {
		String[] parts = path.split("[\\/\\\\]");

		StringBuilder dirPath = new StringBuilder();
		for (String part : parts) {
			if (part.contains("*")) {// append until a wild card
				break;
			}
			dirPath.append(part).append(File.separator);
		}
		if (dirPath.length() == 0) {// if no path then use the current dir
			dirPath.append('.');
		}
		return dirPath.toString();
	}

	/**
	 * wildCardToRegex Supports processing many input files specified as wild cards
	 *
	 * @param path String folder path of files to transform
	 * @return String converts /* into /. for use by ListFiles.
	 */
	protected static String wildCardToRegex(String path) {
		String regex = "";
		String dirPath = extractDir(path);
		String wild = path;
		if (dirPath.length() > 1) {
			wild = wild.substring(dirPath.length());
		}
		String[] parts = wild.split("[\\/\\\\]");
		if (parts.length > 2) {
			LOG.error(TOO_MANY_WILD_CARDS, path);
			return "";
		}
		String lastPart = parts[parts.length - 1];
		if ("**".equals(lastPart)) {
			regex = "."; // any and all files
		} else {
			// turn the last part into REGEX from file wild cards
			regex = lastPart.replaceAll("\\.", "\\\\.");
			regex = regex.replaceAll("\\*", ".*");
		}
		return regex;
	}

	/**
	 * transform performs three functions on the input
	 * 1) XmlInputDecoder decodes / parses xml file into intermediate Node list of nodes.
	 * 2) QrdaValidator invokes validation rules on the parsed list of nodes.
	 *    Accumulates List<ValidationError> validationErrors
	 * 3) QppOutputEncoder transforms the parsed list of nodes into JSON format
	 *
	 * @return Integer 0 when validations exist 1 otherwise
	 */
	private Integer transform() {
		QrdaValidator validator = new QrdaValidator();
		JsonOutputEncoder encoder = new QppOutputEncoder();
		List<ValidationError> validationErrors = Collections.emptyList();

		if (!inFile.exists()) {
			LOG.error(MISSING_INPUT_FILE, inFile.getPath());
			return 0;
		}
		try {
			String inputFileName = inFile.getName().trim();
			Node decoded = XmlInputDecoder.decodeXml(XmlUtils.fileToDOM(inFile));
			LOG.info("Decoded template ID {} from file '{}'", decoded.getId(), inputFileName);

			if (!doDefaults) {
				DefaultDecoder.removeDefaultNode(decoded.getChildNodes());
			}
			if (doValidation) {
				validationErrors = validator.validate(decoded);
			}
			if (validationErrors.isEmpty()) {
				try (Writer writer = new FileWriter(getOutputFile(inputFileName));){
					encoder.setNodes(Arrays.asList(decoded));
					encoder.encode(writer);
				} catch (IOException | EncodeException e) {
					throw new XmlInputFileException(String.format("Issues decoding/encoding file (0) .",inputFileName), e);
				} finally {
					Validations.clear();
				}
			} else {// hasValidations = true;
				writeErrorFile(validationErrors, inputFileName);
			}
		} catch (XmlInputFileException | XmlException xe) {
			LOG.error(NOT_VALID_XML_DOCUMENT);
		} catch (Exception allE) {
			LOG.error(allE.getMessage());// Eat all exceptions in the call
		}
		return validationErrors.isEmpty() ? 1 : 0;
	}

	private void writeErrorFile(List<ValidationError> validationErrors, String name) {
		File outFile = getErrorFile(name);
		try (Writer errWriter = new FileWriter(outFile)) {
			for (ValidationError error : validationErrors) {
				errWriter.write("Validation Error: " + error.getErrorText() + System.lineSeparator());
			}
		} catch (IOException e) {
			LOG.error("Could not write to file: {}", outFile.getName());
		} finally {
			Validations.clear();
		}
	}

	private File getErrorFile(String name) {
		String errName = name.replaceFirst("(?i)(\\.xml)?$", ".err.txt");
		File outFile = new File(errName);
		LOG.info("Writing to file '{}'", outFile.getAbsolutePath());
		return outFile;
	}

	private File getOutputFile(String name) throws IOException{
		String outName = name.replaceFirst("(?i)(\\.xml)?$", ".qpp.json");
		File outFile = new File(outName);
		LOG.info("Writing to file '{}'", outFile.getAbsolutePath());
		return outFile;
	}
}