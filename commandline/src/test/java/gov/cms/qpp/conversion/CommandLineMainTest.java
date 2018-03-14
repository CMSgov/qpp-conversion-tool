package gov.cms.qpp.conversion;

import com.google.common.truth.Truth;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.test.logging.LoggerContract;

class CommandLineMainTest implements LoggerContract {

	@Test
	void testCliInvalidOption() {
		CommandLine line = CommandLineMain.cli("-InvalidArgument");
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
}
