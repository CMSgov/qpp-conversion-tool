package gov.cms.qpp.conversion.api.internal.pii;

import java.io.InputStream;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.amazonaws.util.StringInputStream;
import com.google.common.truth.Truth;

import gov.cms.qpp.conversion.api.model.PcfValidationInfoMap;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.validate.NodeValidator;

import static gov.cms.qpp.conversion.model.Constants.*;

public class SpecPiiValidatorTest {

	@Test
	void testValidCombination() throws Exception {
		SpecPiiValidator validator = validator("DogCow_APM", "DogCow_NPI");
		Node node = node("DogCow_APM", "DogCow_NPI,DogCow_NPI2", "DogCow,DogCow", CPCPLUS_PROGRAM_NAME);
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
		Node node = node("DogCow_APM", "DogCow_NPI", "DogCow", CPCPLUS_PROGRAM_NAME);
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
		Node node = node("Valid_DogCow_APM", "Invalid_Entered_DogCow_NPI", "DogCow", CPCPLUS_PROGRAM_NAME);
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
		Node node = node("DogCow_APM", "Invalid_Entered_DogCow_NPI", "DogCow", CPCPLUS_PROGRAM_NAME);
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
		Node node = node("DogCow_APM", "DogCow_NPI", "_____INVALID", CPCPLUS_PROGRAM_NAME);
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
		Node node = node("DogCow_APM", "DogCow_NPI,DogCow_NPI2", "DogCow,DogCow", PCF_PROGRAM_NAME);
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
		Node node = node("Invalid_Apm", "DogCow_NPI,DogCow_NPI2", "DogCow,DogCow", PCF_PROGRAM_NAME);
		NodeValidator nodeValidator = new NodeValidator() {
			@Override
			protected void performValidation(Node node) {
			}
		};
		validator.validateApmTinNpiCombination(node, nodeValidator);
		Truth.assertThat(nodeValidator.viewWarnings().get(0).getErrorCode()).isEqualTo(108);
	}

//	@Test
//	void testNpiTinSizeMismatch() throws Exception {
//		SpecPiiValidator validator = validator("DogCow_APM", "DogCow_NPI");
//		Node node = node("Invalid_Apm", "DogCow_NPI,DogCow_NPI2", "DogCow", PCF_PROGRAM_NAME);
//		System.out.println(node);
//		NodeValidator nodeValidator = new NodeValidator() {
//			@Override
//			protected void performValidation(Node node) {
//				// Empty Function.  Just adding a comment to avoid sonar flag.
//			}
//		};
//		validator.validateApmTinNpiCombination(node, nodeValidator);
//		Truth.assertThat(nodeValidator.viewWarnings().get(0).getErrorCode()).isEqualTo(80);
//	}

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
		clinicalDocumentNode.putValue(PRACTICE_ID, apm);
		clinicalDocumentNode.putValue(PCF_ENTITY_ID, apm);
		clinicalDocumentNode.putValue(NATIONAL_PROVIDER_IDENTIFIER, npi);
		clinicalDocumentNode.putValue(TAX_PAYER_IDENTIFICATION_NUMBER, tin);
		clinicalDocumentNode.putValue(PROGRAM_NAME, programName);
		return clinicalDocumentNode;
	}

}
