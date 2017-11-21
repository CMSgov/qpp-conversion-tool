package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertWithMessage;

/**
 * Class to test the AciNumeratorValidator
 */
class AciNumeratorValidatorTest {
	
	@Test
	void internalValidateSingleNodeWithGreaterThanZeroValue() throws Exception {
		validateNumeratorWithValue("100");
	}

	@Test
	void internalValidateSingleNodeWithZeroValue() throws Exception {
		validateNumeratorWithValue("0");
	}

	private void validateNumeratorWithValue(String value) {
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCountNode.putValue("aggregateCount", value);
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciNumeratorNode);

		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void noChildrenTest() throws Exception {
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciNumeratorNode);

		assertWithMessage("Should result in Children Validation Error")
				.that(errors)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.NUMERATOR_DENOMINATOR_MISSING_CHILDREN.format(AciNumeratorValidator.NUMERATOR_NAME));
	}

	@Test
	void incorrectChildrenTest() throws Exception {
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_SECTION);
		aggregateCountNode.putValue("aggregateCount", "100");

		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciNumeratorNode);

		assertWithMessage("Incorrect child Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.NUMERATOR_DENOMINATOR_INCORRECT_CHILD.format(
						AciNumeratorValidator.NUMERATOR_NAME));
	}

	@Test
	void tooManyChildrenTest() throws Exception {
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		Node aggregateCountNode1 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		Node aggregateCountNode2 = new Node(TemplateId.ACI_AGGREGATE_COUNT);

		aggregateCountNode1.putValue("aggregateCount", "100");
		aggregateCountNode2.putValue("aggregateCount", "200");

		aciNumeratorNode.addChildNode(aggregateCountNode1);
		aciNumeratorNode.addChildNode(aggregateCountNode2);

		AciNumeratorValidator validator = new AciNumeratorValidator();

		Set<Detail> errors = validator.validateSingleNode(aciNumeratorNode);

		assertWithMessage("Too many children Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.NUMERATOR_DENOMINATOR_TOO_MANY_CHILDREN.format(
						AciNumeratorValidator.NUMERATOR_NAME));
	}

	@Test
	void invalidValueNaNTest() throws Exception {
		//Not a number check
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		String value = "not a number";
		aggregateCountNode.putValue("aggregateCount", value);
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciNumeratorNode);

		assertWithMessage("Validation error size should be 1 because this will be caught by the aggregate count validator.")
				.that(errors).hasSize(1);
	}

	@Test
	void invalidValueNegativeNumberTest() throws Exception {
		//Not a number check
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		String value = "-500";
		aggregateCountNode.putValue("aggregateCount", value);
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciNumeratorNode);

		assertWithMessage("Invalid Value Validation Error not issued")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.NUMERATOR_DENOMINATOR_INVALID_VALUE.format(
						AciNumeratorValidator.NUMERATOR_NAME));
	}}