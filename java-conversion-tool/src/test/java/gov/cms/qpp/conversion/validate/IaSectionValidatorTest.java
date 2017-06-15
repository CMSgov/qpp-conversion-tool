package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class IaSectionValidatorTest {
	private Node iaSectionNode;
	private Node iaMeasureNode;
	private Node reportingParamActNode;

	@Before
	public void setUpIaSectionNode() {
		iaSectionNode = new Node(TemplateId.IA_SECTION);
		iaMeasureNode = new Node(TemplateId.IA_MEASURE);
		reportingParamActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
	}

	@Test
	public void testCorrectIaSectionPassesValidation() {
		iaSectionNode.addChildNodes(iaMeasureNode, reportingParamActNode);

		List<Detail> errors = validatorIaSection();

		assertThat("Must contain no errors", errors, hasSize(0));
	}

	@Test
	public void testValidatesMissingIAMeasure() {
		iaSectionNode.addChildNodes(reportingParamActNode);
		
		List<Detail> errors = validatorIaSection();

		assertThat("Must be missing the correct child", errors.get(0).getMessage(),
				is(IaSectionValidator.MINIMUM_REQUIREMENT_ERROR));
	}

	@Test
	public void testIncorrectChildValidation() {
		Node incorrectAggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		iaSectionNode.addChildNodes(iaMeasureNode, reportingParamActNode, incorrectAggregateCountNode);

		List<Detail> errors = validatorIaSection();

		assertThat("Must contain correct children", errors.get(0).getMessage(),
				is(IaSectionValidator.WRONG_CHILD_ERROR));
	}

	@Test
	public void testMissingReportingParameter() {
		iaSectionNode.addChildNodes(iaMeasureNode);

		List<Detail> errors = validatorIaSection();

		assertThat("Must contain correct children", errors.get(0).getMessage(),
				is(IaSectionValidator.REPORTING_PARAM_REQUIREMENT_ERROR));
	}

	@Test
	public void testTooManyReportingParameters() {
		Node invalidParamActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		iaSectionNode.addChildNodes(iaMeasureNode, reportingParamActNode, invalidParamActNode);

		List<Detail> errors = validatorIaSection();

		assertThat("Must contain correct children", errors.get(0).getMessage(),
				is(IaSectionValidator.REPORTING_PARAM_REQUIREMENT_ERROR));
	}

	private List<Detail> validatorIaSection() {
		IaSectionValidator iaValidator = new IaSectionValidator();

		iaValidator.internalValidateSingleNode(iaSectionNode);
		return iaValidator.getDetails();
	}
}
