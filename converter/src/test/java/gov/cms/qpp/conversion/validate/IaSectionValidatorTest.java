package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

class IaSectionValidatorTest {

	private Node iaSectionNode;
	private Node iaMeasureNode;
	private Node reportingParamActNode;

	@BeforeEach
	void setUpIaSectionNode() {
		iaSectionNode = new Node(TemplateId.IA_SECTION_V3);
		iaMeasureNode = new Node(TemplateId.IA_MEASURE);
		reportingParamActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
	}

	@Test
	void testCorrectIaSectionPassesValidation() {
		iaSectionNode.addChildNodes(iaMeasureNode, reportingParamActNode);

		List<Detail> errors = validatorIaSection();

		assertWithMessage("Must contain no errors")
				.that(errors).isEmpty();
	}

	@Test
	void testValidatesMissingIAMeasure() {
		iaSectionNode.addChildNodes(reportingParamActNode);
		
		List<Detail> errors = validatorIaSection();

		assertWithMessage("Must be missing the correct child")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.IA_SECTION_MISSING_IA_MEASURE);
	}

	@Test
	void testIncorrectChildValidation() {
		Node incorrectAggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		iaSectionNode.addChildNodes(iaMeasureNode, reportingParamActNode, incorrectAggregateCountNode);

		List<Detail> errors = validatorIaSection();

		assertWithMessage("Must contain correct children")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.IA_SECTION_WRONG_CHILD);
	}

	@Test
	void testMissingReportingParameter() {
		iaSectionNode.addChildNodes(iaMeasureNode);

		List<Detail> errors = validatorIaSection();

		assertWithMessage("Must contain correct children")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.IA_SECTION_MISSING_REPORTING_PARAM);
	}

	@Test
	void testTooManyReportingParameters() {
		Node invalidParamActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		iaSectionNode.addChildNodes(iaMeasureNode, reportingParamActNode, invalidParamActNode);

		List<Detail> errors = validatorIaSection();

		assertWithMessage("Must contain correct children")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.IA_SECTION_MISSING_REPORTING_PARAM);
	}

	private List<Detail> validatorIaSection() {
		IaSectionValidator iaValidator = new IaSectionValidator();

		return iaValidator.validateSingleNode(iaSectionNode).getErrors();
	}
}
