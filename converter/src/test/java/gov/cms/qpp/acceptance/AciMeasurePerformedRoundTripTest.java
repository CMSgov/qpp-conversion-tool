package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertWithMessage;

public class AciMeasurePerformedRoundTripTest {

	public static final Path JUNK_QRDA3_FILE = Paths.get("src/test/resources/negative/AciMeasurePerformedGarbage.xml");

	@Test
	public void testGarbage() throws IOException {

		Converter converter = new Converter(new PathQrdaSource(JUNK_QRDA3_FILE));
		JsonWrapper qpp = converter.transform();

		List<Map<String, ?>> aciMeasures = JsonHelper.readJsonAtJsonPath(qpp.toString(),
			"$.measurementSets[?(@.category=='aci')].measurements[?(@.measureId=='TEST_MEASURE_ID')]", List.class);

		assertWithMessage("There should still be an ACI measure even with the junk stuff in ACI measure.")
				.that(aciMeasures)
				.hasSize(1);
		assertWithMessage("The measureId in the ACI measure should still be populated given the junk stuff in the measure.")
				.that(aciMeasures.get(0).get("measureId"))
				.isEqualTo("TEST_MEASURE_ID");
	}
}
