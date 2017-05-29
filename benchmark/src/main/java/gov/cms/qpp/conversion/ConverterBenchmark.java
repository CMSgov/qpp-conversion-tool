package gov.cms.qpp.conversion;

import org.openjdk.jmh.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.*;

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
			Path fileToDeletePath = Paths.get("valid-QRDA-III.qpp.json");
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
		ConversionEntry.main("../qrda-files/valid-QRDA-III.xml");
	}

}
