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
 * Class to test the AciNumeratorValidator
 */
class AciNumeratorValidatorTest {
	
	@Test
	void internalValidateSingleNodeWithGreaterThanZeroValue() throws Exception {
		validateNumeratorWithValue("100");
	}

	@Test
	void internalValidateSingleNodeWithZeroValue() {
		validateNumeratorWithValue("0");
	}

	private void validateNumeratorWithValue(String value) {
		Node aciNumeratorNode = new Node(TemplateId.PI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		aggregateCountNode.putValue("aggregateCount", value);
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<Detail> errors = validator.validateSingleNode(aciNumeratorNode).getErrors();

		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void noChildrenTest() {
		Node aciNumeratorNode = new Node(TemplateId.PI_NUMERATOR);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<Detail> errors = validator.validateSingleNode(aciNumeratorNode).getErrors();

		assertWithMessage("Should result in Children Validation Error")
				.that(errors)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.NUMERATOR_DENOMINATOR_CHILD_EXACT
					.format(AciNumeratorValidator.NUMERATOR_NAME, AciNumeratorValidator.NUMERATOR_NAME));
	}

	@Test
	void incorrectChildrenTest() {
		Node aciNumeratorNode = new Node(TemplateId.PI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.PI_SECTION);
		aggregateCountNode.putValue("aggregateCount", "100");

		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<Detail> errors = validator.validateSingleNode(aciNumeratorNode).getErrors();

		assertWithMessage("Incorrect child Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.NUMERATOR_DENOMINATOR_CHILD_EXACT.format(
						AciNumeratorValidator.NUMERATOR_NAME));
	}

	@Test
	void tooManyChildrenTest() {
		Node aciNumeratorNode = new Node(TemplateId.PI_NUMERATOR);
		Node aggregateCountNode1 = new Node(TemplateId.PI_AGGREGATE_COUNT);
		Node aggregateCountNode2 = new Node(TemplateId.PI_AGGREGATE_COUNT);

		aggregateCountNode1.putValue("aggregateCount", "100");
		aggregateCountNode2.putValue("aggregateCount", "200");

		aciNumeratorNode.addChildNode(aggregateCountNode1);
		aciNumeratorNode.addChildNode(aggregateCountNode2);

		AciNumeratorValidator validator = new AciNumeratorValidator();

		List<Detail> errors = validator.validateSingleNode(aciNumeratorNode).getErrors();

		assertWithMessage("Too many children Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.NUMERATOR_DENOMINATOR_CHILD_EXACT.format(
						AciNumeratorValidator.NUMERATOR_NAME, AciNumeratorValidator.NUMERATOR_NAME));
	}

	@Test
	void invalidValueNaNTest() {
		//Not a number check
		Node aciNumeratorNode = new Node(TemplateId.PI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		String value = "not a number";
		aggregateCountNode.putValue("aggregateCount", value);
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<Detail> errors = validator.validateSingleNode(aciNumeratorNode).getErrors();

		assertWithMessage("Validation error size should be 1 because this will be caught by the aggregate count validator.")
				.that(errors).hasSize(1);
	}

	@Test
	void invalidValueNegativeNumberTest() {
		//Not a number check
		Node aciNumeratorNode = new Node(TemplateId.PI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		String value = "-500";
		aggregateCountNode.putValue("aggregateCount", value);
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<Detail> errors = validator.validateSingleNode(aciNumeratorNode).getErrors();

		assertWithMessage("Invalid Value Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.NUMERATOR_DENOMINATOR_INVALID_VALUE.format(
						AciNumeratorValidator.NUMERATOR_NAME, value));
	}

}