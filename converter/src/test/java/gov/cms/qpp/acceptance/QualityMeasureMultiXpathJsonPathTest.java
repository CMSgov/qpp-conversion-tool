package gov.cms.qpp.acceptance;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.xml.XmlException;

class QualityMeasureMultiXpathJsonPathTest {

	private static JsonWrapper wrapper = new JsonWrapper();
	private static Path path = Path.of("src/test/resources/fixtures/multiPerformanceRatePropMeasure.xml");
	private static JsonPathToXpathHelper helper;

	@BeforeAll
	static void beforeClass() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.TEST_MEASURE_DATA);
		helper = new JsonPathToXpathHelper(path, wrapper);
	}

	@AfterAll
	static void afterClass() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@Test
	void compareFirstSubEligiblePopTotal() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	void compareFirstSubPopPerfMet() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "486");
	}

	@Test
	void compareFirstSubPopDenominator() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	void compareFirstSubPopDenominatorExceptions() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].eligiblePopulationExclusion";
		helper.executeAttributeTest(jsonPath, "value", "35");
	}

	@Test
	void compareFirstSubPopNumerator() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "486");
	}

	@Test
	void compareSecondSubEligiblePopTotal() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[1].eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	@Test
	void compareSecondSubPopDenExcep() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[1].eligiblePopulationExclusion";
		helper.executeAttributeTest(jsonPath, "value", "40");
	}
}