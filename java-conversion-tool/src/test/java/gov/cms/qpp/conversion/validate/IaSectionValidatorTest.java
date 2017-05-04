package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class IaSectionValidatorTest {

	@Test
	public void testCorrectIaSectionPassesValidation() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION.getTemplateId());
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());
		iaSectionNode.addChildNode(iaMeasureNode);

		IaSectionValidator iaValidator = new IaSectionValidator();

		iaValidator.internalValidateSingleNode(iaSectionNode);
		List<ValidationError> errors = iaValidator.getValidationErrors();

		assertThat("Must contain no errors", errors, hasSize(0));
	}

	@Test
	public void testValidatesMissingIAMeasure() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION.getTemplateId());
		IaSectionValidator iaValidator = new IaSectionValidator();

		iaValidator.internalValidateSingleNode(iaSectionNode);
		List<ValidationError> errors = iaValidator.getValidationErrors();

		assertThat("Must be missing the correct child", errors.get(0).getErrorText(),
				is(IaSectionValidator.MINIMIUM_REQUIREMENT_ERROR));
	}

	@Test
	public void testIncorrectChildValidation() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION.getTemplateId());
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		iaSectionNode.addChildNode(aggregateCountNode);

		IaSectionValidator iaValidator = new IaSectionValidator();

		iaValidator.internalValidateSingleNode(iaSectionNode);
		List<ValidationError> errors = iaValidator.getValidationErrors();

		assertThat("Must be missing the correct child", errors.get(0).getErrorText(),
				is(IaSectionValidator.MINIMIUM_REQUIREMENT_ERROR));
	}
}
