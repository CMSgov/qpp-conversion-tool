package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

/**
 * Class to test the AciDenominatorValidatorTest
 */
class AciDenominatorValidatorTest {

	@Test
	void internalValidateSingleNodeWithGreaterThanZeroValue() {
		validateDenominatorWithValue("100");
	}

	@Test
	void internalValidateSingleNodeWithZeroValue() {
		validateDenominatorWithValue("0");
	}

	private void validateDenominatorWithValue(String value) {
		Node aciDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);
		Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<Detail> errors = validator.validateSingleNode(aciDenominatorNode).getErrors();

		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void noChildrenTest() {
		Node aciDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<Detail> errors = validator.validateSingleNode(aciDenominatorNode).getErrors();

		assertWithMessage("No Children Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.NUMERATOR_DENOMINATOR_CHILD_EXACT
					.format(AciDenominatorValidator.DENOMINATOR_NAME, AciDenominatorValidator.DENOMINATOR_NAME));
	}

	@Test
	void incorrectChildrenTest() {
		Node aciDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);
		Node aggregateCountNode = new Node(TemplateId.PI_SECTION_V2);
		aggregateCountNode.putValue("aggregateCount", "100");

		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<Detail> errors = validator.validateSingleNode(aciDenominatorNode).getErrors();

		assertWithMessage("Incorrect child Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.NUMERATOR_DENOMINATOR_CHILD_EXACT.format(
						AciDenominatorValidator.DENOMINATOR_NAME, AciDenominatorValidator.DENOMINATOR_NAME));

	}

	@Test
	void tooManyChildrenTest() {
		Node aciDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);
		Node aggregateCountNode1 = new Node(TemplateId.PI_AGGREGATE_COUNT);
		Node aggregateCountNode2 = new Node(TemplateId.PI_AGGREGATE_COUNT);

		aggregateCountNode1.putValue("aggregateCount", "100");
		aggregateCountNode2.putValue("aggregateCount", "200");

		aciDenominatorNode.addChildNode(aggregateCountNode1);
		aciDenominatorNode.addChildNode(aggregateCountNode2);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<Detail> errors = validator.validateSingleNode(aciDenominatorNode).getErrors();

		assertWithMessage("Too many children Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.NUMERATOR_DENOMINATOR_CHILD_EXACT.format(
						AciDenominatorValidator.DENOMINATOR_NAME, AciDenominatorValidator.DENOMINATOR_NAME));
	}

	@Test
	void invalidValueNaNTest() {
		//Not a number check
		Node aciDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);
		Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		String value = "not a number";
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<Detail> errors = validator.validateSingleNode(aciDenominatorNode).getErrors();
		assertWithMessage("Validation error size should be 1 because this will be caught by the aggregate count validator.")
				.that(errors).hasSize(1);
	}

	@Test
	void invalidValueNegativeNumberTest() {
		//Not a number check
		Node aciDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);
		Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		String value = "-500";
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<Detail> errors = validator.validateSingleNode(aciDenominatorNode).getErrors();

		assertWithMessage("Invalid Value Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.NUMERATOR_DENOMINATOR_INVALID_VALUE.format(
						AciDenominatorValidator.DENOMINATOR_NAME, value));
	}

	@Test
	void invalidValueDenominatorNumberTest() {
		//Not a number check
		Node aciDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);
		Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		String value = "-1";
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<Detail> errors = validator.validateSingleNode(aciDenominatorNode).getErrors();

		assertWithMessage("Invalid Value Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.NUMERATOR_DENOMINATOR_INVALID_VALUE.format(
						AciDenominatorValidator.DENOMINATOR_NAME, value));
	}
}