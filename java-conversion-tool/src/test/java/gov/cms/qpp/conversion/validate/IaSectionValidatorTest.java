package gov.cms.qpp.conversion.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;

public class IaSectionValidatorTest {

	@Test
	public void testCorrectIaSectionPassesValidation() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION);
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE);
		iaSectionNode.addChildNode(iaMeasureNode);

		IaSectionValidator iaValidator = new IaSectionValidator();

		iaValidator.internalValidateSingleNode(iaSectionNode);
		List<Detail> errors = iaValidator.getDetails();

		assertThat("Must contain no errors", errors, hasSize(0));
	}

	@Test
	public void testValidatesMissingIAMeasure() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION);
		IaSectionValidator iaValidator = new IaSectionValidator();

		iaValidator.internalValidateSingleNode(iaSectionNode);
		List<Detail> errors = iaValidator.getDetails();

		assertThat("Must be missing the correct child", errors.get(0).getMessage(),
				is(IaSectionValidator.MINIMUM_REQUIREMENT_ERROR));
	}

	@Test
	public void testIncorrectChildValidation() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION);
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE);
		iaSectionNode.addChildNode(iaMeasureNode);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		iaSectionNode.addChildNode(aggregateCountNode);

		IaSectionValidator iaValidator = new IaSectionValidator();

		iaValidator.internalValidateSingleNode(iaSectionNode);
		List<Detail> errors = iaValidator.getDetails();

		assertThat("Must contain correct children", errors.get(0).getMessage(),
				is(IaSectionValidator.WRONG_CHILD_ERROR));
	}
}
