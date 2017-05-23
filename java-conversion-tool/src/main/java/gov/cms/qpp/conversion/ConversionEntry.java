package gov.cms.qpp.conversion;


import gov.cms.qpp.conversion.segmentation.QrdaScope;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Entry point for the conversion process.
 */
public class ConversionEntry {
	private static final Logger CLIENT_LOG = LoggerFactory.getLogger("CLIENT-LOG");
	private static final Logger DEV_LOG = LoggerFactory.getLogger(ConversionEntry.class);

	private static final String DIR_EXTRACTION = "[\\/\\\\]";

	static final String CLI_PROBLEM = "Problem parsing cli options";
	static final String INVALID_TEMPLATE_SCOPE = "Invalid template scope";
	private static final String TOO_MANY_WILD_CARDS = "Too many wild cards in {}";
	private static final String NO_INPUT_FILE_SPECIFIED = "No input filename was specified.";
	private static final String FILE_DOES_NOT_EXIST = "{} does not exist.";
	private static final String CANNOT_LOCATE_FILE_PATH = "Cannot locate file path {0} {1}";

	static final String BYGONE = "bygone";
	static final String SKIP_VALIDATION = "skipValidation";
	static final String SKIP_DEFAULTS = "skipDefaults";
	static final String TEMPLATE_SCOPE = "templateScope";
	private static final String HELP = "help";

	private static boolean doDefaults = true;
	private static boolean doValidation = true;
	private static boolean historical;
	private static Set<QrdaScope> scope = new HashSet<>();
	private static Options options;
	private static HelpFormatter formatter;

	/**
	 * prevent instantiation
	 */
	private ConversionEntry() {}

	static {
		initCli();
	}

	/**
	 * The main entry point for conversion
	 *
	 * @param args Command Line Arguments list of file names and flags
	 */
	public static void main(String... args) {
		Collection<Path> filenames = validArgs(args);
		filenames.parallelStream().forEach(
				filename -> new Converter(filename)
							.doValidation(doValidation)
							.doDefaults(doDefaults)
							.transform());
	}

	/**
	 * validArgs checks the command line parameters and verifies the existence of Files.
	 *
	 * @param args Command line parameters.
	 * @return  A list of file(s) that are to be transformed.
	 */
	static Collection<Path> validArgs(String[] args) {
		Collection<Path> returnValue = new LinkedList<>();
		try {
			CommandLine line = cli(args);
			if (shouldContinue(line)) {
				returnValue = checkArgs(line);
			} else {
				formatter.printHelp("convert", options, true);
			}
		} catch (ParseException pe) {
			DEV_LOG.error(CLI_PROBLEM, pe);
			CLIENT_LOG.error(CLI_PROBLEM);
		}

		return returnValue;
	}

	/**
	 * Determine if valid arguments were passed via the command line.
	 *
	 * @param line command line arguments
	 * @return determination of validity
	 */
	static boolean shouldContinue(CommandLine line) {
		boolean shouldContinue = !line.hasOption(HELP) && validatedScope(line);
		if (shouldContinue && line.getArgList().isEmpty()) {
			CLIENT_LOG.error(NO_INPUT_FILE_SPECIFIED);
			shouldContinue = false;
		}
		return shouldContinue;
	}

	/**
	 * Validate scope values passed via command line.
	 *
	 * @param line command line arguments
	 * @return determination of validity
	 */
	static boolean validatedScope(CommandLine line) {
		boolean isItValid = true;
		if (line.hasOption(TEMPLATE_SCOPE)) {
			String[] templateScope = line.getOptionValue(TEMPLATE_SCOPE).split(",");
			scope = Arrays.stream(templateScope)
					.map(QrdaScope::getInstanceByName)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());

			if (scope.size() != templateScope.length) {
				CLIENT_LOG.error(INVALID_TEMPLATE_SCOPE);
				isItValid = false;
			}
		}
		return isItValid;
	}

	/**
	 * Initialize the command line interface.
	 */
	private static void initCli() {
		options = new Options();
		options.addOption("b", BYGONE, false, "Signals a historical conversion");
		options.addOption("v", SKIP_VALIDATION, false, "Skip validations");
		options.addOption("d", SKIP_DEFAULTS, false,"Skip defaulted transformations");
		options.addOption("h", HELP, false,"This help message");

		Option templateScope = Option.builder("t")
				.longOpt(TEMPLATE_SCOPE)
				.argName("scope1,scope2,...")
				.hasArg()
				.desc("Comma delimited scope values to use for context. Valid values: "
						+ Arrays.toString(QrdaScope.getNames()))
				.build();
		options.addOption(templateScope);

		formatter = new HelpFormatter();
	}

	/**
	 * Interprets command line options
	 *
	 * @param arguments array of values entered on the command line
	 * @return parsed representation of command line entries
	 * @throws ParseException when options cannot be parsed
	 */
	static CommandLine cli(String[] arguments) throws ParseException {
		return new DefaultParser().parse(options, arguments);
	}

	/**
	 * Extract values from command line entries and use as a means of configuration.
	 *
	 * @param line parsed command line
	 * @return paths extracted from the command line
	 */
	private static Collection<Path> checkArgs(CommandLine line) {
		Collection<Path> validFiles = new LinkedList<>();

		for (String arg : checkFlags(line).getArgs()) {
			validFiles.addAll(checkPath(arg));
		}

		return validFiles;
	}

	/**
	 * Mine values used to influence conversion behavior.
	 *
	 * @param line parsed representation of the command line
	 */
	static CommandLine checkFlags(CommandLine line) {
		doValidation = !line.hasOption(SKIP_VALIDATION);
		doDefaults = !line.hasOption(SKIP_DEFAULTS);
		historical = line.hasOption(BYGONE);
		return line;
	}

	/**
	 * Is this a conversion of historical submissions.
	 *
	 * @return determination of whether or not the conversion is enacted on historical submissions.
	 */
	public static boolean isHistorical() {
		return historical;
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
			System.out.println("opsgpoerh " + inDir);
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

	/**
	 * Get the scope that determines which data may be transformed.
	 *
	 * @return scope
	 */
	public static Collection<QrdaScope> getScope() {
		return Collections.unmodifiableSet(scope);
	}
}
