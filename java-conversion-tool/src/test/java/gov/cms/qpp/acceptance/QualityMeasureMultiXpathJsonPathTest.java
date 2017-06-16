package gov.cms.qpp.acceptance;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import gov.cms.qpp.ConversionTestSuite;
import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.xml.XmlException;

public class QualityMeasureMultiXpathJsonPathTest extends ConversionTestSuite {

	private static JsonWrapper wrapper = new JsonWrapper(false);
	private static Path path = Paths.get("src/test/resources/fixtures/multiPerformanceRatePropMeasure.xml");
	private static JsonPathToXpathHelper helper;

	@BeforeClass
	public static void beforeClass() throws Exception {
		helper = new JsonPathToXpathHelper(path, wrapper);
	}

	@Test
	public void compareFirstSubEligiblePopTotal() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	public void compareFirstSubPopPerfMet() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "486");
	}

	@Test
	public void compareFirstSubPopDenominator() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	public void compareFirstSubPopDenominatorExceptions() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].eligiblePopulationException";
		helper.executeAttributeTest(jsonPath, "value", "35");
	}

	@Test
	public void compareFirstSubPopNumerator() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "486");
	}

	@Test
	public void compareSecondSubEligiblePopTotal() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[1].eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	@Test
	public void compareSecondSubPopDenExcep() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[1].eligiblePopulationException";
		helper.executeAttributeTest(jsonPath, "value", "40");
	}

	@Test
	public void compareThirdSubPopNumerator() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[2].performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "520");
	}
}