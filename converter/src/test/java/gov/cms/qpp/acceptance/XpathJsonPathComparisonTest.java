package gov.cms.qpp.acceptance;

import com.jayway.jsonpath.PathNotFoundException;
import gov.cms.qpp.acceptance.helper.JsonPathToXpathHelper;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.xml.XmlException;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class XpathJsonPathComparisonTest {

	private static JsonWrapper wrapper = new JsonWrapper();
	private static Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
	private static JsonPathToXpathHelper helper;
	private static final String EXTENSION = "extension";

	@BeforeAll
	static void beforeClass() {
		helper = new JsonPathToXpathHelper(path, wrapper);
	}

	@Test
	void compareTopLevelElement() throws XmlException {
		helper.executeElementTest("", "ClinicalDocument");
	}

	@Test
	void compareTopLevelAttributeProgramName() throws XmlException {
		String jsonPath = "programName";
		helper.executeAttributeTest(jsonPath, EXTENSION, "MIPS_INDIV");
	}

	@Test
	void compareTopLevelAttributeTin() throws XmlException {
		String jsonPath = "taxpayerIdentificationNumber";
		helper.executeAttributeTest(jsonPath, EXTENSION, "000777777");
	}

	@Test
	void compareTopLevelAttributeNpi() throws XmlException {
		String jsonPath = "nationalProviderIdentifier";
		helper.executeAttributeTest(jsonPath, EXTENSION, "0777777777");
	}

	@Test
	void compareTopLevelAttributeEntityId() throws XmlException {
		String jsonPath = "practiceId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "AR000000");
	}

	//ACI
	@Test
	void compareAciMeasurePerformanceEnd() throws XmlException {
		String jsonPath = "measurementSets[1].performanceEnd";
		helper.executeAttributeTest(jsonPath, "value", "20230531");
	}

	@Test
	void compareAciMeasurePerformanceStart() throws XmlException {
		String jsonPath = "measurementSets[1].performanceStart";
		helper.executeAttributeTest(jsonPath, "value", "20230201");
	}

	@Test
	void compareAciMeasurePerformedMeasureIdPiPea1() throws XmlException {
		String jsonPath = "measurementSets[1].measurements[0].measureId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "PI_PEA_1");
	}
	
	@Test
	void compareAciMeasurePerformedMeasureIdAciPea1Numerator() throws XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.numerator";
		helper.executeAttributeTest(jsonPath, "value", "600");
	}

	@Test
	void compareAciMeasurePerformedMeasureIdAciPea1Denominator() throws XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.denominator";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	//IA
	@Test
	void compareIaMeasurePerformanceEnd() throws XmlException {
		String jsonPath = "measurementSets[2].performanceEnd";
		helper.executeAttributeTest(jsonPath, "value", "20230430");
	}

	@Test
	void compareIaMeasurePerformanceStart() throws XmlException {
		String jsonPath = "measurementSets[2].performanceStart";
		helper.executeAttributeTest(jsonPath, "value", "20230101");
	}

	@Test
	void compareIaMeasurePerformedMeasureIdIaEpa1Value() throws XmlException {
		String jsonPath = "measurementSets[2].measurements[0].value";
		helper.executeAttributeTest(jsonPath, "code", "Y");
	}

	@Test
	void compareIaMeasurePerformedMeasureIdIaEpa1() throws XmlException {
		String jsonPath = "measurementSets[2].measurements[0].measureId";
		helper.executeAttributeTest(jsonPath, EXTENSION, "IA_EPA_3");
	}

	//Quality measure
	@Test
	void compareQualityMeasurePerformanceEnd() throws XmlException {
		String jsonPath = "measurementSets[0].performanceEnd";
		helper.executeAttributeTest(jsonPath, "value", "20231231");
	}

	@Test
	void compareQualityMeasurePerformanceStart() throws XmlException {
		String jsonPath = "measurementSets[0].performanceStart";
		helper.executeAttributeTest(jsonPath, "value", "20230101");
	}

	@Test
	void compareQualityMeasureIdValuePerformanceExclusion() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulationExclusion";
		helper.executeAttributeTest(jsonPath, "value", "100");
	}

	@Test
	void compareQualityMeasureIdValuePerformanceMet() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	@Test
	void compareQualityMeasureIdValueNumerator() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.performanceMet";
		helper.executeAttributeTest(jsonPath, "value", "800");
	}

	@Test
	void compareQualityMeasureIdValueDenominator() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "1000");
	}

	@Test
	void compareQualityMeasureIdValueEligiblePopulation() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulation";
		helper.executeAttributeTest(jsonPath, "value", "1000");
	}

	@Test
	void compareQualityMeasureIdValueEligiblePopulationExclusion() throws XmlException {
		String jsonPath = "measurementSets[0].measurements[0].value.eligiblePopulationExclusion";
		helper.executeAttributeTest(jsonPath, "value", "100");
	}

	@Test
	void nonexistentJsonPath() {
		Assertions.assertThrows(PathNotFoundException.class, () -> {
			String jsonPath = "meep.mawp";
			helper.executeAttributeTest(jsonPath, "", "");
		});
	}
}
