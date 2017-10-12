package gov.cms.qpp.conversion;

import gov.cms.qpp.test.FileTestHelper;
import gov.cms.qpp.test.LoadTestSuite;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertWithMessage;

public class ParameterizedBenchmarkTest extends LoadTestSuite {

	private static Field fileSystemField;
	private static FileSystem defaultFileSystem;
	private static FileSystem fileSystem;
	private static List<BenchmarkResult> benchResults;

	@BeforeClass
	public static void loadPaths() throws IOException, RunnerException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		String[] paths;
		fileSystem = FileTestHelper.createMockFileSystem();
		fileSystemField = ConversionEntry.class.getDeclaredField("fileSystem");
		fileSystemField.setAccessible(true);
		defaultFileSystem = (FileSystem) fileSystemField.get(null);
		fileSystemField.set(null, fileSystem);
		paths = FileTestHelper.getAllQrdaFiles(fileSystem, "-latest.xml").map(Path::toString).toArray(String[]::new);

		Options opt = new OptionsBuilder()
				.mode(Mode.Throughput)
				.mode(Mode.AverageTime)
				.include(".*" + ParameterizedBenchmark.class.getSimpleName() + ".*")
				.param("fileName", paths)
				.forks(1)
				.build();

		List<RunResult> results = new ArrayList<>(new Runner(opt).run());
		benchResults = results.stream()
				.map(RunResult::getAggregatedResult)
				.collect(Collectors.toList());
	}

	@AfterClass
	public static void cleanup() throws IllegalArgumentException, IllegalAccessException, IOException {
		if (fileSystemField != null) {
			fileSystemField.set(null, defaultFileSystem);
		}
		if (fileSystem != null) {
			fileSystem.close();
		}
	}

	@Test
	public void testParameterizedBenchmarkThroughput() throws RunnerException {
		benchResults.stream()
			.filter(result -> result.getParams().getMode().equals(Mode.Throughput))
			.forEach(br -> {
				String fileName = br.getParams().getParam("fileName");
				Result result = br.getPrimaryResult();

				assertWithMessage("Throughput should be greater than 2 for file: %s", fileName)
						.that(result.getScore()).isAtLeast(2.0);
			});
	}

	@Test
	public void testParameterizedBenchmarkAverageTime() throws RunnerException {
		benchResults.stream()
			.filter(result -> result.getParams().getMode().equals(Mode.AverageTime))
			.forEach(br -> {
				String fileName = br.getParams().getParam("fileName");
				Result result = br.getPrimaryResult();

				assertWithMessage("Average time should be less than .5 seconds %s", fileName)
						.that(result.getScore())
						.isLessThan(0.5);
			});
	}
}
