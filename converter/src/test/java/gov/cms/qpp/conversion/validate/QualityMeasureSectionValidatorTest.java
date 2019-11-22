package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.model.TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.MarkupManipulationHandler;
import gov.cms.qpp.conversion.decode.QualitySectionDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

class QualityMeasureSectionValidatorTest {

	private static MarkupManipulationHandler manipulatorHandler;
	private Node reportingParameterNode;
	private Node qualityMeasureSectionNode;
	private Node measure;


	@BeforeAll
	static void setup() {
		manipulatorHandler = new MarkupManipulationHandler("../qrda-files/valid-QRDA-III-latest.xml");
	}

	@BeforeEach
	void setUpQualityMeasureSection() {
		reportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		qualityMeasureSectionNode = new Node(TemplateId.MEASURE_SECTION_V3);
		measure = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
	}

	@Test
	void testValidQualityMeasureSectionValidation() {
		qualityMeasureSectionNode.addChildNode(reportingParameterNode);
		qualityMeasureSectionNode.addChildNode(measure);
		qualityMeasureSectionNode.putValue(QualitySectionDecoder.MEASURE_SECTION_V4,
			TemplateId.MEASURE_SECTION_V4.getExtension());

		List<Detail> errors = validateQualityMeasureSection();

		assertWithMessage("Must not contain errors")
				.that(errors).isEmpty();
	}

	@Test
	void testQualityMeasureSectionWithoutMeasure() {
		qualityMeasureSectionNode.addChildNode(reportingParameterNode);
		qualityMeasureSectionNode.putValue(QualitySectionDecoder.MEASURE_SECTION_V4,
			TemplateId.MEASURE_SECTION_V4.getExtension());

		List<Detail> errors = validateQualityMeasureSection();

		assertWithMessage("Must contain 1 error")
				.that(errors).hasSize(1);

		assertWithMessage("Error must be " + ErrorCode.MEASURE_SECTION_MISSING_MEASURE)
			.that(errors.get(0).getErrorCode()).isEqualTo(ErrorCode.MEASURE_SECTION_MISSING_MEASURE.getCode());
	}

	@Test
	void testMissingReportingParams() {
		qualityMeasureSectionNode.putValue(QualitySectionDecoder.MEASURE_SECTION_V4,
			TemplateId.MEASURE_SECTION_V4.getExtension());

		List<Detail> errors = validateQualityMeasureSection();

		assertWithMessage("Must contain correct error")
				.that(errors)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT);
	}

	@Test
	void testTooManyReportingParams() {
		Node secondReportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		qualityMeasureSectionNode.addChildNodes(reportingParameterNode, secondReportingParameterNode);
		qualityMeasureSectionNode.putValue(QualitySectionDecoder.MEASURE_SECTION_V4,
			TemplateId.MEASURE_SECTION_V4.getExtension());

		List<Detail> errors = validateQualityMeasureSection();

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

	@Test
	void testMissingQualityMeasureSectionV4() {
		qualityMeasureSectionNode.addChildNode(reportingParameterNode);
		qualityMeasureSectionNode.addChildNode(measure);

		List<Detail> errors = validateQualityMeasureSection();

		assertThat(errors)
			.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ErrorCode.MEASURE_SECTION_V4_REQUIRED);
	}

	private List<Detail> validateQualityMeasureSection() {
		QualityMeasureSectionValidator validator = new QualityMeasureSectionValidator();
		return validator.validateSingleNode(qualityMeasureSectionNode).getErrors();
	}
}
