package gov.cms.qpp.conversion;

import gov.cms.qpp.test.logging.LoggerContract;

import com.google.common.truth.Truth;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Test;

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
		Truth.assertThat(getLogs()).isNotEmpty();
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
