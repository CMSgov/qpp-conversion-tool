package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.ConversionFileWriterWrapper;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.containsValidationErrorInAnyOrderIgnoringPath;
import static gov.cms.qpp.conversion.util.JsonHelper.readJson;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.junit.Assert.assertThat;

public class ClinicalDocumentValidatorTest {

	private static final String EXPECTED_TEXT = "Clinical Document Node is required";
	private static final String EXPECTED_ONE_ALLOWED = "Only one Clinical Document Node is allowed";
	private static final String EXPECTED_NO_SECTION = "Clinical Document Node must have at least one Aci or IA or eCQM Section Node as a child";
	private static final String CLINICAL_DOCUMENT_ERROR_FILE = "angerClinicalDocumentValidations.err.json";

	@After
	public void cleanup() throws IOException {
		Files.deleteIfExists(Paths.get(CLINICAL_DOCUMENT_ERROR_FILE));
	}

	@Test
	public void testClinicalDocumentPresent() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		clinicalDocumentNode.addChildNode(createReportingNode());

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testClinicalDocumentPresentIa() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		clinicalDocumentNode.addChildNode(createReportingNode());

		Node iaSectionNode = createIASectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(iaSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testClinicalDocumentPresentEcQM() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		clinicalDocumentNode.addChildNode(createReportingNode());

		Node ecqmSectionNode = new Node(TemplateId.MEASURE_SECTION_V2, clinicalDocumentNode);
		ecqmSectionNode.putValue("category", "eCQM");

		clinicalDocumentNode.addChildNode(ecqmSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testClinicalDocumentNotPresent() {
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSameTemplateIdNodes(Arrays.asList());

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing Clinical Document node", errors,
			containsValidationErrorInAnyOrderIgnoringPath(EXPECTED_TEXT));
	}

	@Test
	public void testTooManyClinicalDocumentNodes() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		Node clinicalDocumentNode2 = new Node(TemplateId.CLINICAL_DOCUMENT);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSameTemplateIdNodes(Arrays.asList(clinicalDocumentNode, clinicalDocumentNode2));

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about too many Clinical Document nodes", errors,
			containsValidationErrorInAnyOrderIgnoringPath(EXPECTED_ONE_ALLOWED));
	}

	@Test
	public void testNoSections() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		clinicalDocumentNode.addChildNode(createReportingNode());

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing section node", errors,
			containsValidationErrorInAnyOrderIgnoringPath(EXPECTED_NO_SECTION));
	}

	@Test
	public void testNoSectionsOtherChildren() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		clinicalDocumentNode.addChildNode(createReportingNode());

		Node placeholderNode = new Node(TemplateId.PLACEHOLDER);

		clinicalDocumentNode.addChildNode(placeholderNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, hasSize(1));
		assertThat("error should be about missing section node", errors,
			containsValidationErrorInAnyOrderIgnoringPath(EXPECTED_NO_SECTION));
	}

	@Test
	public void testMissingName() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.addChildNode(createReportingNode());

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be two errors", errors, hasSize(2));
		assertThat("error should be about missing missing program name", errors,
			containsValidationErrorInAnyOrderIgnoringPath(
				ClinicalDocumentValidator.CONTAINS_PROGRAM_NAME,
				ClinicalDocumentValidator.INCORRECT_PROGRAM_NAME));
	}

	@Test
	public void testMissingTin() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.addChildNode(createReportingNode());

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, hasSize(1));
		assertThat("error should be about missing section node", errors,
			containsValidationErrorInAnyOrderIgnoringPath(ClinicalDocumentValidator.CONTAINS_TAX_ID_NUMBER));
	}

	@Test
	public void testNpiIsOptional() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");

		clinicalDocumentNode.addChildNode(createReportingNode());

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be no errors", errors, empty());
	}

	@Test
	public void testClinicalDocumentMissingPerformanceStartPresent() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, hasSize(2));
		assertThat("error should be about missing reporting node", errors,
			containsValidationErrorInAnyOrderIgnoringPath(
					ClinicalDocumentValidator.REPORTING_PARAMETER_REQUIRED,
					ClinicalDocumentValidator.CONTAINS_PERFORMANCE_YEAR));
	}

	@Test
	public void testDuplicateAciSectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node performanceSection = createReportingNode();

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		Node duplicateAciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(aciSectionNode, performanceSection, duplicateAciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("Should contain one error", errors, hasSize(1));
		assertThat("Should contain one error", errors,
			containsValidationErrorInAnyOrderIgnoringPath(ClinicalDocumentValidator.CONTAINS_DUPLICATE_ACI_SECTIONS));
	}

	@Test
	public void testDuplicateIASectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node performanceSection = createReportingNode();

		Node IASectionNode = createIASectionNode(clinicalDocumentNode);

		Node duplicateIASectionNode = createIASectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(IASectionNode, performanceSection, duplicateIASectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("Should contain one error", errors, hasSize(1));
		assertThat("Should contain one error", errors,
			containsValidationErrorInAnyOrderIgnoringPath(ClinicalDocumentValidator.CONTAINS_DUPLICATE_IA_SECTIONS));
	}

	@Test
	public void testDuplicateQualityMeasureSectionCausesError() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node performanceSection = createReportingNode();

		Node qualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);

		Node duplicateQualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(qualityMeasureNode, performanceSection, duplicateQualityMeasureNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("Should contain one error", errors, hasSize(1));
		assertThat("Should contain one error", errors,
			containsValidationErrorInAnyOrderIgnoringPath(ClinicalDocumentValidator.CONTAINS_DUPLICATE_ECQM_SECTIONS));
	}

	@Test
	public void testMultipleNonDuplicatedSectionsIsValid() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node performanceSection = createReportingNode();

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		Node IASectionNode = createIASectionNode(clinicalDocumentNode);

		Node qualityMeasureNode = createQualityMeasureSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNodes(aciSectionNode, performanceSection, IASectionNode, qualityMeasureNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("Should have no validation errors", errors, empty());
	}

	@Test
	public void testClinicalDocumentValidationParsesMultipleErrors() throws IOException {
		//setup
		Path path = Paths.get("src/test/resources/negative/angerClinicalDocumentValidations.xml");

		//execute
		new ConversionFileWriterWrapper(path).transform();
		AllErrors allErrors = readJson(CLINICAL_DOCUMENT_ERROR_FILE, AllErrors.class);
		List<ValidationError> errors = getErrors(allErrors);

		assertThat("Must have 4 errors", errors, hasSize(4));

		assertThat("Must contain the error", errors,
			containsValidationErrorInAnyOrderIgnoringPath(
				ClinicalDocumentValidator.CONTAINS_PROGRAM_NAME,
				ClinicalDocumentValidator.INCORRECT_PROGRAM_NAME,
				ClinicalDocumentValidator.CONTAINS_TAX_ID_NUMBER,
				ClinicalDocumentValidator.CONTAINS_PERFORMANCE_YEAR));
	}

	@Test
	public void testInvalidProgramName() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		Node performanceSection = createReportingNode();
		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);
		clinicalDocumentNode.addChildNodes(aciSectionNode, performanceSection);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME,"Invalid program name");
		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("Should have 1 validation errors", errors, hasSize(1));
		assertThat("Must contain the error", errors,
			containsValidationErrorInAnyOrderIgnoringPath(ClinicalDocumentValidator.INCORRECT_PROGRAM_NAME));
	}


	private List<ValidationError> getErrors(AllErrors content) {
		return content.getErrorSources().get(0).getValidationErrors();
	}

	private Node createValidClinicalDocumentNode() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		return clinicalDocumentNode;
	}

	private Node createReportingNode() {
		Node reportingSection = new Node(TemplateId.REPORTING_PARAMETERS_SECTION);
		Node reportingParametersAct = new Node(TemplateId.REPORTING_PARAMETERS_ACT, reportingSection);
		reportingParametersAct.putValue("performanceStart", "20170101");
		reportingParametersAct.putValue("performanceEnd", "20171231");
		reportingSection.addChildNode(reportingParametersAct);
		return reportingSection;
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