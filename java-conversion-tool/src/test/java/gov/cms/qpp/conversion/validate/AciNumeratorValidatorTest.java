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
 * Class to test the AciNumeratorValidator
 */
public class AciNumeratorValidatorTest {
	@Test
	public void internalValidateSingleNode() throws Exception {
		Node aciNumeratorNode = new Node(NodeType.ACI_NUMERATOR.getTemplateId());
		Node aggregateCountNode = new Node(NodeType.ACI_AGGREGATE_COUNT.getTemplateId());
		aggregateCountNode.putValue("value", "100");
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		errors.addAll(validator.validateSameTemplateIdNodes(Arrays.asList(aciNumeratorNode)));

		assertThat("no errors should be present", errors, empty());

	}

	@Test
	public void internalValidateSameTemplateIdNodes() throws Exception {
	}

	@Test
	public void noChildrenTest() throws Exception {
		Node aciNumeratorNode = new Node(NodeType.ACI_NUMERATOR.getTemplateId());

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("No Children Validation Error not issued", errors.get(0).getErrorText(), is(AciNumeratorValidator.NO_CHILDREN));

	}

	@Test
	public void incorrectChildrenTest() throws Exception {
		Node aciNumeratorNode = new Node(NodeType.ACI_NUMERATOR.getTemplateId());
		Node aggregateCountNode = new Node(NodeType.ACI_SECTION.getTemplateId());
		aggregateCountNode.putValue("value", "100");

		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Incorrect child Validation Error not issued", errors.get(0).getErrorText(), is(AciNumeratorValidator.INCORRECT_CHILD));

	}

	@Test
	public void tooManyChildrenTest() throws Exception {
		Node aciNumeratorNode = new Node(NodeType.ACI_NUMERATOR.getTemplateId());
		Node aggregateCountNode1 = new Node(NodeType.ACI_AGGREGATE_COUNT.getTemplateId());
		Node aggregateCountNode2 = new Node(NodeType.ACI_AGGREGATE_COUNT.getTemplateId());

		aggregateCountNode1.putValue("value", "100");
		aggregateCountNode2.putValue("value", "200");

		aciNumeratorNode.addChildNode(aggregateCountNode1);
		aciNumeratorNode.addChildNode(aggregateCountNode2);

		AciNumeratorValidator validator = new AciNumeratorValidator();

		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("Too many children Validation Error not issued", errors.get(0).getErrorText(),
				is(AciNumeratorValidator.TOO_MANY_CHILDREN));

	}

	@Test
	public void invalidValueTest() throws Exception {
		Node aciNumeratorNode = new Node(NodeType.ACI_NUMERATOR.getTemplateId());
		Node aggregateCountNode = new Node(NodeType.ACI_AGGREGATE_COUNT.getTemplateId());
		String value = "not a number";
		aggregateCountNode.putValue("value", value);
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		assertThat("Validation error size should be 1", errors.size(), is(1));
		assertThat("NO CHILDREN Validation Error not issued", errors.get(0).getErrorText(),
				is(AciNumeratorValidator.INVALID_VALUE + value));
	}

}