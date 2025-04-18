package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.model.Constants.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

class ClinicalDocumentValidatorTest {

	private static final String CLINICAL_DOCUMENT_ERROR_FILE = "angerClinicalDocumentValidations-error.json";

	@AfterEach
	void cleanup() throws IOException {
		Files.deleteIfExists(Path.of(CLINICAL_DOCUMENT_ERROR_FILE));
	}

	@Test
	void testClinicalDocumentPresent() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		clinicalDocumentNode.addChildNode(aciSectionNode);
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void testClinicalDocumentPresentIa() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node iaSectionNode = createIASectionNode(clinicalDocumentNode);
		clinicalDocumentNode.addChildNode(iaSectionNode);
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void testClinicalDocumentPresentEcQM() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node ecqmSectionNode = new Node(TemplateId.MEASURE_SECTION_V5, clinicalDocumentNode);
		ecqmSectionNode.putValue("category", "eCQM");
		clinicalDocumentNode.addChildNode(ecqmSectionNode);
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void testNoSections() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("error should be about missing section node")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CLINICAL_DOCUMENT_MISSING_PI_OR_IA_OR_ECQM_CHILD);
	}

	@Test
	void testNoSectionsOtherChildren() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node placeholderNode = new Node(TemplateId.PLACEHOLDER);

		clinicalDocumentNode.addChildNode(placeholderNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("error should be about missing section node")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CLINICAL_DOCUMENT_MISSING_PI_OR_IA_OR_ECQM_CHILD);
	}

	@Test
	void testMissingName() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("error should be about missing missing program name")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME.format(ClinicalDocumentValidator.VALID_PROGRAM_NAMES));
	}

	@Test
	void testMissingTin() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("there should NOT be an error")
				.that(errors).isEmpty();
	}

	@Test
	void testNpiIsOptional() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("there should be no errors")
				.that(errors).isEmpty();
	}

	@Test
	void testDuplicateAciSectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		Node duplicateAciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(aciSectionNode, duplicateAciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Should contain one error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_PI_SECTIONS);
	}

	@Test
	void testDuplicateIASectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node IASectionNode = createIASectionNode(clinicalDocumentNode);
		Node duplicateIASectionNode = createIASectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(IASectionNode, duplicateIASectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Should contain one error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS);
	}

	@Test
	void testDuplicateQualityMeasureSectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node qualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);
		Node duplicateQualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(qualityMeasureNode, duplicateQualityMeasureNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Should contain one error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS);
	}

	@Test
	void testMultipleNonDuplicatedSectionsIsValid() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		Node IASectionNode = createIASectionNode(clinicalDocumentNode);
		Node qualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(aciSectionNode, IASectionNode, qualityMeasureNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Should have no validation errors")
				.that(errors).isEmpty();
	}

	@Test
	void testClinicalDocumentValidationParsesMultipleErrors() {
		//setup
		Path path = Path.of("src/test/resources/negative/angerClinicalDocumentValidations.xml");

		//execute
		Context context = new Context();
		Converter converter = new Converter(new PathSource(path), context);
		AllErrors allErrors = new AllErrors();
		try {
			converter.transform();
		} catch(TransformException exception) {
			allErrors = exception.getDetails();
		}

		List<Detail> errors = getErrors(allErrors);

		assertWithMessage("Must have 3 errors")
				.that(errors).hasSize(3);

		assertWithMessage("Must contain the correct errors")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsAtLeast(
						ProblemCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME.format(ClinicalDocumentValidator.VALID_PROGRAM_NAMES),
						ProblemCode.REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START,
						ProblemCode.IA_SECTION_MISSING_REPORTING_PARAM);
	}

	@Test
	void testInvalidProgramName() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		clinicalDocumentNode.addChildNodes(aciSectionNode);
		String invalidProgramName = "Invalid program name";
		clinicalDocumentNode.putValue(PROGRAM_NAME,invalidProgramName);
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Must contain the error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME.format(ClinicalDocumentValidator.VALID_PROGRAM_NAMES, invalidProgramName));
	}

	@Test
	void testMissingVirtualGroupId() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		clinicalDocumentNode.putValue(ENTITY_TYPE, ENTITY_VIRTUAL_GROUP);
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		clinicalDocumentNode.addChildNode(aciSectionNode);
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.VIRTUAL_GROUP_ID_REQUIRED);
	}

	@Test
	void testSuccessVirtualGroupId() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		clinicalDocumentNode.putValue(ENTITY_TYPE, ENTITY_VIRTUAL_GROUP);
		clinicalDocumentNode.putValue(ENTITY_ID, "x12345");
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		clinicalDocumentNode.addChildNode(aciSectionNode);
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertThat(errors).isEmpty();
	}


	private List<Detail> getErrors(AllErrors content) {
		return content.getErrors().get(0).getDetails();
	}

	private Node createValidClinicalDocumentNode() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		return clinicalDocumentNode;
	}

	private Node createAciSectionNode(Node clinicalDocumentNode) {
		Node aciSectionNode = new Node(TemplateId.PI_SECTION_V3, clinicalDocumentNode);
		aciSectionNode.putValue("category", "aci");
		return aciSectionNode;
	}

	private Node createIASectionNode(Node clinicalDocumentNode) {
		Node IASectionNode = new Node(TemplateId.IA_SECTION_V3, clinicalDocumentNode);
		IASectionNode.putValue("category", "ia");
		return IASectionNode;
	}

	private Node createQualityMeasureSectionNode(Node clinicalDocumentNode) {
		Node qualityMeasureNode = new Node(TemplateId.MEASURE_SECTION_V5, clinicalDocumentNode);
		qualityMeasureNode.putValue("category", "ecqm");
		return qualityMeasureNode;
	}
}