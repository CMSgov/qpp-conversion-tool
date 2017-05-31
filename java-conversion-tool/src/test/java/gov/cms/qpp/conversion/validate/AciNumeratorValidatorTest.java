package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Class to test the AciNumeratorValidator
 */
public class AciNumeratorValidatorTest {
	
	@Test
	public void internalValidateSingleNodeWithGreaterThanZeroValue() throws Exception {
		validateNumeratorWithValue("100");
	}

	@Test
	public void internalValidateSingleNodeWithZeroValue() throws Exception {
		validateNumeratorWithValue("0");
	}

	private void validateNumeratorWithValue(String value) {
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCountNode.putValue("aggregateCount", value);
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		errors.addAll(validator.validateSameTemplateIdNodes(Arrays.asList(aciNumeratorNode)));

		assertThat("no errors should be present", errors, empty());
	}


	@Test
	public void noChildrenTest() throws Exception {
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("No Children Validation Error not issued",
				errors.get(0).getErrorText(), is(String.format(AciNumeratorValidator.NO_CHILDREN,
					AciNumeratorValidator.NUMERATOR_NAME)));

	}

	@Test
	public void incorrectChildrenTest() throws Exception {
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_SECTION);
		aggregateCountNode.putValue("aggregateCount", "100");

		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Incorrect child Validation Error not issued", errors.get(0).getErrorText(),
				is(String.format(AciNumeratorValidator.INCORRECT_CHILD, AciNumeratorValidator.NUMERATOR_NAME)));

	}

	@Test
	public void tooManyChildrenTest() throws Exception {
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		Node aggregateCountNode1 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		Node aggregateCountNode2 = new Node(TemplateId.ACI_AGGREGATE_COUNT);

		aggregateCountNode1.putValue("aggregateCount", "100");
		aggregateCountNode2.putValue("aggregateCount", "200");

		aciNumeratorNode.addChildNode(aggregateCountNode1);
		aciNumeratorNode.addChildNode(aggregateCountNode2);

		AciNumeratorValidator validator = new AciNumeratorValidator();

		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Too many children Validation Error not issued", errors.get(0).getErrorText(),
				is(String.format(AciNumeratorValidator.TOO_MANY_CHILDREN, AciNumeratorValidator.NUMERATOR_NAME)));
	}

	@Test
	public void invalidValueNaNTest() throws Exception {
		//Not a number check
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		String value = "not a number";
		aggregateCountNode.putValue("aggregateCount", value);
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		assertThat("Validation error size should be 1 because this will be caught by the aggregate count validator.",
			errors.size(), is(1));
	}

	@Test
	public void invalidValueNegativeNumberTest() throws Exception {
		//Not a number check
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		String value = "-500";
		aggregateCountNode.putValue("aggregateCount", value);
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Invalid Value Validation Error not issued", errors.get(0).getErrorText(),
				is(String.format(AciNumeratorValidator.INVALID_VALUE, AciNumeratorValidator.NUMERATOR_NAME)));


	}}