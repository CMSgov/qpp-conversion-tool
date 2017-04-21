package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.junit.Assert.assertThat;

public class ClinicalDocumentValidatorTest {

	private static final String EXPECTED_TEXT = "Clinical Document Node is required";
	private static final String EXPECTED_ONE_ALLOWED = "Only one Clinical Document Node is allowed";
	private static final String EXPECTED_NO_SECTION = "Clinical Document Node must have at least one Aci or IA or eCQM Section Node as a child";

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

		Node ecqmSectionNode = new Node(clinicalDocumentNode, TemplateId.MEASURE_SECTION_V2.getTemplateId());
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
		assertThat("error should be about missing Clinical Document node", errors.get(0).getErrorText(),
				is(EXPECTED_TEXT));
	}

	@Test
	public void testTooManyClinicalDocumentNodes() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		Node clinicalDocumentNode2 = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSameTemplateIdNodes(Arrays.asList(clinicalDocumentNode, clinicalDocumentNode2));

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about too many Clinical Document nodes", errors.get(0).getErrorText(),
				is(EXPECTED_ONE_ALLOWED));
	}

	@Test
	public void testNoSections() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		clinicalDocumentNode.addChildNode(createReportingNode());

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing section node", errors.get(0).getErrorText(), is(EXPECTED_NO_SECTION));
	}

	@Test
	public void testNoSectionsOtherChildren() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();
		clinicalDocumentNode.addChildNode(createReportingNode());

		Node placeholderNode = new Node("placeholder");

		clinicalDocumentNode.addChildNode(placeholderNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing section node", errors.get(0).getErrorText(), is(EXPECTED_NO_SECTION));
	}

	@Test
	public void testMissingName() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.addChildNode(createReportingNode());

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing section node", errors.get(0).getErrorText(), is(ClinicalDocumentValidator.CONTAINS_PROGRAM_NAME));
	}

	@Test
	public void testMissingTin() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.addChildNode(createReportingNode());

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing section node", errors.get(0).getErrorText(), is(ClinicalDocumentValidator.CONTAINS_TAX_ID_NUMBER));
	}

	@Test
	public void testNpiIsOptional() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");

		clinicalDocumentNode.addChildNode(createReportingNode());

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be no errors", errors, iterableWithSize(0));
		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testClinicalDocumentMissingPerformanceStartPresent() {
		Node clinicalDocumentNode = createValidClinicalDocumentNode();

		Node aciSectionNode = createAciSectionNode(clinicalDocumentNode);

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(2));
		assertThat("error should be about missing reporting node",
				errors.get(0).getErrorText(), is(ClinicalDocumentValidator.REPORTING_PARAMETER_REQUIRED));
		assertThat("error should be about missing performance start",
				errors.get(1).getErrorText(), is(ClinicalDocumentValidator.CONTAINS_PERFORMANCE_YEAR));
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
		assertThat("Should contain one error", errors.get(0).getErrorText(),
				is(ClinicalDocumentValidator.CONTAINS_DUPLICATE_ACI_SECTIONS));
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
		assertThat("Should contain one error", errors.get(0).getErrorText(),
				is(ClinicalDocumentValidator.CONTAINS_DUPLICATE_IA_SECTIONS));
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
		assertThat("Should contain one error", errors.get(0).getErrorText(), is(ClinicalDocumentValidator.CONTAINS_DUPLICATE_ECQM_SECTIONS));
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

		assertThat("Should have no validation errors", errors, hasSize(0));
	}

	private Node createValidClinicalDocumentNode() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		return clinicalDocumentNode;
	}

	private Node createReportingNode() {
		Node reportingSection = new Node(TemplateId.REPORTING_PARAMETERS_SECTION.getTemplateId());
		Node reportingParametersAct = new Node(reportingSection, TemplateId.REPORTING_PARAMETERS_ACT.getTemplateId());
		reportingParametersAct.putValue("performanceStart", "20170101");
		reportingParametersAct.putValue("performanceEnd", "20171231");
		reportingSection.addChildNode(reportingParametersAct);
		return reportingSection;
	}

	private Node createAciSectionNode(Node clinicalDocumentNode) {
		Node aciSectionNode = new Node(clinicalDocumentNode, TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");
		return aciSectionNode;
	}

	private Node createIASectionNode(Node clinicalDocumentNode) {
		Node IASectionNode = new Node(clinicalDocumentNode, TemplateId.IA_SECTION.getTemplateId());
		IASectionNode.putValue("category", "ia");
		return IASectionNode;
	}

	private Node createQualityMeasureSectionNode(Node clinicalDocumentNode) {
		Node qualityMeasureNode = new Node(clinicalDocumentNode, TemplateId.MEASURE_SECTION_V2.getTemplateId());
		qualityMeasureNode.putValue("category", "ecqm");
		return qualityMeasureNode;
	}
}