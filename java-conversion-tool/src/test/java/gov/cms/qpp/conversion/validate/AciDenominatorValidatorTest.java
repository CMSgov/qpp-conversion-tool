package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Class to test the AciDenominatorValidatorTest
 */
public class AciDenominatorValidatorTest {
	@Test
	public void internalValidateSingleNode() throws Exception {
		Node aciDenominatorNode = new Node(NodeType.ACI_DENOMINATOR.getTemplateId());
		Node aggregateCountNode = new Node(NodeType.ACI_AGGREGATE_COUNT.getTemplateId());
		aggregateCountNode.putValue("aggregateCount", "100");
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciDenominatorNode);
		errors.addAll(validator.validateSameTemplateIdNodes(Arrays.asList(aciDenominatorNode)));

		assertThat("no errors should be present", errors, empty());

	}

	@Test
	public void noChildrenTest() throws Exception {
		Node aciDenominatorNode = new Node(NodeType.ACI_DENOMINATOR.getTemplateId());

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("No Children Validation Error not issued",
				errors.get(0).getErrorText(), is(String.format(AciDenominatorValidator.NO_CHILDREN, aciDenominatorNode)));

	}

	@Test
	public void missingXML() throws Exception {
		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<ValidationError> errors = validator.validateSingleNode(null);

		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Missing XML Validation Error not issued", errors.get(0).getErrorText(),
				is(AciDenominatorValidator.EMPTY_MISSING_XML));

	}

	@Test
	public void incorrectChildrenTest() throws Exception {
		Node aciDenominatorNode = new Node(NodeType.ACI_DENOMINATOR.getTemplateId());
		Node aggregateCountNode = new Node(NodeType.ACI_SECTION.getTemplateId());
		aggregateCountNode.putValue("aggregateCount", "100");

		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Incorrect child Validation Error not issued", errors.get(0).getErrorText(),
				is(String.format(AciDenominatorValidator.INCORRECT_CHILD, aciDenominatorNode)));

	}

	@Test
	public void tooManyChildrenTest() throws Exception {
		Node aciDenominatorNode = new Node(NodeType.ACI_DENOMINATOR.getTemplateId());
		Node aggregateCountNode1 = new Node(NodeType.ACI_AGGREGATE_COUNT.getTemplateId());
		Node aggregateCountNode2 = new Node(NodeType.ACI_AGGREGATE_COUNT.getTemplateId());

		aggregateCountNode1.putValue("aggregateCount", "100");
		aggregateCountNode2.putValue("aggregateCount", "200");

		aciDenominatorNode.addChildNode(aggregateCountNode1);
		aciDenominatorNode.addChildNode(aggregateCountNode2);

		AciDenominatorValidator validator = new AciDenominatorValidator();

		List<ValidationError> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Too many children Validation Error not issued", errors.get(0).getErrorText(),
				is(String.format(AciDenominatorValidator.TOO_MANY_CHILDREN, aciDenominatorNode)));
	}

	@Test
	public void invalidValueNaNTest() throws Exception {
		//Not a number check
		Node aciDenominatorNode = new Node(NodeType.ACI_DENOMINATOR.getTemplateId());
		Node aggregateCountNode = new Node(NodeType.ACI_AGGREGATE_COUNT.getTemplateId());
		String value = "not a number";
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Invalid Value Validation Error not issued", errors.get(0).getErrorText(),
				is(String.format(AciDenominatorValidator.INVALID_VALUE, value, aciDenominatorNode)));

	}

	@Test
	public void invalidValueNegativeNumberTest() throws Exception {
		//Not a number check
		Node aciDenominatorNode = new Node(NodeType.ACI_DENOMINATOR.getTemplateId());
		Node aggregateCountNode = new Node(NodeType.ACI_AGGREGATE_COUNT.getTemplateId());
		String value = "-500";
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Invalid Value Validation Error not issued", errors.get(0).getErrorText(),
				is(String.format(AciDenominatorValidator.INVALID_VALUE, value, aciDenominatorNode)));


	}
	@Test
	public void invalidValueDenominatorNumberTest() throws Exception {
		//Not a number check
		Node aciDenominatorNode = new Node(NodeType.ACI_DENOMINATOR.getTemplateId());
		Node aggregateCountNode = new Node(NodeType.ACI_AGGREGATE_COUNT.getTemplateId());
		String value = "0";
		aggregateCountNode.putValue("aggregateCount", value);
		aciDenominatorNode.addChildNode(aggregateCountNode);

		AciDenominatorValidator validator = new AciDenominatorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciDenominatorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Invalid Value Validation Error not issued", errors.get(0).getErrorText(),
				is(String.format(AciNumeratorDenominatorValidator.DENOMINATOR_CANNOT_BE_ZERO, value, aciDenominatorNode)));


	}
}