package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.TypeRef;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;

class AciMeasurePerformedRoundTripTest {

	static final Path JUNK_QRDA3_FILE = Path.of("src/test/resources/negative/AciMeasurePerformedGarbage.xml");

	@Test
	void testGarbage() {
		Converter converter = new Converter(new PathSource(JUNK_QRDA3_FILE));
		JsonWrapper qpp = converter.transform().copyWithoutMetadata();

		List<Map<String, String>> piMeasures = JsonHelper.readJsonAtJsonPath(qpp.toString(),
			"$.measurementSets[?(@.category=='pi')].measurements[?(@.measureId=='TEST_MEASURE_ID')]", new TypeRef<List<Map<String, String>>>() { });

		assertThat(piMeasures)
				.hasSize(1);
		assertThat((piMeasures.get(0).get("measureId")))
				.isEqualTo("TEST_MEASURE_ID");
	}
}
