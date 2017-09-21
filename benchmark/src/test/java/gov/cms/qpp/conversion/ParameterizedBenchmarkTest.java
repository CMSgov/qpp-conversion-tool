package gov.cms.qpp.conversion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

import gov.cms.qpp.test.FileTestHelper;

public class ParameterizedBenchmarkTest {

	private static Field fileSystemField;
	private static FileSystem defaultFileSystem;
	private static FileSystem fileSystem;
	private static List<BenchmarkResult> benchResults;
	private static String[] paths;

	@BeforeClass
	public static void loadPaths() throws IOException, RunnerException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
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
		fileSystemField.set(null, defaultFileSystem);
		fileSystem.close();
	}

	@Test
	public void testNumPaths() {
		assertThat("2 test files are expected", paths.length, is(2));
	}

	@Test
	public void testParameterizedBenchmarkThroughput() throws RunnerException {
		benchResults.stream()
			.filter(result -> result.getParams().getMode().equals(Mode.Throughput))
			.forEach(br -> {
				String fileName = br.getParams().getParam("fileName");
				Result result = br.getPrimaryResult();

				assertThat("Throughput should be greater than 2 for file: " + fileName,
						result.getScore(), greaterThan(2.0));
			});
	}

	@Test
	public void testParameterizedBenchmarkAverageTime() throws RunnerException {
		benchResults.stream()
			.filter(result -> result.getParams().getMode().equals(Mode.AverageTime))
			.forEach(br -> {
				String fileName = br.getParams().getParam("fileName");
				Result result = br.getPrimaryResult();

				assertThat("Average time should be less than .5 seconds" + fileName,
						result.getScore(), lessThan(0.5));
			});
	}
}
