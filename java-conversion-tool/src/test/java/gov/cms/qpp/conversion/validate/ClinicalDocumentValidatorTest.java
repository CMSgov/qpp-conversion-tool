package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.ConversionFileWriterWrapper;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static gov.cms.qpp.conversion.util.JsonHelper.readJson;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.junit.Assert.assertThat;

public class ClinicalDocumentValidatorTest {

	private static final String EXPECTED_NO_SECTION = "Clinical Document Node must have at least one Aci or IA or eCQM Section Node as a child";
	private static final String CLINICAL_DOCUMENT_ERROR_FILE = "angerClinicalDocumentValidations.err.json";

	@After
	public void cleanup() throws IOException {
		Files.deleteIfExists(Paths.get(CLINICAL_DOCUMENT_ERROR_FILE));
	}

	@Test
	public void testClinicalDocumentPresent() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testClinicalDocumentPresentIa() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		Node iaSectionNode = createIASectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(iaSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testClinicalDocumentPresentEcQM() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		Node ecqmSectionNode = new Node(TemplateId.MEASURE_SECTION_V2, clinicalDocumentNode);
		ecqmSectionNode.putValue("category", "eCQM");

		clinicalDocumentNode.addChildNode(ecqmSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testNoSections() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing section node", errors,
			hasValidationErrorsIgnoringPath(EXPECTED_NO_SECTION));
	}

	@Test
	public void testNoSectionsOtherChildren() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		Node placeholderNode = new Node(TemplateId.PLACEHOLDER);

		clinicalDocumentNode.addChildNode(placeholderNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, hasSize(1));
		assertThat("error should be about missing section node", errors,
			hasValidationErrorsIgnoringPath(EXPECTED_NO_SECTION));
	}

	@Test
	public void testMissingName() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be two errors", errors, hasSize(2));
		assertThat("error should be about missing missing program name", errors,
			hasValidationErrorsIgnoringPath(
				ClinicalDocumentValidator.CONTAINS_PROGRAM_NAME,
				ClinicalDocumentValidator.INCORRECT_PROGRAM_NAME));
	}

	@Test
	public void testMissingTin() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, hasSize(1));
		assertThat("error should be about missing section node", errors,
			hasValidationErrorsIgnoringPath(ClinicalDocumentValidator.CONTAINS_TAX_ID_NUMBER));
	}

	@Test
	public void testNpiIsOptional() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be no errors", errors, empty());
	}

	@Test
	public void testDuplicateAciSectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		Node duplicateAciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(aciSectionNode, duplicateAciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("Should contain one error", errors, hasSize(1));
		assertThat("Should contain one error", errors,
			hasValidationErrorsIgnoringPath(ClinicalDocumentValidator.CONTAINS_DUPLICATE_ACI_SECTIONS));
	}

	@Test
	public void testDuplicateIASectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		Node IASectionNode = createIASectionNode(clinicalDocumentNode);

		Node duplicateIASectionNode = createIASectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(IASectionNode, duplicateIASectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("Should contain one error", errors, hasSize(1));
		assertThat("Should contain one error", errors,
			hasValidationErrorsIgnoringPath(ClinicalDocumentValidator.CONTAINS_DUPLICATE_IA_SECTIONS));
	}

	@Test
	public void testDuplicateQualityMeasureSectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		Node qualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);

		Node duplicateQualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(qualityMeasureNode, duplicateQualityMeasureNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("Should contain one error", errors, hasSize(1));
		assertThat("Should contain one error", errors,
			hasValidationErrorsIgnoringPath(ClinicalDocumentValidator.CONTAINS_DUPLICATE_ECQM_SECTIONS));
	}

	@Test
	public void testMultipleNonDuplicatedSectionsIsValid() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		Node IASectionNode = createIASectionNode(clinicalDocumentNode);

		Node qualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(aciSectionNode, IASectionNode, qualityMeasureNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("Should have no validation errors", errors, empty());
	}

	@Test
	public void testClinicalDocumentValidationParsesMultipleErrors() throws IOException {
		//setup
		Path path = Paths.get("src/test/resources/negative/angerClinicalDocumentValidations.xml");

		//execute
		new ConversionFileWriterWrapper(path).transform();
		AllErrors allErrors = readJson(CLINICAL_DOCUMENT_ERROR_FILE, AllErrors.class);
		List<Detail> errors = getErrors(allErrors);

		assertThat("Must have 6 errors", errors, hasSize(6));

		assertThat("Must contain the error", errors,
			hasValidationErrorsIgnoringPath(
				ClinicalDocumentValidator.CONTAINS_PROGRAM_NAME,
				ClinicalDocumentValidator.INCORRECT_PROGRAM_NAME,
				ClinicalDocumentValidator.CONTAINS_TAX_ID_NUMBER,
				ReportingParametersActValidator.SINGLE_PERFORMANCE_START));
	}

	@Test
	public void testInvalidProgramName() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		clinicalDocumentNode.addChildNodes(aciSectionNode);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME,"Invalid program name");
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		Set<Detail> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("Should have 1 validation errors", errors, hasSize(1));
		assertThat("Must contain the error", errors,
			hasValidationErrorsIgnoringPath(ClinicalDocumentValidator.INCORRECT_PROGRAM_NAME));
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