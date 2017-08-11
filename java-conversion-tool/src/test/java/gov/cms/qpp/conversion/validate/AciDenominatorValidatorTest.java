package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Class to test the AciDenominatorValidatorTest
 */
public class AciDenominatorValidatorTest {

	@Test
	public void internalValidateSingleNodeWithGreaterThanZeroValue() throws Exception {
		validateDenominatorWithValue("100");
	}

	@Test
	public void internalValidateSingleNodeWithZeroValue() throws Exception {
		validateDenominatorWithValue("0");
	}

	private void validateDenominatorWithValue(String value) {
		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciDenominatorNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void noChildrenTest() throws Exception {
		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("No Children Validation Error not issued",
				errors.iterator().next().getMessage(), is(String.format(AciDenominatorValidator.NO_CHILDREN,
					AciDenominatorValidator.DENOMINATOR_NAME)));

	}

	@Test
	public void incorrectChildrenTest() throws Exception {
		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_SECTION);
		aggregateCountNode.putValue("aggregateCount", "100");

		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Incorrect child Validation Error not issued", errors.iterator().next().getMessage(),
				is(String.format(AciDenominatorValidator.INCORRECT_CHILD, AciDenominatorValidator.DENOMINATOR_NAME)));

	}

	@Test
	public void tooManyChildrenTest() throws Exception {
		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		Node aggregateCountNode1 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		Node aggregateCountNode2 = new Node(TemplateId.ACI_AGGREGATE_COUNT);

		aggregateCountNode1.putValue("aggregateCount", "100");
		aggregateCountNode2.putValue("aggregateCount", "200");

		aciDenominatorNode.addChildNode(aggregateCountNode1);
		aciDenominatorNode.addChildNode(aggregateCountNode2);

		AciDenominatorValidator validator = new AciDenominatorValidator();

		Set<Detail> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Too many children Validation Error not issued", errors.iterator().next().getMessage(),
				is(String.format(AciDenominatorValidator.TOO_MANY_CHILDREN, AciDenominatorValidator.DENOMINATOR_NAME)));
	}

	@Test
	public void invalidValueNaNTest() throws Exception {
		//Not a number check
		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		String value = "not a number";
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1 because this will be caught by the aggregate count validator.",
			errors.size(), is(1));
	}

	@Test
	public void invalidValueNegativeNumberTest() throws Exception {
		//Not a number check
		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		String value = "-500";
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Invalid Value Validation Error not issued", errors.iterator().next().getMessage(),
				is(String.format(AciDenominatorValidator.INVALID_VALUE, AciDenominatorValidator.DENOMINATOR_NAME)));


	}
	@Test
	public void invalidValueDenominatorNumberTest() throws Exception {
		//Not a number check
		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		String value = "-1";
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		Set<Detail> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Invalid Value Validation Error not issued", errors.iterator().next().getMessage(),
				is(String.format("This %s Node Aggregate Value has an invalid value", AciDenominatorValidator.DENOMINATOR_NAME)));
	}
}