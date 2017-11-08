package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;

@Disabled
class MultipleTinRoundTripTest {

	private static JsonWrapper wrapper = new JsonWrapper();
	private static ReadContext ctx;

	@BeforeAll
	static void setup() throws IOException {
		Path path = Paths.get("../qrda-files/ComprehensivePrimaryCare_Sample_QRDA_III-latest.xml");
		new JsonPathToXpathHelper(path, wrapper, false);
		ctx = JsonPath.parse(wrapper.toString());
	}

	@Test
	void hasMultipleNpiTinCombo() {
		List<Map<String, Object>> topLevel = ctx.read("$");
		assertThat(topLevel)
				.hasSize(5);
	}

}
