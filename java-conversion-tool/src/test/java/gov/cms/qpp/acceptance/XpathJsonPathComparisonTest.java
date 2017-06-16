package gov.cms.qpp.acceptance;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.jsonpath.PathNotFoundException;

import gov.cms.qpp.ConversionTestSuite;
import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.xml.XmlException;

public class XpathJsonPathComparisonTest extends ConversionTestSuite {

	private static JsonWrapper wrapper = new JsonWrapper(false);
	private static Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
	private static JsonPathToXpathHelper helper;
	private static final String EXTENSION = "extension";

	@BeforeClass
	public static void beforeClass() throws Exception {
		helper = new JsonPathToXpathHelper(path, wrapper);
	}

	@Test
	public void compareTopLevelElement() throws XmlException, IOException {
		helper.executeElementTest("", "ClinicalDocument");
	}

	@Test
	public void compareTopLevelAttributeProgramName() throws XmlException, IOException {
		String jsonPath = "programName";
		helper.executeAttributeTest(jsonPath, EXTENSION, "MIPS_INDIV");
	}

	@Test
	public void compareTopLevelAttributeTin() throws XmlException, IOException {
		String jsonPath = "taxpayerIdentificationNumber";
		helper.executeAttributeTest(jsonPath, EXTENSION, "123456789");
	}

	@Test
	public void compareTopLevelAttributeNpi() throws XmlException, IOException {
		String jsonPath = "nationalProviderIdentifier";
		helper.executeAttributeTest(jsonPath, EXTENSION, "2567891421");
	}

	@Test
	public void compareTopLevelAttributeEntityId() throws XmlException, IOException {
		String jsonPath = "entityId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "AR000000");
	}

	//ACI
	@Test
	public void compareAciMeasurePerformanceEnd() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].performanceEnd";
		helper.executeAttributeTest(jsonPath, "value", "20170531");
	}

	@Test
	public void compareAciMeasurePerformanceStart() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].performanceStart";
		helper.executeAttributeTest(jsonPath, "value", "20170201");
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].measureId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "ACI_PEA_1");
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciEp1() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[1].measureId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "ACI_EP_1");
	}
	
	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1Numerator() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.numerator";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1Denominator() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.denominator";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	//IA
	@Test
	public void compareIaMeasurePerformanceEnd() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].performanceEnd";
		helper.executeAttributeTest(jsonPath, "value", "20170430");
	}

	@Test
	public void compareIaMeasurePerformanceStart() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].performanceStart";
		helper.executeAttributeTest(jsonPath, "value", "20170101");
	}

	@Test
	public void compareIaMeasurePerformedMeasureIdIaEpa1Value() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[0].value";
		helper.executeAttributeTest(jsonPath, "code", "Y");
	}

	@Test
	public void compareIaMeasurePerformedMeasureIdIaEpa1() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[0].measureId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "IA_EPA_3");
	}

	//Quality measure
	@Test
	public void compareQualityMeasurePerformanceEnd() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].performanceEnd";
		helper.executeAttributeTest(jsonPath, "value", "20171231");
	}

	@Test
	public void compareQualityMeasurePerformanceStart() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].performanceStart";
		helper.executeAttributeTest(jsonPath, "value", "20170101");
	}

	@Test
	public void compareQualityMeasureIdValuePerformanceExclusion() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulationExclusion";
		helper.executeAttributeTest(jsonPath, "value", "50");
	}

	@Test
	public void compareQualityMeasureIdValuePerformanceMet() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	@Test
	public void compareQualityMeasureIdValueNumerator() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	@Test
	public void compareQualityMeasureIdValueDenominator() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "1000");
	}

	@Test
	public void compareQualityMeasureIdValueEligiblePopulation() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "1000");
	}

	@Test
	public void compareQualityMeasureIdValueEligiblePopulationExclusion() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulationExclusion";
		helper.executeAttributeTest(jsonPath, "value", "50");
	}

	@Test(expected = PathNotFoundException.class)
	public void nonexistentJsonPath() throws IOException, XmlException {
		String jsonPath = "meep.mawp";
		helper.executeAttributeTest(jsonPath, "", "");
	}
}
