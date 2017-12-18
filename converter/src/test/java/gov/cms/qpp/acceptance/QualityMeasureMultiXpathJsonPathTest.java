package gov.cms.qpp.acceptance;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.xml.XmlException;

class QualityMeasureMultiXpathJsonPathTest {

	private static JsonWrapper wrapper = new JsonWrapper(false);
	private static Path path = Paths.get("src/test/resources/fixtures/multiPerformanceRatePropMeasure.xml");
	private static JsonPathToXpathHelper helper;

	@BeforeAll
	static void beforeClass() throws IOException {
		helper = new JsonPathToXpathHelper(path, wrapper);
	}

	@Test
	void compareFirstSubEligiblePopTotal() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	void compareFirstSubPopPerfMet() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "486");
	}

	@Test
	void compareFirstSubPopDenominator() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	void compareFirstSubPopDenominatorExceptions() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].eligiblePopulationException";
		helper.executeAttributeTest(jsonPath, "value", "35");
	}

	@Test
	void compareFirstSubPopNumerator() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "486");
	}

	@Test
	void compareSecondSubEligiblePopTotal() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[1].eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	@Test
	void compareSecondSubPopDenExcep() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[1].eligiblePopulationException";
		helper.executeAttributeTest(jsonPath, "value", "40");
	}

	@Test
	void compareThirdSubPopNumerator() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[2].performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "520");
	}
}