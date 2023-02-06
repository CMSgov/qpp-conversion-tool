package gov.cms.qpp.conversion.api.internal.pii;

import java.io.InputStream;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.amazonaws.util.StringInputStream;
import com.google.common.truth.Truth;

import gov.cms.qpp.conversion.api.model.PcfValidationInfoMap;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.validate.NodeValidator;

public class SpecPiiValidatorTest {

	@Test
	void testValidCombination() throws Exception {
		SpecPiiValidator validator = validator("DogCow_APM", "DogCow_NPI");
		Node node = node("DogCow_APM", "DogCow_NPI,DogCow_NPI2", "DogCow,DogCow", ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		NodeValidator nodeValidator = new NodeValidator() {
			@Override
			protected void performValidation(Node node) {
			}
		};
		validator.validateApmTinNpiCombination(node, nodeValidator);
		Truth.assertThat(nodeValidator.viewWarnings()).isEmpty();
	}

	@Test
	void testDuplicateSpecStillValid() throws Exception {
		SpecPiiValidator validator = validatorWithDupeSpec("DogCow_APM", "DogCow_NPI");
		Node node = node("DogCow_APM", "DogCow_NPI", "DogCow", ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		NodeValidator nodeValidator = new NodeValidator() {
			@Override
			protected void performValidation(Node node) {
			}
		};
		validator.validateApmTinNpiCombination(node, nodeValidator);
		Truth.assertThat(nodeValidator.viewWarnings()).isEmpty();
	}

	@Test
	void testMissingAndInvalidCombination() throws Exception {
		SpecPiiValidator validator = validator("Valid_DogCow_APM", "Valid_DogCow_NPI");
		Node node = node("Valid_DogCow_APM", "Invalid_Entered_DogCow_NPI", "DogCow", ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		NodeValidator nodeValidator = new NodeValidator() {
			@Override
			protected void performValidation(Node node) {
			}
		};
		validator.validateApmTinNpiCombination(node, nodeValidator);
		Truth.assertThat(nodeValidator.viewWarnings()).isNotEmpty();
		Truth.assertThat(nodeValidator.viewWarnings()).hasSize(2);
	}

	@Test
	void testNullSpec() throws  Exception {
		SpecPiiValidator validator = validator("Valid_DogCow_APM", "Valid_DogCow_NPI");
		Node node = node("DogCow_APM", "Invalid_Entered_DogCow_NPI", "DogCow", ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		NodeValidator nodeValidator = new NodeValidator() {
			@Override
			protected void performValidation(Node node) {
			}
		};
		validator.validateApmTinNpiCombination(node, nodeValidator);
		Truth.assertThat(nodeValidator.viewWarnings()).isNotEmpty();
	}

	@Test
	void testMasking() throws Exception {
		SpecPiiValidator validator = validator("DogCow_APM", "DogCow_NPI");
		Node node = node("DogCow_APM", "DogCow_NPI", "_____INVALID", ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		NodeValidator nodeValidator = new NodeValidator() {
			@Override
			protected void performValidation(Node node) {
			}
		};
		validator.validateApmTinNpiCombination(node, nodeValidator);
		Truth.assertThat(nodeValidator.viewWarnings().get(0).getMessage()).contains("*****INVALID");
		Truth.assertThat(nodeValidator.viewWarnings().get(1).getMessage()).contains("*****w");
	}

	@Test
	void testValidPcfCombination() throws Exception {
		SpecPiiValidator validator = validator("DogCow_APM", "DogCow_NPI");
		Node node = node("DogCow_APM", "DogCow_NPI,DogCow_NPI2", "DogCow,DogCow", ClinicalDocumentDecoder.PCF_PROGRAM_NAME);
		NodeValidator nodeValidator = new NodeValidator() {
			@Override
			protected void performValidation(Node node) {
			}
		};
		validator.validateApmTinNpiCombination(node, nodeValidator);
		Truth.assertThat(nodeValidator.viewWarnings()).isEmpty();
	}

	@Test
	void testInvalidApm() throws Exception {
		SpecPiiValidator validator = validator("DogCow_APM", "DogCow_NPI");
		Node node = node("Invalid_Apm", "DogCow_NPI,DogCow_NPI2", "DogCow,DogCow", ClinicalDocumentDecoder.PCF_PROGRAM_NAME);
		NodeValidator nodeValidator = new NodeValidator() {
			@Override
			protected void performValidation(Node node) {
			}
		};
		validator.validateApmTinNpiCombination(node, nodeValidator);
		Truth.assertThat(nodeValidator.viewWarnings().get(0).getErrorCode()).isEqualTo(108);
	}

	private SpecPiiValidator validator(String apm, String npi) throws Exception {
		return new SpecPiiValidator(createSpecFile(apm, npi));
	}

	private SpecPiiValidator validatorWithDupeSpec(String apm, String npi) throws Exception {
		return new SpecPiiValidator(createDuplicatedSpecFile(apm, npi));
	}

	private PcfValidationInfoMap createSpecFile(String apm, String npi) throws Exception {
		String json = ("[\r\n" +
			    "   {\r\n" + 
				"		\"apm_entity_id\": \"{apm}\",\r\n" +
				"		\"tin\": \"DogCow\",\r\n" +
				"		\"npi\": \"{npi}\"\r\n" + 
				"	},\r\n" +
				"   {\r\n" +
				"		\"apm_entity_id\": \"DogCow_APM\",\r\n" +
				"		\"tin\": \"DogCow\",\r\n" +
				"		\"npi\": \"DogCow_NPI2\"\r\n" +
				"	}\r\n" +
				"]\r\n").replace("{apm}", apm).replace("{npi}", npi);
		InputStream jsonStream = new StringInputStream(json);
		PcfValidationInfoMap file = new PcfValidationInfoMap(jsonStream);
		Assumptions.assumeFalse(file.getApmTinNpiCombinationMap() == null);
		return file;
	}

	private PcfValidationInfoMap createDuplicatedSpecFile(String apm, String npi) throws Exception {
		String json = ("[\r\n" +
			"   {\r\n" +
			"		\"apm_entity_id\": \"{apm}\",\r\n" +
			"		\"tin\": \"DogCow\",\r\n" +
			"		\"npi\": \"{npi}\"\r\n" +
			"	},\r\n" +
			"   {\r\n" +
			"		\"apm_entity_id\": \"{apm}\",\r\n" +
			"		\"tin\": \"DogCow\",\r\n" +
			"		\"npi\": \"{npi}\"\r\n" +
			"	}\r\n" +
			"]\r\n").replace("{apm}", apm).replace("{npi}", npi);
		InputStream jsonStream = new StringInputStream(json);
		PcfValidationInfoMap file = new PcfValidationInfoMap(jsonStream);
		Assumptions.assumeFalse(file.getApmTinNpiCombinationMap() == null);
		return file;
	}

	private Node node(String apm, String npi, String tin, String programName) {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_ID, apm);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PCF_ENTITY_ID, apm);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER, npi);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, tin);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, programName);
		return clinicalDocumentNode;
	}

}
