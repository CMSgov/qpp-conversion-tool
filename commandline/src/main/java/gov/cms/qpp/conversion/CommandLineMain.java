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

public class CommandLineMain {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(CommandLineMain.class);

	static final Options OPTIONS;
	static final HelpFormatter HELP_FORMAT;

	public static final String BYGONE = "bygone";
	public static final String SKIP_VALIDATION = "skipValidation";
	public static final String SKIP_DEFAULTS = "skipDefaults";
	public static final String TEMPLATE_SCOPE = "templateScope";
	public static final String RECURSIVE = "recursive";
	public static final String HELP = "help";

	static {
		OPTIONS = new Options();
		OPTIONS.addOption("b", BYGONE, false, "Signals a historical conversion");
		OPTIONS.addOption("v", SKIP_VALIDATION, false, "Skip validations");
		OPTIONS.addOption("d", SKIP_DEFAULTS, false, "Skip defaulted transformations");
		OPTIONS.addOption("r", RECURSIVE, false, "Search for specified files recursively");
		OPTIONS.addOption("h", HELP, false, "This help message");

		Option templateScope = Option.builder("t")
				.longOpt(TEMPLATE_SCOPE)
				.argName("scope1,scope2,...")
				.hasArg()
				.desc("Comma delimited scope values to use for context. Valid values: " + QrdaScope.getNames())
				.build();
		OPTIONS.addOption(templateScope);

		HELP_FORMAT = new HelpFormatter();
	}

	public static void main(String... arguments) {
		CommandLine commandLine = cli(arguments);

		if (commandLine != null) {
			new CommandLineRunner(commandLine).run();
		}
	}

	static CommandLine cli(String... arguments) {
		try {
			return new DefaultParser().parse(OPTIONS, arguments);
		} catch (ParseException exception) {
			DEV_LOG.error("Problem parsing cli options, {}", exception.getMessage());
			return null;
		}
	}

}
