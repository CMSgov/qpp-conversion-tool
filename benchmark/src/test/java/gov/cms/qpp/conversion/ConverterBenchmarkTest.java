package gov.cms.qpp.conversion;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

public class ConverterBenchmarkTest {
	private static List<Path> paths;

	@BeforeClass
	public static void loadPaths() throws IOException {
		paths = Files.walk(Paths.get("../qrda-files"))
				.filter(Files::isRegularFile)
				.filter(path -> {
					return path.toFile().getName().endsWith("-latest.xml");
				})
				.collect(Collectors.toList());
	}

	@Test
	public void testNumPaths() {
		assertThat("2 test files are expected", paths.size(), is(2));
	}

	@Test
	public void converterBenchmarkThroughput() throws RunnerException {
		Options opt = getOptions(Mode.Throughput, ConverterBenchmark.class);
		Result res = executeBenchmark(opt);

		assertThat("Throughput should be greater than 2", res.getScore(), greaterThan(2.0));
	}

	@Test
	public void converterBenchmarkAverageTime() throws RunnerException {
		Options opt = getOptions(Mode.AverageTime, ConverterBenchmark.class);
		Result res = executeBenchmark(opt);

		assertThat("Average time should be less than .5 seconds", res.getScore(), lessThan(0.5));
	}

	private Options getOptions(Mode mode, Class benchmarkClass) {
		return new OptionsBuilder()
				.mode(mode)
				.include(".*" + benchmarkClass.getSimpleName() + ".*")
				.build();
	}

	private Result executeBenchmark(Options opt) throws RunnerException {
		List<RunResult> results = new ArrayList<>(new Runner(opt).run());
		RunResult result = results.get(0);

		assertThat("Should return a single benchmark result", results, hasSize(1));
		return result.getAggregatedResult().getPrimaryResult();
	}
}

