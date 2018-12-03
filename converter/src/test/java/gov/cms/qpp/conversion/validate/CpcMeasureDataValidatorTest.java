package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QrdaDecoderEngine;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.model.validation.SupplementalData;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Set;
import java.util.function.Consumer;

import static com.google.common.truth.Truth.assertThat;

class CpcMeasureDataValidatorTest {

	private static final String MEASURE_ID = "CMS122v5";
	private static final String MISSING_SUPPLEMENTAL_CODES_FILE = "missingSupplementalCodeFile.xml";

	@Test
	void validateSuccessfulSupplementalDataFieldsTest() throws Exception {
		String successfulFile = TestHelper.getFixture("successfulSupplementalDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(successfulFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		Set<Detail> errors = validator.getDetails();
		assertThat(errors).isEmpty();
	}

	@Test
	void validateFailureSupplementalDataMissingCountTest() throws Exception {
		String failurePayerFile = TestHelper.getFixture("failureSupplementalDataCountFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failurePayerFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_SUPPLEMENTAL_DATA_MISSING_COUNT.format(
			SupplementalData.MALE.getCode(), SubPopulationLabel.IPOP.name(), MEASURE_ID);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(expectedError);
	}

	@DisplayName("Should fail on absent supplemental race data")
	@ParameterizedTest(name = "{index} => Supplemental data=''{0}''")
	@EnumSource(value = SupplementalData.class, mode = EnumSource.Mode.INCLUDE,
		names = {"ALASKAN_NATIVE_AMERICAN_INDIAN", "ASIAN", "AFRICAN_AMERICAN",
			"HAWAIIAN_PACIFIC_ISLANDER", "WHITE", "OTHER_RACE"})
	void validateFailureSupplementalRaceDataTest(SupplementalData supplementalData) {
		supplementalDataCheck("failureSupplementalRaceDataFile.xml").accept(supplementalData);
	}

	@DisplayName("Should fail on absent supplemental ethnicity data")
	@ParameterizedTest(name = "{index} => Supplemental data=''{0}''")
	@EnumSource(value = SupplementalData.class, mode = EnumSource.Mode.INCLUDE,
		names = {"HISPANIC_LATINO", "NOT_HISPANIC_LATINO"})
	void validateFailureSupplementalEthnicityDataTest(SupplementalData supplementalData) {
		supplementalDataCheck("failureSupplementalEthnicityDataFile.xml").accept(supplementalData);
	}

	@DisplayName("Should fail on absent supplemental sex data")
	@ParameterizedTest(name = "{index} => Supplemental data=''{0}''")
	@EnumSource(value = SupplementalData.class, mode = EnumSource.Mode.INCLUDE,
		names = {"MALE", "FEMALE"})
	void validateFailureSupplementalSexDataTest(SupplementalData supplementalData) {
		supplementalDataCheck("failureSupplementalSexDataFile.xml").accept(supplementalData);
	}

	@DisplayName("Should fail on absent supplemental payer data")
	@ParameterizedTest(name = "{index} => Supplemental data=''{0}''")
	@EnumSource(value = SupplementalData.class, mode = EnumSource.Mode.INCLUDE,
		names = {"MEDICAID", "PRIVATE_HEALTH_INSURANCE", "OTHER_PAYER", "MEDICARE"})
	void validateFailureSupplementalPayerDataTest(SupplementalData supplementalData) {
		supplementalDataCheck("failureSupplementalPayerDataFile.xml").accept(supplementalData);
	}

	@DisplayName("Should fail when missing supplemental codes")
	@ParameterizedTest(name = "{index} => Supplemental data=''{0}''")
	@EnumSource(value = SupplementalData.class, mode = EnumSource.Mode.INCLUDE,
		names = {"AFRICAN_AMERICAN", "MALE", "NOT_HISPANIC_LATINO", "MEDICARE"})
	void validateFailureSupplementalRaceCodeMissing(SupplementalData supplementalData) {
		supplementalDataCheck(MISSING_SUPPLEMENTAL_CODES_FILE).accept(supplementalData);
	}

	private Consumer<SupplementalData> supplementalDataCheck(final String scenarioFile) {
		return (supplementalData) -> {
			try {
				String failureFile = TestHelper.getFixture(scenarioFile);
				Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureFile));
				CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
				Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
				validator.internalValidateSingleNode(underTest);

				LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE
					.format(supplementalData.getType(), supplementalData, supplementalData.getCode(),
						MEASURE_ID, SubPopulationLabel.IPOP.name());

				Set<Detail> errors = validator.getDetails();

				assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
					.contains(expectedError);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
}
