package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.segmentation.QrdaScope;
import gov.cms.qpp.conversion.util.Finder;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineRunner implements Runnable {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(CommandLineMain.class);
	private static final Pattern LITERAL_COMMA = Pattern.compile(",", Pattern.LITERAL);
	private static final Pattern NORMAL_PATH = Pattern.compile("[a-zA-Z0-9_\\-\\s,.\\/]+");
	private static final Pattern GLOB_FINDER = Pattern.compile("(" + NORMAL_PATH.pattern() + ").+");

	private final CommandLine commandLine;
	private final FileSystem fileSystem;
	private Set<QrdaScope> scope;
	private boolean doValidation;
	private boolean doDefaults;
	private boolean historical;

	public CommandLineRunner(CommandLine commandLine) {
		this(commandLine, FileSystems.getDefault());
	}

	public CommandLineRunner(CommandLine commandLine, FileSystem fileSystem) {
		Objects.requireNonNull(commandLine, "commandLine");
		Objects.requireNonNull(fileSystem, "fileSystem");

		this.commandLine = commandLine;
		this.fileSystem = fileSystem;
	}

	@Override
	public void run() {
		if (isHelp()) {
			sendHelp();
			return;
		}

		if (!hasPotentialFiles()) {
			DEV_LOG.error("You must specify files to convert");
			sendHelpHint();
			return;
		}

		scope = getScope();
		if (scope == null) {
			DEV_LOG.error("A given template scope was invalid");
			sendHelpHint();
			return;
		}

		Set<Path> convert = getConvert();

		List<Path> invalid = convert.stream()
				.filter(path -> !isValid(path))
				.collect(Collectors.toList());
		if (!invalid.isEmpty()) {
			DEV_LOG.error("Invalid or missing paths: " + invalid);
			sendHelpHint();
			return;
		}

		doValidation = !commandLine.hasOption(CommandLineMain.SKIP_VALIDATION);
		doDefaults = !commandLine.hasOption(CommandLineMain.SKIP_DEFAULTS);
		historical = commandLine.hasOption(CommandLineMain.BYGONE);

		convert.parallelStream()
			.map(ConversionFileWriterWrapper::new)
			.peek(conversion -> conversion.setContext(createContext()))
			.forEach(ConversionFileWriterWrapper::transform);
	}

	private Context createContext() {
		Context context = new Context();
		context.setDoDefaults(doDefaults);
		context.setDoValidation(doValidation);
		context.setHistorical(historical);
		context.setScope(scope);
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

	private Set<QrdaScope> getScope() {
		if (commandLine.hasOption(CommandLineMain.TEMPLATE_SCOPE)) {
			String[] templateScope = LITERAL_COMMA.split(commandLine.getOptionValue(CommandLineMain.TEMPLATE_SCOPE));
			Set<QrdaScope> scope = Arrays.stream(templateScope)
					.map(QrdaScope::getInstanceByName)
					.filter(Objects::nonNull)
					.collect(Collectors.toCollection(() -> EnumSet.noneOf(QrdaScope.class)));

			if (scope.size() != templateScope.length) {
				return null;
			}
		}
		return EnumSet.noneOf(QrdaScope.class);
	}

	private Set<Path> getConvert() { // TODO rename
		return commandLine.getArgList()
				.stream()
				.map(this::getConvert)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	private Collection<Path> getConvert(String path) { // TODO rename
		if (isNormalPath(path)) {
			return Collections.singleton(fileSystem.getPath(path));
		}

		Path directory;
		String glob;
		Matcher globMatcher = GLOB_FINDER.matcher(path);
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
		return NORMAL_PATH.matcher(path).matches();
	}

	private boolean isValid(Path path) {
		return Files.isRegularFile(path) && Files.isReadable(path);
	}

}
