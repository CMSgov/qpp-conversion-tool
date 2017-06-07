package gov.cms.qpp.acceptance;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.acceptance.helper.JsonPathAggregator;
import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static junit.framework.TestCase.fail;

public class JsonPathToXpathCoverageTest {
	private static JsonWrapper metaWrapper = new JsonWrapper(false);
	private static Path[] paths = {
			Paths.get("../qrda-files/valid-QRDA-III.xml"),
			Paths.get("src/test/resources/fixtures/multiPerformanceRatePropMeasure.xml")
	};
	private static JsonPathToXpathHelper helper;
	private JsonPathAggregator agg;
	private ObjectMapper om = new ObjectMapper();

	@Test
	public void coverage() throws IOException {
		Arrays.stream(paths).forEach(this::checkCoverage);
	}

	private void checkCoverage(Path path) {
		try{
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
