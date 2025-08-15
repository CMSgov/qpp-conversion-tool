package gov.cms.qpp.conversion;

import com.google.common.truth.Truth;
import gov.cms.qpp.conversion.util.Finder;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.test.jimfs.JimfsTest;
import gov.cms.qpp.test.logging.LoggerContract;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class CommandLineRunnerTest implements LoggerContract {

	private static final String VALID_FILE = "src/test/resources/valid-QRDA-III-latest.xml";
	private static final String INVALID_FILE = "THIS_FILE_SHOULD_NOT_EXIST.xml";
	private static final String WINDOWS_FILE = "src/test/resources/valid-QRDA-III-latest.xml";
	public static final String VALID_QRDA_III_LATEST_QPP_JSON = "valid-QRDA-III-latest-qpp.json";

	@Test
	void testNewNull() {
		NullPointerException exception = Assertions.assertThrows(NullPointerException.class, () -> new CommandLineRunner(null));
		Truth.assertThat(exception).hasMessageThat().isEqualTo("commandLine");
	}

	@Test
	void testRunHelp() {
		CommandLineRunner runner = new CommandLineRunner(line("-" + CommandLineMain.HELP));
		runner.run();
		Truth.assertThat(getLogs()).isNotEmpty();
	}

	@Test
	void testRunWithoutFiles() {
		CommandLineRunner runner = new CommandLineRunner(line());
		runner.run();
		Truth.assertThat(getLogs()).contains("You must specify files to convert");
	}

	@Test
	void testRunWithMissingFile() {
		CommandLineRunner runner = new CommandLineRunner(line(INVALID_FILE));
		runner.run();
		Truth.assertThat(getLogs()).contains("Invalid or missing paths: [FILE]".replace("FILE", INVALID_FILE));
	}

	@JimfsTest
	void testRunWithValidFile(FileSystem fileSystem) {
		String path = VALID_FILE.replaceAll("/", "\\" + fileSystem.getSeparator());
		CommandLineRunner runner = new CommandLineRunner(line(path), fileSystem);
		runner.run();
		Truth.assertThat(Files.exists(fileSystem.getPath(VALID_QRDA_III_LATEST_QPP_JSON))).isTrue();
	}

	@JimfsTest
	void testRunWithInvalidFile(FileSystem fileSystem) {
		String path = "src/test/resources/qrda_bad_denominator.xml".replaceAll("/", "\\" + fileSystem.getSeparator());
		CommandLineRunner runner = new CommandLineRunner(line(path), fileSystem);
		runner.run();
		Truth.assertThat(Files.exists(fileSystem.getPath("qrda_bad_denominator-error.json"))).isTrue();
	}

	@JimfsTest
	void testRunWithInvalidFileWithoutValidation(FileSystem fileSystem) {
		String path = "src/test/resources/qrda_bad_denominator.xml".replaceAll("/", "\\" + fileSystem.getSeparator());
		CommandLineRunner runner = new CommandLineRunner(line(path,
				"-" + CommandLineMain.SKIP_VALIDATION), fileSystem);
		runner.run();
		Truth.assertThat(Files.exists(fileSystem.getPath("qrda_bad_denominator-qpp.json"))).isTrue();
	}

	@JimfsTest
	void testRunWithValidFileGlobAtHeadInRoot(FileSystem fileSystem) throws IOException {
		Files.copy(fileSystem.getPath(VALID_FILE), fileSystem.getPath(VALID_QRDA_III_LATEST_QPP_JSON));
		CommandLineRunner runner = new CommandLineRunner(line("*.xml"), fileSystem);
		runner.run();
		Truth.assertThat(Files.exists(fileSystem.getPath(VALID_QRDA_III_LATEST_QPP_JSON))).isTrue();
	}

	@JimfsTest
	void testRunWithValidFileGlobAtTailInRoot(FileSystem fileSystem) throws IOException {
		Files.copy(fileSystem.getPath(VALID_FILE), fileSystem.getPath(VALID_QRDA_III_LATEST_QPP_JSON));
		CommandLineRunner runner = new CommandLineRunner(line("valid-QRDA-III-latest.*"), fileSystem);
		runner.run();
		Truth.assertThat(Files.exists(fileSystem.getPath("valid-QRDA-III-latest-qpp.json"))).isTrue();
	}

	@JimfsTest
	void testRunWithValidGlobAllFiles(FileSystem fileSystem) {
		String path = "src/test/resources/*".replaceAll("/", "\\" + fileSystem.getSeparator());
		CommandLineRunner runner = new CommandLineRunner(line(path), fileSystem);
		runner.run();
		Truth.assertThat(Files.exists(fileSystem.getPath(VALID_QRDA_III_LATEST_QPP_JSON))).isTrue();
		Truth.assertThat(Files.exists(fileSystem.getPath("not-a-QRDA-III-file-error.json"))).isTrue();
	}

	@Test
	void testWindowsFileSeparator() {
		FileSystem mockWindowsFileSystem = mock(FileSystem.class);

		when(mockWindowsFileSystem.getSeparator()).thenReturn("\\");
		CommandLineRunner runner = new CommandLineRunner(line(WINDOWS_FILE), mockWindowsFileSystem);
		Truth.assertThat(runner.getNormalPathPattern().pattern()).contains("\\\\");
	}

	@Test
	void testNixFileSeparator() {
		FileSystem mockWindowsFileSystem = mock(FileSystem.class);

		when(mockWindowsFileSystem.getSeparator()).thenReturn("/");
		CommandLineRunner runner = new CommandLineRunner(line(VALID_FILE), mockWindowsFileSystem);
		Truth.assertThat(runner.getNormalPathPattern().pattern()).contains("\\/");
	}

	@Test
	void testGlobPattern() {
		FileSystem mockWindowsFileSystem = mock(FileSystem.class);

		String mockSeparator = ":";
		when(mockWindowsFileSystem.getSeparator()).thenReturn(mockSeparator);
		CommandLineRunner runner = new CommandLineRunner(line(VALID_FILE), mockWindowsFileSystem);
		Truth.assertThat(runner.getGlobFinderPattern().pattern()).contains(mockSeparator);
	}

	private CommandLine line(String... arguments) {
		return CommandLineMain.cli(arguments);
	}

	@Override
	public Class<?> getLoggerType() {
		return CommandLineRunner.class;
	}
	
	@Test
	void testValidPath_valid() {
		Path path = Path.of(VALID_FILE);
		boolean valid = CommandLineRunner.isValid(path);
		
		Truth.assertThat(valid).isTrue();
	}
	
	@Test
	void testValidPath_invalid() {
		Path path = Path.of(INVALID_FILE);
		boolean invalid = CommandLineRunner.isValid(path);
		
		Truth.assertThat(invalid).isFalse();
	}

	@Test
	void testNormalAndGlobPatternCaching() {
		FileSystem fs = mock(FileSystem.class);
		when(fs.getSeparator()).thenReturn("/");
		CommandLineRunner runner = new CommandLineRunner(line(VALID_FILE), fs);

		Pattern first = runner.getNormalPathPattern();
		Pattern second = runner.getNormalPathPattern();
		Truth.assertThat(first).isSameInstanceAs(second);

		Pattern gFirst = runner.getGlobFinderPattern();
		Pattern gSecond = runner.getGlobFinderPattern();
		Truth.assertThat(gFirst).isSameInstanceAs(gSecond);
	}

	@Test
	void testGlobIOException() throws IOException {
		FileSystem fs = FileSystems.getDefault();
		Path tempDir = Files.createTempDirectory("unreadableDir");
		Files.delete(tempDir);

		CommandLineRunner runner = new CommandLineRunner(line(tempDir + "/*.xml"), fs);

		Assertions.assertThrows(UncheckedIOException.class, runner::run);
	}

	@JimfsTest
	void testRunWithMultipleValidFiles(FileSystem fileSystem) throws IOException {
		Path file1 = fileSystem.getPath("file1.xml");
		Path file2 = fileSystem.getPath("file2.xml");
		Files.copy(fileSystem.getPath(VALID_FILE), file1);
		Files.copy(fileSystem.getPath(VALID_FILE), file2);

		CommandLineRunner runner = new CommandLineRunner(line(file1.toString(), file2.toString()), fileSystem);
		runner.run();

		Truth.assertThat(Files.exists(fileSystem.getPath("file1-qpp.json"))).isTrue();
		Truth.assertThat(Files.exists(fileSystem.getPath("file2-qpp.json"))).isTrue();
	}
}
