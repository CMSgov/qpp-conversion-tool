package gov.cms.qpp.acceptance;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cms.qpp.acceptance.helper.JsonPathAggregator;
import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;

class JsonPathToXpathCoverageTest {

	private JsonPathToXpathHelper helper;
	private ObjectMapper om = new ObjectMapper();

	static Stream<Path> paths() {
		return Stream.of("../qrda-files/valid-QRDA-III-latest.xml",
				"src/test/resources/fixtures/multiPerformanceRatePropMeasure.xml")
				.map(Paths::get);
	}

	@ParameterizedTest
	@MethodSource("paths")
	void testCoverage(Path path) throws IOException {
		JsonWrapper metaWrapper = new JsonWrapper(false);
		helper = new JsonPathToXpathHelper(path, metaWrapper, false);
		JsonWrapper wrapper = new JsonWrapper(metaWrapper, true);
		JsonNode root = om.readTree(wrapper.toString());
		JsonPathAggregator agg = new JsonPathAggregator(root);

		agg.getJsonPaths().forEach((key, value) -> helper.executeAttributeTest(key, value));
	}
}
