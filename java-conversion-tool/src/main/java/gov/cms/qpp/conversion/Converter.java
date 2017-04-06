package gov.cms.qpp.conversion;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Converter provides the command line processing for QRDA III to QPP json.
 * 
 * @author David Uselmann
 *
 */
public class Converter {

	public static final String SKIP_VALIDATION = "--skip-validation";
	public static final String SKIP_DEFAULTS = "--skip-defaults";

	private static final Logger LOG = LoggerFactory.getLogger(Converter.class);

	private static boolean doDefaults = true;
	private static boolean doValidation = true;

	final Path inFile;

	public Converter(Path inFile) {
		this.inFile = inFile;
	}

	/**
	 * Perform transformation on {@link Converter#inFile}
	 *
	 * @return
	 */
	public Integer transform() {
		boolean hasValidationErrors = false;

		try {
			Node decoded = XmlInputDecoder.decodeXml(XmlUtils.fileToDOM(inFile));

			if (!doDefaults) {
				DefaultDecoder.removeDefaultNode(decoded.getChildNodes());
			}
			
			QrdaValidator validator = new QrdaValidator();

			List<ValidationError> validationErrors = Collections.emptyList();
			
			if (doValidation) {
				validationErrors = validator.validate(decoded);
			} 
			
			String name = inFile.getFileName().toString().trim();
			
			if (validationErrors.isEmpty()) {
				writeConvertedFile(decoded, name);
			} else {
				hasValidationErrors = true;
				writeValidationErrors(name, validationErrors);
			}
		} catch (XmlInputFileException | XmlException xe) {
			LOG.error("The file is not a valid XML document", xe);
		} catch (Exception allE) {
			LOG.error("Unexpected exception occurred during conversion", allE);
		}
		return hasValidationErrors ? 0 : 1;
	}

	private void writeConvertedFile(Node decoded, String name) {
		JsonOutputEncoder encoder = new QppOutputEncoder();

		LOG.info("Decoded template ID {} from file '{}'", decoded.getId(), name);
		String outName = name.replaceFirst("(?i)(\\.xml)?$", ".qpp.json");

		Path outFile = Paths.get(outName);
		LOG.info("Writing to file '{}'", outFile.toAbsolutePath());

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
		String errName = name.replaceFirst("(?i)(\\.xml)?$", ".err.txt");
		Path outFile = Paths.get(errName);
		LOG.info("Writing to file '{}'", outFile.toAbsolutePath());

		try (Writer errWriter = Files.newBufferedWriter(outFile)) {
			for (ValidationError error : validationErrors) {
				errWriter.write("Validation Error: " + error.getErrorText() + System.lineSeparator());
			}
		} catch (IOException e) { // coverage ignore candidate
			LOG.error("Could not write to file: {} {}", errName, e);
		} finally {
			Validations.clear();
		}
	}

	/**
	 * Validate filename arguments
	 *
	 * @param args filenames
	 * @return
	 */
	public static Collection<Path> validArgs(String[] args) {
		if (args.length < 1) {
			LOG.error("No filename found...");
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
	 * @param path
	 * @return
	 */
	public static Collection<Path> checkPath(String path) {
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
			LOG.error(path + " does not exist.");
		}

		return existingFiles;
	}

	/**
	 * Accumulates collection of files that match the given path
	 *
	 * @param path
	 * @return
	 */
	public static Collection<Path> manyPath(String path) {
		Path inDir = Paths.get(extractDir(path));
		Pattern fileRegex = wildCardToRegex(path);
		try {
			return Files.walk(inDir)
					.filter(file -> fileRegex.matcher(file.toString()).matches())
					.filter(file -> !Files.isDirectory(file))
					.collect(Collectors.toList());
		} catch (Exception e) {
			LOG.error("No matching files found {} {} {}", path, inDir, fileRegex);
			LOG.error("bad glob", e);
			return new LinkedList<>();
		}
	}

	/**
	 * Extract base directory of given path
	 *
	 * @param path
	 * @return
	 */
	public static String extractDir(String path) {
		String[] parts = path.split("[\\/\\\\]");

		StringJoiner dirPath = new StringJoiner(FileSystems.getDefault().getSeparator());
		for (String part : parts) {
			// append until a wild card
			if (part.contains("*")) {
				break;
			}
			dirPath.add(part);
		}
		// if no path then use the current dir
		if (dirPath.length() == 0) {
			return ".";
		}

		return dirPath.add("").toString();
	}

	/**
	 * Creates file finding regex from given wildcard containing path
	 *
	 * @param path
	 * @return
	 */
	public static Pattern wildCardToRegex(String path) {
		String regex = "";

		// this replace should work if the user does not give conflicting OS
		// path separators
		String dirPath = extractDir(path);
		String wild = path;
		if (dirPath.length() > 1) {
			wild = wild.substring(dirPath.length());
		}

		String[] parts = wild.split("[\\/\\\\]");

		if (parts.length > 2) {
			LOG.error("Too many wild cards in {}", path);
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

	/**
	 * Entry point for the conversion process
	 *
	 * @param args file path(s) of resources subject to conversion
	 */
	public static void main(String[] args) {
		Collection<Path> filenames = validArgs(args);
		filenames.parallelStream().forEach(
				filename -> new Converter(filename).transform());
	}

}
