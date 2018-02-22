package gov.cms.qpp.conversion;

import gov.cms.qpp.test.jimfs.FileTestHelper;

import java.nio.file.FileSystem;
import java.util.concurrent.TimeUnit;

import com.google.common.jimfs.Configuration;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@State(Scope.Benchmark)
public class ParameterizedBenchmark {

	@State(Scope.Thread)
	public static class FileSystemState {
		private FileSystem fileSystem;

		@Setup(Level.Iteration)
		public void setup() {
			fileSystem = FileTestHelper.createMockFileSystem(Configuration.unix());
		}

		public FileSystem getFileSystem() {
			return fileSystem;
		}
	}

	@Param("../qrda-files/valid-QRDA-III-latest.xml")
	public String fileName;

	@Benchmark
	public void bench(FileSystemState state) {
		new CommandLineRunner(CommandLineMain.cli(fileName), state.getFileSystem()).run();
	}
}