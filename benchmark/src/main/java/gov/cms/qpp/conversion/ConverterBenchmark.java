package gov.cms.qpp.conversion;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Performance test harness.
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(3)
public class ConverterBenchmark {

	/**
	 * State management for tests.
	 */
	@State(Scope.Thread)
	public static class Cleaner {
		@TearDown(Level.Trial)
		public void doTearDown() throws IOException {
			Path fileToDeletePath = Paths.get("valid-QRDA-III-latest.qpp.json");
			Files.delete(fileToDeletePath);
		}
	}

	/**
	 * Benchmark qrda file conversion.
	 *
	 * @param cleaner State management for conversion runs, ensures that output files are deleted.
	 */
	@Benchmark
	@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
	public void benchmarkMain(Cleaner cleaner) {
		ConversionEntry.main("../qrda-files/valid-QRDA-III-latest.xml");
	}

}
