package gov.cms.qpp.conversion;


import gov.cms.qpp.conversion.segmentation.QRDAScoper;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConversionEntry {
	private static final Logger CLIENT_LOG = LoggerFactory.getLogger("CLIENT-LOG");
	private static final Logger DEV_LOG = LoggerFactory.getLogger(ConversionEntry.class);

	private static final String DIR_EXTRACTION = "[\\/\\\\]";

	private static final String TOO_MANY_WILD_CARDS = "Too many wild cards in {}";
	private static final String NO_INPUT_FILE_SPECIFIED = "No input filename was specified.";
	private static final String FILE_DOES_NOT_EXIST = "{} does not exist.";
	private static final String CANNOT_LOCATE_FILE_PATH = "Cannot locate file path {0} {1}";

	static final String SKIP_VALIDATION = "skipValidation";
	static final String SKIP_DEFAULTS = "skipDefaults";
	static final String TEMPLATE_SCOPE = "templateScope";

	private static boolean doDefaults = true;
	private static boolean doValidation = true;

	/**
	 * The main entry point for conversion
	 *
	 * @param args Command Line Arguments list of file names and flags
	 */
	public static void main(String... args) {
		Collection<Path> filenames = validArgs(args);
		filenames.parallelStream().forEach(
				(filename) -> {
					new Converter(filename)
							.doValidation(doValidation)
							.doDefaults(doDefaults)
							.transform();
				});
	}

	/**
	 * validArgs checks the command line parameters and verifies the existence of Files.
	 *
	 * @param args Command line parameters.
	 * @return  A list of file(s) that are to be transformed.
	 */
	static Collection<Path> validArgs(String[] args) {
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

	private static void resetFlags() {
		doValidation = true;
		doDefaults = true;
	}

	private static boolean checkFlags(String arg) {
		if (SKIP_VALIDATION.equals(arg)) {
			doValidation = false;
			return true;
		}

		if (SKIP_DEFAULTS.equals(arg)) {
			doDefaults = false;
			return true;
		}

		return false;
	}

	/**
	 * Produce collection of files found within the given path
	 *
	 * @param path A file location.
	 * @return The list of files at the file location.
	 */
	static Collection<Path> checkPath(String path) {
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
	 * @param path a path which may contain wildcards
	 * @return a collection of paths representing files to be converted
	 */
	static Collection<Path> manyPath(String path) {
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
	static String extractDir(String path) {
		String[] parts = path.split(DIR_EXTRACTION);

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
	static Pattern wildCardToRegex(String path) {
		String regex;
		String dirPath = extractDir(path);
		String wild = path;
		if (dirPath.length() > 1) {
			wild = wild.substring(dirPath.length());
		}

		String[] parts = wild.split(DIR_EXTRACTION);

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

	static CommandLine cli(String[] arguments) throws ParseException {
		Options options = new Options();
		options.addOption(SKIP_VALIDATION, false, "skip validations");
		options.addOption(SKIP_DEFAULTS, false,"skip defaulted transformations");

		Option templateScope = Option.builder(TEMPLATE_SCOPE)
				.argName("scope...")
				.hasArg()
				.desc("scope values to use for context. Valid values: " + QRDAScoper.getNames())
				.build();
		options.addOption(templateScope);

		return new DefaultParser().parse(options, arguments);
	}
}
