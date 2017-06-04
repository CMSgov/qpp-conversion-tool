package gov.cms.qpp.acceptance;


import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.xml.XmlException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XpathJsonPathComparisonTest {
	private static JsonWrapper wrapper = new JsonWrapper(false);
	private static Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
	private static JsonPathToXpathHelper helper;

	@BeforeClass
	public static void beforeClass() throws IOException {
		helper = new JsonPathToXpathHelper(path, wrapper);
	}

	@Test
	public void compareTopLevelElement() throws XmlException, IOException {
		helper.executeElementTest("", "ClinicalDocument");
	}

	@Test
	public void compareTopLevelAttributeProgramName() throws XmlException, IOException {
		String jsonPath = "programName";
		helper.executeAttributeTest(jsonPath, "extension", "MIPS");
	}

	@Test
	public void compareTopLevelAttributeTin() throws XmlException, IOException {
		String jsonPath = "taxpayerIdentificationNumber";
		helper.executeAttributeTest(jsonPath, "extension", "123456789");
	}

	@Test
	public void compareTopLevelAttributeNpi() throws XmlException, IOException {
		String jsonPath = "nationalProviderIdentifier";
		helper.executeAttributeTest(jsonPath, "extension", "2567891421");
	}

	@Test
	public void compareTopLevelAttributeEntityId() throws XmlException, IOException {
		String jsonPath = "entityId";
		helper.executeAttributeTest(jsonPath, "extension", "AR000000");
	}

	@Test
	public void compareTopLevelAttributePerformanceYear() throws XmlException, IOException {
		String jsonPath = "performanceYear";
		helper.executeAttributeTest(jsonPath, "value", "20170101");
	}

	//ACI
	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[0].measureId";
		helper.executeAttributeTest(jsonPath, "extension", "ACI-PEA-1");
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciEp1() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[1].measureId";
		helper.executeAttributeTest(jsonPath, "extension", "ACI_EP_1");
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciCctpe3() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[2].measureId";
		helper.executeAttributeTest(jsonPath, "extension", "ACI_CCTPE_3");
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1Numerator() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[0].value.performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1Denominator() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[0].value.eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	//IA
	@Test
	public void compareIaMeasurePerformedMeasureIdIaEpa1Value() throws IOException, XmlException {
		String jsonPath = "measurementSets[3].measurements[0].value";
		helper.executeAttributeTest(jsonPath, "code", "Y");
	}

	@Test
	public void compareIaMeasurePerformedMeasureIdIaEpa1() throws IOException, XmlException {
		String jsonPath = "measurementSets[3].measurements[0].measureId";
		helper.executeAttributeTest(jsonPath, "extension", "IA_EPA_1");
	}

	//Quality measure
	@Test
	public void compareQualityMeasureIdValuePerformanceNotMet() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.performanceNotMet";
		helper.executeAttributeTest(jsonPath, "value", "50");
	}

	@Test
	public void compareQualityMeasureIdValuePerformanceExclusion() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.eligiblePopulationExclusion";
		helper.executeAttributeTest(jsonPath, "value", "50");
	}

	@Test
	public void compareQualityMeasureIdValuePerformanceMet() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "1000");
	}

	@Test
	public void compareQualityMeasureIdValueNumerator() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "1000");
	}

	@Test
	public void compareQualityMeasureIdValueDenominator() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "1000");
	}

	@Test
	public void compareQualityMeasureIdValueEligiblePopulation() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "1000");
	}

	@Test
	public void compareQualityMeasureIdValueEligiblePopulationExclusion() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.eligiblePopulationExclusion";
		helper.executeAttributeTest(jsonPath, "value", "50");
	}
}
