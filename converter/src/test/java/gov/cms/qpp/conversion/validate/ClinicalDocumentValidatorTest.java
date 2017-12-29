package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

class ClinicalDocumentValidatorTest {

	private static final String CLINICAL_DOCUMENT_ERROR_FILE = "angerClinicalDocumentValidations.err.json";

	@AfterEach
	void cleanup() throws IOException {
		Files.deleteIfExists(Paths.get(CLINICAL_DOCUMENT_ERROR_FILE));
	}

	@Test
	void testClinicalDocumentPresent() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		clinicalDocumentNode.addChildNode(aciSectionNode);
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void testClinicalDocumentPresentIa() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node iaSectionNode = createIASectionNode(clinicalDocumentNode);
		clinicalDocumentNode.addChildNode(iaSectionNode);
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void testClinicalDocumentPresentEcQM() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node ecqmSectionNode = new Node(TemplateId.MEASURE_SECTION_V2, clinicalDocumentNode);
		ecqmSectionNode.putValue("category", "eCQM");
		clinicalDocumentNode.addChildNode(ecqmSectionNode);
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void testNoSections() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("error should be about missing section node")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CLINICAL_DOCUMENT_MISSING_ACI_OR_IA_OR_ECQM_CHILD);
	}

	@Test
	void testNoSectionsOtherChildren() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node placeholderNode = new Node(TemplateId.PLACEHOLDER);

		clinicalDocumentNode.addChildNode(placeholderNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("error should be about missing section node")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CLINICAL_DOCUMENT_MISSING_ACI_OR_IA_OR_ECQM_CHILD);
	}

	@Test
	void testMissingName() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("error should be about missing missing program name")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME,
						ErrorCode.CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME);
	}

	@Test
	void testMissingTin() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

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
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

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
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("Should contain one error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_ACI_SECTIONS);
	}

	@Test
	void testDuplicateIASectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node IASectionNode = createIASectionNode(clinicalDocumentNode);
		Node duplicateIASectionNode = createIASectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(IASectionNode, duplicateIASectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("Should contain one error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS);
	}

	@Test
	void testDuplicateQualityMeasureSectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node qualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);
		Node duplicateQualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(qualityMeasureNode, duplicateQualityMeasureNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("Should contain one error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS);
	}

	@Test
	void testMultipleNonDuplicatedSectionsIsValid() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		Node IASectionNode = createIASectionNode(clinicalDocumentNode);
		Node qualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(aciSectionNode, IASectionNode, qualityMeasureNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("Should have no validation errors")
				.that(errors).isEmpty();
	}

	@Test
	void testClinicalDocumentValidationParsesMultipleErrors() throws IOException {
		//setup
		Path path = Paths.get("src/test/resources/negative/angerClinicalDocumentValidations.xml");

		//execute
		Converter converter = new Converter(new PathSource(path));
		AllErrors allErrors = new AllErrors();
		try {
			converter.transform();
		} catch(TransformException exception) {
			allErrors = exception.getDetails();
		}

		List<Detail> errors = getErrors(allErrors);

		assertWithMessage("Must have 5 errors")
				.that(errors).hasSize(5);

		assertWithMessage("Must contain the correct errors")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsAllOf(ErrorCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME,
						ErrorCode.CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME,
						ErrorCode.REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START);
	}

	@Test
	void testInvalidProgramName() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		clinicalDocumentNode.addChildNodes(aciSectionNode);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME,"Invalid program name");
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must contain the error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME);
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
		Node aciSectionNode = new Node(TemplateId.ACI_SECTION, clinicalDocumentNode);
		aciSectionNode.putValue("category", "aci");
		return aciSectionNode;
	}

	private Node createIASectionNode(Node clinicalDocumentNode) {
		Node IASectionNode = new Node(TemplateId.IA_SECTION, clinicalDocumentNode);
		IASectionNode.putValue("category", "ia");
		return IASectionNode;
	}

	private Node createQualityMeasureSectionNode(Node clinicalDocumentNode) {
		Node qualityMeasureNode = new Node(TemplateId.MEASURE_SECTION_V2, clinicalDocumentNode);
		qualityMeasureNode.putValue("category", "ecqm");
		return qualityMeasureNode;
	}
}