package gov.cms.qpp.conversion.validate;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QrdaDecoderEngine;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import gov.cms.qpp.conversion.model.validation.SupplementalData;
import gov.cms.qpp.conversion.xml.XmlUtils;

import java.util.Set;

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
	void validateFailureSupplementalAfricanRaceDataTest() throws Exception {
		String failureRaceFile = TestHelper.getFixture("failureSupplementalRaceDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureRaceFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.AFRICAN_AMERICAN.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalAsianRaceDataTest() throws Exception {
		String failureRaceFile = TestHelper.getFixture("failureSupplementalRaceDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureRaceFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.ASIAN.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalHawaiianRaceDataTest() throws Exception {
		String failureRaceFile = TestHelper.getFixture("failureSupplementalRaceDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureRaceFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.HAWAIIAN_PACIFIC_ISLANDER.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalWhiteRaceDataTest() throws Exception {
		String failureRaceFile = TestHelper.getFixture("failureSupplementalRaceDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureRaceFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.WHITE.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalAlaskanRaceDataTest() throws Exception {
		String failureRaceFile = TestHelper.getFixture("failureSupplementalRaceDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureRaceFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.ALASKAN_NATIVE_AMERICAN_INDIAN.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalOtherRaceDataTest() throws Exception {
		String failureRaceFile = TestHelper.getFixture("failureSupplementalRaceDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureRaceFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.OTHER_RACE.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalRaceCodeMissing() throws Exception {
		String failureSexFile = TestHelper.getFixture(MISSING_SUPPLEMENTAL_CODES_FILE);
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureSexFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.AFRICAN_AMERICAN.getCode(),
			MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalMaleSexDataTest() throws Exception {
		String failureSexFile = TestHelper.getFixture("failureSupplementalSexDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureSexFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.MALE.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalFemaleSexDataTest() throws Exception {
		String failureSexFile = TestHelper.getFixture("failureSupplementalSexDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureSexFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.FEMALE.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalSexCodeMissing() throws Exception {
		String failureSexFile = TestHelper.getFixture(MISSING_SUPPLEMENTAL_CODES_FILE);
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureSexFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.MALE.getCode(),
			MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalEthnicityNotHispanicDataTest() throws Exception {
		String failureEthnicityFile = TestHelper.getFixture("failureSupplementalEthnicityDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureEthnicityFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.NOT_HISPANIC_LATINO.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalEthnicityHispanicDataTest() throws Exception {
		String failureEthnicityFile = TestHelper.getFixture("failureSupplementalEthnicityDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureEthnicityFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.HISPANIC_LATINO.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalEthnicityCodeMissing() throws Exception {
		String failureSexFile = TestHelper.getFixture(MISSING_SUPPLEMENTAL_CODES_FILE);
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureSexFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.NOT_HISPANIC_LATINO.getCode(),
			MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalPayerMedicareDataTest() throws Exception {
		String failurePayerFile = TestHelper.getFixture("failureSupplementalPayerDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failurePayerFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.MEDICARE.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalPayerMedicaidDataTest() throws Exception {
		String failurePayerFile = TestHelper.getFixture("failureSupplementalPayerDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failurePayerFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.MEDICAID.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalPayerPrivateDataTest() throws Exception {
		String failurePayerFile = TestHelper.getFixture("failureSupplementalPayerDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failurePayerFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.PRIVATE_HEALTH_INSURANCE.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalPayerOtherDataTest() throws Exception {
		String failurePayerFile = TestHelper.getFixture("failureSupplementalPayerDataFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failurePayerFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.OTHER_PAYER.getCode(),
				MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalPayerCodeMissing() throws Exception {
		String failureSexFile = TestHelper.getFixture(MISSING_SUPPLEMENTAL_CODES_FILE);
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failureSexFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(SupplementalData.MEDICARE.getCode(),
			MEASURE_ID, SubPopulations.IPOP);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(expectedError);
	}

	@Test
	void validateFailureSupplementalDataMissingCountTest() throws Exception {
		String failurePayerFile = TestHelper.getFixture("failureSupplementalDataCountFile.xml");
		Node placeholder = new QrdaDecoderEngine(new Context()).decode(XmlUtils.stringToDom(failurePayerFile));
		CpcMeasureDataValidator validator = new CpcMeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		LocalizedError expectedError = ErrorCode.CPC_PLUS_SUPPLEMENTAL_DATA_MISSING_COUNT.format(
				 SupplementalData.MALE.getCode(), SubPopulations.IPOP, MEASURE_ID);

		Set<Detail> errors = validator.getDetails();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(expectedError);
	}
}
