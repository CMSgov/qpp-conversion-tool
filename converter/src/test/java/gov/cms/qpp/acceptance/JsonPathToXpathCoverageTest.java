package gov.cms.qpp.acceptance;

import static junit.framework.TestCase.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cms.qpp.acceptance.helper.JsonPathAggregator;
import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;

class JsonPathToXpathCoverageTest {
	private static JsonPathToXpathHelper helper;
	private ObjectMapper om = new ObjectMapper();

	@Test
	void coverage() {
		Arrays.asList(
				Paths.get("../qrda-files/valid-QRDA-III-latest.xml"),
				Paths.get("src/test/resources/fixtures/multiPerformanceRatePropMeasure.xml")
		).forEach(this::checkCoverage);
	}

	private void checkCoverage(Path path) {
		try {
			JsonWrapper metaWrapper = new JsonWrapper(false);
			helper = new JsonPathToXpathHelper(path, metaWrapper, false);
			JsonWrapper wrapper = new JsonWrapper(metaWrapper, true);
			JsonNode root = om.readTree(wrapper.toString());
			JsonPathAggregator agg = new JsonPathAggregator(root);

			agg.getJsonPaths().forEach(
				(key, value) -> helper.executeAttributeTest(key, value));
		} catch (IOException ex) {
			fail(ex.getMessage());
		}
	}
}
