package gov.cms.qpp.conversion;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.util.Finder;

/**
 * Responsible for executing the given command line instructions
 */
public class CommandLineRunner implements Runnable {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(CommandLineRunner.class);

	private final CommandLine commandLine;
	private final FileSystem fileSystem;
	private boolean doValidation;
	private boolean historical;
	private Pattern normalPathPattern;
	private Pattern globFinderPattern;

	/**
	 * Creates a new CommandLineRunner from a given {@link CommandLine}
	 *
	 * @param commandLine command line execute
	 */
	public CommandLineRunner(CommandLine commandLine) {
		this(commandLine, FileSystems.getDefault());
	}

	/**
	 * Creates a new CommandLineRunner from a given {@link CommandLine} and {@link FileSystem}
	 *
	 * @param commandLine command line execute
	 * @param fileSystem contextual file system to use for path operations
	 */
	public CommandLineRunner(CommandLine commandLine, FileSystem fileSystem) {
		Objects.requireNonNull(commandLine, "commandLine");
		Objects.requireNonNull(fileSystem, "fileSystem");

		this.commandLine = commandLine;
		this.fileSystem = fileSystem;
	}

	/**
	 * Executes the command line
	 */
	@Override
	public void run() {
		if (isHelp()) {
			sendHelp();
		} else if (hasPotentialFiles()) {
			Set<Path> convert = getRequestedFilesForConversion();

			List<Path> invalid = convert.stream()
					.filter(path -> !isValid(path))
					.collect(Collectors.toList());
			if (invalid.isEmpty()) {
				doValidation = !commandLine.hasOption(CommandLineMain.SKIP_VALIDATION);
				historical = commandLine.hasOption(CommandLineMain.BYGONE);

				convert.parallelStream()
					.map(ConversionFileWriterWrapper::new)
					.peek(conversion -> conversion.setContext(createContext()))
					.forEach(ConversionFileWriterWrapper::transform);
			} else {
				DEV_LOG.error("Invalid or missing paths: " + invalid);
				sendHelpHint();
			}
		} else {
			DEV_LOG.error("You must specify files to convert");
			sendHelpHint();
		}
	}

	private Context createContext() {
		Context context = new Context();
		context.setDoValidation(doValidation);
		context.setHistorical(historical);
		return context;
	}

	private boolean isHelp() {
		return commandLine.hasOption(CommandLineMain.HELP);
	}

	private void sendHelp() {
		StringWriter content = new StringWriter();
		PrintWriter out = new PrintWriter(content);
		CommandLineMain.HELP_FORMAT.printHelp(out, CommandLineMain.HELP_FORMAT.getWidth(),
				"convert", null, CommandLineMain.OPTIONS, 0, 0, null, true);

		DEV_LOG.info(content.toString());
	}

	private void sendHelpHint() {
		DEV_LOG.info("Get help by rerunning with -help.");
	}

	private boolean hasPotentialFiles() {
		return !commandLine.getArgList().isEmpty();
	}

	private Set<Path> getRequestedFilesForConversion() {
		return commandLine.getArgList()
				.stream()
				.map(this::getRequestedFilesForConversion)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	private Collection<Path> getRequestedFilesForConversion(String path) {
		if (isNormalPath(path)) {
			return Collections.singleton(fileSystem.getPath(path));
		}

		Path directory;
		String glob;
		Matcher globMatcher = getGlobFinderPattern().matcher(path);
		if (globMatcher.matches()) {
			String directoryName = globMatcher.group(1);
			if (directoryName.endsWith(".")) {
				directoryName = directoryName.substring(0, directoryName.length() - 1);
			}
			int lastSlash = directoryName.replace('\\', '/').lastIndexOf('/');
			if (lastSlash == -1) {
				directory = fileSystem.getPath(".");
				glob = path;
			} else {
				directory = fileSystem.getPath(directoryName);
				glob = path.substring(directoryName.substring(0, lastSlash + 1).length());
			}
		} else {
			directory = fileSystem.getPath(".");
			glob = path;
		}
		glob = "glob:" + glob;

		boolean recursive = commandLine.hasOption(CommandLineMain.RECURSIVE);
		Finder finder = new Finder(fileSystem.getPathMatcher(glob), recursive);
		try {
			Files.walkFileTree(directory, finder);
			return finder.getFoundFiles();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private boolean isNormalPath(String path) {
		return getNormalPathPattern().matcher(path).matches();
	}

	/**
	 * Generates a regex {@link Pattern} to determine if a path is for a normal file.
	 *
	 * The regex is based on the path separator of the {@link FileSystem} used.
	 *
	 * @return A regex pattern.
	 */
	protected Pattern getNormalPathPattern() {
		if (normalPathPattern == null) {
			String separator = "\\" + this.fileSystem.getSeparator();
			normalPathPattern = Pattern.compile("[a-zA-Z0-9_\\-\\s,." + separator + "]+");
		}

		return normalPathPattern;
	}

	/**
	 * Generates a regex {@link Pattern} to determine if a path is a glob path.
	 *
	 * The regex is based on the {@link #getNormalPathPattern()} regex.
	 *
	 * @return A regex pattern.
	 */
	protected Pattern getGlobFinderPattern() {
		if (globFinderPattern == null) {
			globFinderPattern = Pattern.compile("(" + getNormalPathPattern().pattern() + ").+");
		}

		return globFinderPattern;
	}

	public static boolean isValid(Path path) {
		// despite what sonar recommends this is a fine implementation
		return Files.isRegularFile(path) && Files.isReadable(path); //NOSONAR better than toFile().isFile()
		// Sonar recommends to change from Files to File impl
		// The Google JimFS Path impl does not support toFile()
		// Further more the following code is slower and the sonar warning is a performance flag
		//		File filePath = new File(path.toString());
		//		return filePath.exists() && filePath.isFile() && filePath.canRead();
	}
}
