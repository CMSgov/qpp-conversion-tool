package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class QualityMeasureSectionValidatorTest {
	private Node reportingParameterNode;
	private Node qualityMeasureSectionNode;

	@Before
	public void setUpQualityMeasureSection() {
		reportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		qualityMeasureSectionNode = new Node(TemplateId.MEASURE_SECTION_V2);
	}

	@Test
	public void validQualityMeasureSectionValidation() {
		qualityMeasureSectionNode.addChildNode(reportingParameterNode);

		List<Detail> errors = validateQualityMeasureSection();

		assertThat("Must not contain errors", errors, hasSize(0));
	}

	@Test
	public void testMissingReportingParams() {
		List<Detail> errors = validateQualityMeasureSection();

		assertThat("Must contain correct error", errors.get(0).getMessage(),
				is(QualityMeasureSectionValidator.REQUIRED_REPORTING_PARAM_REQUIREMENT_ERROR));
	}

	@Test
	public void testTooManyReportingParams() {
		Node secondReportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		qualityMeasureSectionNode.addChildNodes(reportingParameterNode, secondReportingParameterNode);

		List<Detail> errors = validateQualityMeasureSection();

		assertThat("Must contain correct error", errors.get(0).getMessage(),
				is(QualityMeasureSectionValidator.REQUIRED_REPORTING_PARAM_REQUIREMENT_ERROR));
	}

	private List<Detail> validateQualityMeasureSection() {
		QualityMeasureSectionValidator validator = new QualityMeasureSectionValidator();
		validator.internalValidateSingleNode(qualityMeasureSectionNode);
		return validator.getDetails();
	}
}
