package gov.cms.qpp.conversion.validate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QrdaDecoderEngine;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.model.validation.SupplementalData;
import gov.cms.qpp.conversion.xml.XmlUtils;

import java.util.List;
import java.util.function.Consumer;

import static com.google.common.truth.Truth.assertThat;

public class PcfMeasureDataValidatorTest {
	private static final String MEASURE_ID = "CMS122v7";
	private static final String MISSING_SUPPLEMENTAL_CODES_FILE = "missingSupplementalCodeFile.xml";

	@BeforeAll
	static void setup() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.TEST_MEASURE_DATA);
	}

	@AfterAll
	static void teardown(){
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@Test
	void testSuccessfulSupplementalDataFieldsTest() throws Exception {
		String successfulFile = TestHelper.getFixture("successfulSupplementalDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(successfulFile));
		PcfMeasureDataValidator validator = new PcfMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V4);
		List<Detail> errors = validator.validateSingleNode(underTest).getErrors();
		assertThat(errors).isEmpty();
	}

	@Test
	void testFailureSupplementalDataMissingCountTest() throws Exception {
		String failurePayerFile = TestHelper.getFixture("failureSupplementalDataCountFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failurePayerFile));
		PcfMeasureDataValidator validator = new PcfMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V4);
		List<Detail> errors = validator.validateSingleNode(underTest).getErrors();

		LocalizedProblem expectedError = ProblemCode.CPC_PCF_PLUS_SUPPLEMENTAL_DATA_MISSING_COUNT.format(
			SupplementalData.MALE.getCode(), SubPopulationLabel.IPOP.name(), MEASURE_ID);

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(expectedError);
	}

	@DisplayName("Should fail on absent supplemental race data")
	@ParameterizedTest(name = "{index} => Supplemental data=''{0}''")
	@EnumSource(value = SupplementalData.class, mode = EnumSource.Mode.INCLUDE,
		names = {"ALASKAN_NATIVE_AMERICAN_INDIAN", "ASIAN", "AFRICAN_AMERICAN",
			"HAWAIIAN_PACIFIC_ISLANDER", "WHITE", "OTHER_RACE"})
	void testFailureSupplementalRaceDataTest(SupplementalData supplementalData) {
		supplementalDataCheck("failureSupplementalRaceDataFile.xml").accept(supplementalData);
	}

	@DisplayName("Should fail on absent supplemental ethnicity data")
	@ParameterizedTest(name = "{index} => Supplemental data=''{0}''")
	@EnumSource(value = SupplementalData.class, mode = EnumSource.Mode.INCLUDE,
		names = {"HISPANIC_LATINO", "NOT_HISPANIC_LATINO"})
	void testFailureSupplementalEthnicityDataTest(SupplementalData supplementalData) {
		supplementalDataCheck("failureSupplementalEthnicityDataFile.xml").accept(supplementalData);
	}

	@DisplayName("Should fail on absent supplemental sex data")
	@ParameterizedTest(name = "{index} => Supplemental data=''{0}''")
	@EnumSource(value = SupplementalData.class, mode = EnumSource.Mode.INCLUDE,
		names = {"MALE", "FEMALE"})
	void testFailureSupplementalSexDataTest(SupplementalData supplementalData) {
		supplementalDataCheck("failureSupplementalSexDataFile.xml").accept(supplementalData);
	}

	@DisplayName("Should fail on absent supplemental payer data")
	@ParameterizedTest(name = "{index} => Supplemental data=''{0}''")
	@EnumSource(value = SupplementalData.class, mode = EnumSource.Mode.INCLUDE,
		names = {"MEDICAID", "PRIVATE_HEALTH_INSURANCE", "OTHER_PAYER", "MEDICARE"})
	void testFailureSupplementalPayerDataTest(SupplementalData supplementalData) {
		supplementalDataCheck("failureSupplementalPayerDataFile.xml").accept(supplementalData);
	}

	@DisplayName("Should fail when missing supplemental codes")
	@ParameterizedTest(name = "{index} => Supplemental data=''{0}''")
	@EnumSource(value = SupplementalData.class, mode = EnumSource.Mode.INCLUDE,
		names = {"AFRICAN_AMERICAN", "MALE", "NOT_HISPANIC_LATINO", "MEDICARE"})
	void testFailureSupplementalRaceCodeMissing(SupplementalData supplementalData) {
		supplementalDataCheck(MISSING_SUPPLEMENTAL_CODES_FILE).accept(supplementalData);
	}

	private Consumer<SupplementalData> supplementalDataCheck(final String scenarioFile) {
		return (supplementalData) -> {
			try {
				String failureFile = TestHelper.getFixture(scenarioFile);
				Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureFile));
				PcfMeasureDataValidator validator = new PcfMeasureDataValidator();
				Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V4);
				List<Detail> errors = validator.validateSingleNode(underTest).getErrors();

				LocalizedProblem expectedError = ProblemCode.CPC_PCF_PLUS_MISSING_SUPPLEMENTAL_CODE
					.format(supplementalData.getType(), supplementalData, supplementalData.getCode(),
						MEASURE_ID, SubPopulationLabel.IPOP.name());

				assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
					.contains(expectedError);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
}
