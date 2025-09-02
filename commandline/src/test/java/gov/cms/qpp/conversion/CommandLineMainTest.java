package gov.cms.qpp.conversion;

import com.google.common.truth.Truth;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.test.logging.LoggerContract;

class CommandLineMainTest implements LoggerContract {

	@Test
	void testCliInvalidOption() {
		CommandLine line = CommandLineMain.cli("-" + CommandLineMain.SKIP_VALIDATION + " -InvalidArgument");
		Truth.assertThat(line).isNull();
		Truth.assertThat(getLogs()).isNotEmpty();
	}

	@Test
	void testMainInvalidCli() {
		CommandLineMain.main("-InvalidArgument");
		Truth.assertThat(getLogs()).isNotEmpty();
	}

	@Test
	void testMain() {
		CommandLineMain.main();
		Truth.assertThat(getLogs()).isEmpty();
	}

	@Test
	void testNew() {
		new CommandLineMain();
	}

	@Override
	public Class<?> getLoggerType() {
		return CommandLineMain.class;
	}

	@Test
	void testCliValidOption() {
		CommandLine line = CommandLineMain.cli("-" + CommandLineMain.SKIP_VALIDATION);
		Truth.assertThat(line).isNotNull();
		Truth.assertThat(line.hasOption(CommandLineMain.SKIP_VALIDATION)).isTrue();
	}

	@Test
	void testMainWithValidCli() {
		CommandLineMain.main("-" + CommandLineMain.SKIP_VALIDATION);
		Truth.assertThat(getLogs()).isEmpty();
	}

	@Test
	void testCliHelpOption() {
		CommandLine line = CommandLineMain.cli("-" + CommandLineMain.HELP);
		Truth.assertThat(line).isNotNull();
		Truth.assertThat(line.hasOption(CommandLineMain.HELP)).isTrue();
	}

	@Test
	void testOptionsContainExpectedFlags() {
		Truth.assertThat(CommandLineMain.OPTIONS.hasOption(CommandLineMain.BYGONE)).isTrue();
		Truth.assertThat(CommandLineMain.OPTIONS.hasOption(CommandLineMain.SKIP_VALIDATION)).isTrue();
		Truth.assertThat(CommandLineMain.OPTIONS.hasOption(CommandLineMain.RECURSIVE)).isTrue();
		Truth.assertThat(CommandLineMain.OPTIONS.hasOption(CommandLineMain.HELP)).isTrue();
	}
}
