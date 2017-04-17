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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Converter provides the command line processing for QRDA III to QPP json.
 * Expects a list of filenames as CLI parameters to be processed
 * Supports wild card characters in paths
 */
public class Converter {

	public static final Logger CLIENT_LOG = LoggerFactory.getLogger("CLIENT-LOG");

	protected static final String SKIP_VALIDATION = "--skip-validation";
	protected static final String SKIP_DEFAULTS = "--skip-defaults";

	private static final Logger DEV_LOG = LoggerFactory.getLogger(Converter.class);

	private static final String NO_INPUT_FILE_SPECIFIED = "No input filename was specified.";
	private static final String CANNOT_LOCATE_FILE_PATH = "Cannot locate file path {0} {1}";
	private static final String FILE_DOES_NOT_EXIST = "{} does not exist.";
	private static final String TOO_MANY_WILD_CARDS = "Too many wild cards in {}";
	private static final String NOT_VALID_XML_DOCUMENT = "The file is not a valid XML document";

	private static boolean doDefaults = true;
	private static boolean doValidation = true;
	private final Path inFile; //The file being decoded

	/**
	 * Constructor for the CLI Converter application
	 *
	 * @param inFile File
	 */
	public Converter(Path inFile) {
		this.inFile = inFile;
	}

	/**
	 * The Converter main entry point
	 *
	 * @param args Command Line Arguments list of file names and flags
	 */
	public static void main(String... args) {
		Collection<Path> filenames = validArgs(args);
		filenames.parallelStream().forEach(
				(filename) -> new Converter(filename).transform());
	}

	/**
	 * validArgs checks the command line parameters and verifies the existence of Files.
	 *
	 * @param args String[] Command line parameters
	 * @return Collection<File> list of file(s) that are to be transformed
	 */
	protected static Collection<Path> validArgs(String[] args) {
		if (args.length < 1) {
			CLIENT_LOG.error(NO_INPUT_FILE_SPECIFIED);
			return new LinkedList<>();
		}

		Collection<Path> validFiles = new LinkedList<>();

		resetFlags();
		for (String arg : args) {
			if (checkFlags(arg)) {
				continue;
			}

			validFiles.addAll(checkPath(arg));
		}

		return validFiles;
	}

	private static void resetFlags(){
		doValidation = true;
		doDefaults = true;
	}

	private static boolean checkFlags(String arg) {
		if ( SKIP_VALIDATION.equals(arg) ) {
			doValidation = false;
			return true;
		}

		if ( SKIP_DEFAULTS.equals(arg) ) {
			doDefaults = false;
			return true;
		}
		return false;
	}

	/**
	 * Produce collection of files found within the given path
	 *
	 * @param path String file location
	 * @return Collection<File> list of files at the file location
	 */
	protected static Collection<Path> checkPath(String path) {
		Collection<Path> existingFiles = new LinkedList<>();

		if (path == null || path.trim().isEmpty()) {
			return existingFiles;
		}
		if (path.contains("*")) {
			return manyPath(path);
		}

		Path file = Paths.get(path);
		if (Files.exists(file)) {
			existingFiles.add(file);
		} else {
			CLIENT_LOG.error(FILE_DOES_NOT_EXIST, path);
		}
		return existingFiles;
	}

	/**
	 * Accumulates collection of files that match the given path
	 *
	 * @param path
	 * @return
	 */
	protected static Collection<Path> manyPath(String path) {
		Path inDir = Paths.get(extractDir(path));
		Pattern fileRegex = wildCardToRegex(path);
		try {
			return Files.walk(inDir)
					.filter(file -> fileRegex.matcher(file.toString()).matches())
					.filter(file -> !Files.isDirectory(file))
					.collect(Collectors.toList());
		} catch (Exception e) {
			DEV_LOG.error(MessageFormat.format(CANNOT_LOCATE_FILE_PATH, path, inDir), e);
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

		StringJoiner dirPath = new StringJoiner(FileSystems.getDefault().getSeparator());
		for (String part : parts) {
			// append until a wild card
			if (part.contains("*")) { // append until a wild card
				break;
			}
			dirPath.add(part);
		}
		if (dirPath.length() == 0) { // if no path then use the current dir
			return ".";
		}

		return dirPath.toString();
	}

	/**
	 * wildCardToRegex Supports processing many input files specified as wild cards
	 *
	 * @param path String folder path of files to transform
	 * @return String converts /* into /. for use by ListFiles.
	 */
	protected static Pattern wildCardToRegex(String path) {
		String regex = "";

		String dirPath = extractDir(path);
		String wild = path;
		if (dirPath.length() > 1) {
			wild = wild.substring(dirPath.length());
		}

		String[] parts = wild.split("[\\/\\\\]");

		if (parts.length > 2) {
			CLIENT_LOG.error(TOO_MANY_WILD_CARDS, path);
			return Pattern.compile("");
		}
		String lastPart = parts[parts.length - 1];

		if ("**".equals(lastPart)) {
			regex = "."; // any and all files
		} else {
			// turn the last part into REGEX from file wild cards
			regex = lastPart.replaceAll("\\.", "\\\\.");
			regex = regex.replaceAll("\\*", ".*");
		}

		return Pattern.compile(regex);
	}

	private Integer transform() {
		QrdaValidator validator = new QrdaValidator();
		List<ValidationError> validationErrors = Collections.emptyList();

		try {
			String inputFileName = inFile.getFileName().toString().trim();
			Node decoded = XmlInputDecoder.decodeXml(XmlUtils.fileToDOM(inFile));
			if (null == decoded) {
				return 2;
			}

			CLIENT_LOG.info("Decoded template ID {} from file '{}'", decoded.getId(), inputFileName);

			if (!doDefaults) {
				DefaultDecoder.removeDefaultNode(decoded.getChildNodes());
			}
			if (doValidation) {
				validationErrors = validator.validate(decoded);
			}
			if (validationErrors.isEmpty()) {
				writeConvertedFile(decoded, inputFileName);
			} else {
				writeValidationErrors(inputFileName, validationErrors);
			}
		} catch (XmlInputFileException | XmlException xe) {
			CLIENT_LOG.error(NOT_VALID_XML_DOCUMENT);
			DEV_LOG.error(NOT_VALID_XML_DOCUMENT, xe);
		} catch (Exception allE) {
			DEV_LOG.error("Unexpected exception occurred during conversion", allE);// Eat all exceptions in the call
		}
		return validationErrors.isEmpty() ? 1 : 0;
	}

	private void writeConvertedFile(Node decoded, String name) {
		JsonOutputEncoder encoder = new QppOutputEncoder();

		CLIENT_LOG.info("Decoded template ID {} from file '{}'", decoded.getId(), name);
		Path outFile = getOutputFile( name, ".qpp.json");

		try ( Writer writer = Files.newBufferedWriter(outFile) ){
			encoder.setNodes(Arrays.asList(decoded));
			encoder.encode(writer);
			// do something with encode validations
		} catch (IOException | EncodeException e) { // coverage ignore candidate
			throw new XmlInputFileException("Issues decoding/encoding.", e);
		} finally {
			Validations.clear();
		}
	}

	private void writeValidationErrors(String name, List<ValidationError> validationErrors) {
		Path outFile = getOutputFile( name, ".err.txt" );

		try (Writer errWriter = Files.newBufferedWriter(outFile)) {
			for (ValidationError error : validationErrors) {
				errWriter.write("Validation Error: " + error.getErrorText() + System.lineSeparator());
			}
		} catch (IOException e) { // coverage ignore candidate
			DEV_LOG.error("Could not write to file: {}", outFile.toString(), e);
		} finally {
			Validations.clear();
		}
	}

	private Path getOutputFile(String name, String extension) {
		String outName = name.replaceFirst("(?i)(\\.xml)?$", extension);
		Path outFile = Paths.get(outName);
		CLIENT_LOG.info("Writing to file '{}'", outFile.toAbsolutePath());
		return outFile;
	}
}
