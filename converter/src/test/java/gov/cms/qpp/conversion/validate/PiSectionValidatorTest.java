package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

class PiSectionValidatorTest {

	private static final String VALID_ACI_MEASURE = "ACI_EP_1";
	private Node reportingParamNode;
	private Node piNumeratorDenominatorNode;
	private Node measureNode;
	private Node piSectionNode;

	@BeforeEach
	void setUpAciSectionNode() {
		reportingParamNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		piNumeratorDenominatorNode = new Node(TemplateId.PI_NUMERATOR_DENOMINATOR);
		measureNode = new Node(TemplateId.MEASURE_PERFORMED);
		measureNode.putValue("measureId", VALID_ACI_MEASURE);

		piSectionNode = new Node(TemplateId.PI_SECTION_V2);
		piSectionNode.putValue("category", "pi");
	}

	@Test
	void testNoReportingParamPresent() {
		piSectionNode.addChildNodes(piNumeratorDenominatorNode, measureNode);

		PiSectionValidator piSectionValidator = new PiSectionValidator();

		List<Detail> errors = piSectionValidator.validateSingleNode(piSectionNode).getErrors();

		assertWithMessage("error should be about missing proportion node")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.PI_SECTION_MISSING_REPORTING_PARAMETER_ACT);
	}

	@Test
	void testTooManyReportingParams() {
		Node invalidReportingParamNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		piSectionNode.addChildNodes(reportingParamNode, invalidReportingParamNode, piNumeratorDenominatorNode, measureNode);

		PiSectionValidator piSectionValidator = new PiSectionValidator();

		List<Detail> errors = piSectionValidator.validateSingleNode(piSectionNode).getErrors();

		assertWithMessage("error should be about missing required Measure")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.PI_SECTION_MISSING_REPORTING_PARAMETER_ACT);
	}

	@Test
	void testInvalidPiMeasureCombination() {
		Node hie5NumeratorDenominatorNode = new Node(TemplateId.PI_NUMERATOR_DENOMINATOR);
		hie5NumeratorDenominatorNode.putValue("measureId", "PI_HIE_5");
		Node hie1NumeratorDenominatorNode = new Node(TemplateId.PI_NUMERATOR_DENOMINATOR);
		hie1NumeratorDenominatorNode.putValue("measureId", "PI_HIE_1");

		piSectionNode.addChildNodes(hie5NumeratorDenominatorNode, hie1NumeratorDenominatorNode);

		PiSectionValidator piSectionValidator = new PiSectionValidator();
		List<Detail> errors = piSectionValidator.validateSingleNode(piSectionNode).getErrors();

		assertThat(errors)
			.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ProblemCode.PI_RESTRICTED_MEASURES);
	}
}