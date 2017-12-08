package gov.cms.qpp.acceptance;

import com.jayway.jsonpath.PathNotFoundException;
import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.xml.XmlException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class XpathJsonPathComparisonTest {

	private static JsonWrapper wrapper = new JsonWrapper(false);
	private static Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
	private static JsonPathToXpathHelper helper;
	private static final String EXTENSION = "extension";

	@BeforeAll
	static void beforeClass() throws IOException {
		helper = new JsonPathToXpathHelper(path, wrapper);
	}

	@Test
	void compareTopLevelElement() throws XmlException, IOException {
		helper.executeElementTest("", "ClinicalDocument");
	}

	@Test
	void compareTopLevelAttributeProgramName() throws XmlException, IOException {
		String jsonPath = "programName";
		helper.executeAttributeTest(jsonPath, EXTENSION, "MIPS_INDIV");
	}

	@Test
	void compareTopLevelAttributeTin() throws XmlException, IOException {
		String jsonPath = "taxpayerIdentificationNumber";
		helper.executeAttributeTest(jsonPath, EXTENSION, "000777777");
	}

	@Test
	void compareTopLevelAttributeNpi() throws XmlException, IOException {
		String jsonPath = "nationalProviderIdentifier";
		helper.executeAttributeTest(jsonPath, EXTENSION, "0777777777");
	}

	@Test
	void compareTopLevelAttributeEntityId() throws XmlException, IOException {
		String jsonPath = "practiceId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "AR000000");
	}

	//ACI
	@Test
	void compareAciMeasurePerformanceEnd() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].performanceEnd";
		helper.executeAttributeTest(jsonPath, "value", "20170531");
	}

	@Test
	void compareAciMeasurePerformanceStart() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].performanceStart";
		helper.executeAttributeTest(jsonPath, "value", "20170201");
	}

	@Test
	void compareAciMeasurePerformedMeasureIdAciPea1() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].measureId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "ACI_PEA_1");
	}

	@Test
	void compareAciMeasurePerformedMeasureIdAciEp1() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[1].measureId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "ACI_EP_1");
	}
	
	@Test
	void compareAciMeasurePerformedMeasureIdAciPea1Numerator() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.numerator";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	void compareAciMeasurePerformedMeasureIdAciPea1Denominator() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.denominator";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	//IA
	@Test
	void compareIaMeasurePerformanceEnd() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].performanceEnd";
		helper.executeAttributeTest(jsonPath, "value", "20170430");
	}

	@Test
	void compareIaMeasurePerformanceStart() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].performanceStart";
		helper.executeAttributeTest(jsonPath, "value", "20170101");
	}

	@Test
	void compareIaMeasurePerformedMeasureIdIaEpa1Value() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[0].value";
		helper.executeAttributeTest(jsonPath, "code", "Y");
	}

	@Test
	void compareIaMeasurePerformedMeasureIdIaEpa1() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[0].measureId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "IA_EPA_3");
	}

	//Quality measure
	@Test
	void compareQualityMeasurePerformanceEnd() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].performanceEnd";
		helper.executeAttributeTest(jsonPath, "value", "20171231");
	}

	@Test
	void compareQualityMeasurePerformanceStart() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].performanceStart";
		helper.executeAttributeTest(jsonPath, "value", "20170101");
	}

	@Test
	void compareQualityMeasureIdValuePerformanceExclusion() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulationExclusion";
		helper.executeAttributeTest(jsonPath, "value", "50");
	}

	@Test
	void compareQualityMeasureIdValuePerformanceMet() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	@Test
	void compareQualityMeasureIdValueNumerator() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	@Test
	void compareQualityMeasureIdValueDenominator() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "1000");
	}

	@Test
	void compareQualityMeasureIdValueEligiblePopulation() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "1000");
	}

	@Test
	void compareQualityMeasureIdValueEligiblePopulationExclusion() throws IOException, XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulationExclusion";
		helper.executeAttributeTest(jsonPath, "value", "50");
	}

	@Test
	void nonexistentJsonPath() throws IOException, XmlException {
		Assertions.assertThrows(PathNotFoundException.class, () -> {
			String jsonPath = "meep.mawp";
			helper.executeAttributeTest(jsonPath, "", "");
		});
	}
}
