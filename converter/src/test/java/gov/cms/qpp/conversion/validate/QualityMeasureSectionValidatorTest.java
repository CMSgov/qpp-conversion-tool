package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.MarkupManipulationHandler;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.model.TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2;

class QualityMeasureSectionValidatorTest {

	private static MarkupManipulationHandler manipulatorHandler;
	private Node reportingParameterNode;
	private Node qualityMeasureSectionNode;


	@BeforeAll
	static void setup() {
		manipulatorHandler = new MarkupManipulationHandler("../qrda-files/valid-QRDA-III-latest.xml");
	}

	@BeforeEach
	void setUpQualityMeasureSection() {
		reportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		qualityMeasureSectionNode = new Node(TemplateId.MEASURE_SECTION_V2);
	}

	@Test
	void validQualityMeasureSectionValidation() {
		qualityMeasureSectionNode.addChildNode(reportingParameterNode);

		Set<Detail> errors = validateQualityMeasureSection();

		assertWithMessage("Must not contain errors")
				.that(errors).isEmpty();
	}

	@Test
	void testMissingReportingParams() {
		Set<Detail> errors = validateQualityMeasureSection();

		assertWithMessage("Must contain correct error")
				.that(errors)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT);
	}

	@Test
	void testTooManyReportingParams() {
		Node secondReportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		qualityMeasureSectionNode.addChildNodes(reportingParameterNode, secondReportingParameterNode);

		Set<Detail> errors = validateQualityMeasureSection();

		assertWithMessage("Must contain correct error")
				.that(errors)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT);
	}

	@Test
	void duplicateEcqMeasure() {
		List<Detail> errorDetails = manipulatorHandler
				.executeScenario(MEASURE_REFERENCE_RESULTS_CMS_V2.name(), "measureId", false);
		assertThat(errorDetails)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ErrorCode.MISSING_OR_DUPLICATED_MEASURE_GUID);
	}

	@Test
	void duplicateEcqMeasureLevelUp() {
		String xpath = manipulatorHandler.getCannedPath(MarkupManipulationHandler.CannedPath.ECQM_PARENT);
		List<Detail> errorDetails = manipulatorHandler.executeScenario(xpath, false);
		assertThat(errorDetails)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ErrorCode.MEASURES_RNR_WITH_DUPLICATED_MEASURE_GUID);
	}

	private Set<Detail> validateQualityMeasureSection() {
		QualityMeasureSectionValidator validator = new QualityMeasureSectionValidator();
		validator.internalValidateSingleNode(qualityMeasureSectionNode);
		return validator.getDetails();
	}
}
