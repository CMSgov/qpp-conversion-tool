package gov.cms.qpp.conversion;

import gov.cms.qpp.test.annotations.PerformanceTest;
import gov.cms.qpp.test.jimfs.FileTestHelper;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.jupiter.api.BeforeAll;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

class ParameterizedBenchmarkTest {

	private static List<BenchmarkResult> benchResults;

	@BeforeAll
	static void loadPaths() throws RunnerException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		String[] paths = FileTestHelper.getAllQrdaFiles(FileSystems.getDefault(), "-latest.xml").map(Path::toString).toArray(String[]::new);

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

	@PerformanceTest
	void testParameterizedBenchmarkThroughput() {
		benchResults.stream()
			.filter(result -> result.getParams().getMode().equals(Mode.Throughput))
			.forEach(br -> {
				String fileName = br.getParams().getParam("fileName");
				Result result = br.getPrimaryResult();

				assertWithMessage("Throughput should be greater than 2 for file: %s", fileName)
						.that(result.getScore()).isAtLeast(2.0);
			});
	}

	@PerformanceTest
	void testParameterizedBenchmarkAverageTime() {
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
