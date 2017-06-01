package gov.cms.qpp.acceptance;

import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.ConversionFileWriterWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class AciMeasurePerformedRoundTripTest extends BaseTest {

	public static final Path JUNK_QRDA3_FILE = Paths.get("src/test/resources/negative/AciMeasurePerformedGarbage.xml");

	@After
	public void deleteJsonFile() throws IOException {
		Files.deleteIfExists(Paths.get("AciMeasurePerformedGarbage.qpp.json"));
	}

	@Test
	public void testGarbage() throws IOException {
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(JUNK_QRDA3_FILE);
		converterWrapper.transform();

		List<Map<String, ?>> aciMeasures = JsonHelper.readJsonAtJsonPath(Paths.get("AciMeasurePerformedGarbage.qpp.json"),
			"$.measurementSets[?(@.category=='aci')].measurements[?(@.measureId=='TEST_MEASURE_ID')]", List.class);

		assertThat("There should still be an ACI measure even with the junk stuff in ACI measure.",
			aciMeasures, hasSize(1));
		assertThat("The measureId in the ACI measure should still be populated given the junk stuff in the measure.",
			aciMeasures.get(0).get("measureId"), is("TEST_MEASURE_ID"));
	}
}
