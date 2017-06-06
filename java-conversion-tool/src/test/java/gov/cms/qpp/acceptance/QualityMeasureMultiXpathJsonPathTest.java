package gov.cms.qpp.acceptance;

import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.xml.XmlException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class QualityMeasureMultiXpathJsonPathTest {
	private static JsonWrapper wrapper = new JsonWrapper(false);
	private static Path path = Paths.get("src/test/resources/fixtures/multiPerformanceRatePropMeasure.xml");
	private static JsonPathToXpathHelper helper;

	@BeforeClass
	public static void beforeClass() throws IOException {
		helper = new JsonPathToXpathHelper(path, wrapper);
	}

	@Test
	public void compareFirstSubPopPopTotal() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].populationTotal";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	public void compareFirstSubPopPerfMet() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "486");
	}

	@Test
	public void compareFirstSubPopInitPop() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].initialPopulation";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	public void compareFirstSubPopDenominator() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].denominator";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	public void compareFirstSubPopDenominatorExceptions() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].denominatorExceptions";
		helper.executeAttributeTest(jsonPath, "value", "35");
	}

	@Test
	public void compareFirstSubPopNumerator() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[0].numerator";
		helper.executeAttributeTest(jsonPath, "value", "486");
	}

	@Test
	public void compareSecondSubPopPopTotal() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[1].populationTotal";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	@Test
	public void compareSecondSubPopDenExcep() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[1].denominatorExceptions";
		helper.executeAttributeTest(jsonPath, "value", "40");
	}

	@Test
	public void compareThirdSubPopNumerator() throws XmlException, IOException {
		String jsonPath = "measurementSets[0].measurements[0].value.strata[2].numerator";
		helper.executeAttributeTest(jsonPath, "value", "520");
	}
}