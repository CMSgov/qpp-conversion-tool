package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class QrdaValidatorTest {

	private QrdaValidator objectUnderTest;

	private static List<Node> nodesPassedIntoValidateNode;

	private static List<Node> nodesPassedIntoValidateNodes;

	private static final String TEST_TEMPLATE_ID = "testTemplateId";

	private static final String TEST_VALIDATION_ERROR_FOR_SINGLE_NODE = "single node validation error";
	private static final String TEST_VALIDATION_ERROR_FOR_LIST_OF_NODE = "list of nodes validation error";

	@Before
	public void beforeEachTest() {
		objectUnderTest = new QrdaValidator();
		nodesPassedIntoValidateNode = new ArrayList<>();
		nodesPassedIntoValidateNodes = null;
	}

	@Test
	public void testValidateSingleNode() {

		//set-up
		Node testRootNode = new Node();
		testRootNode.setId(TEST_TEMPLATE_ID);
		final String testKey = "testKey";
		final String testValue = "testValue";
		testRootNode.putValue(testKey, testValue);

		//execute
		List<ValidationError> validationErrors = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateNode, 1, TEST_TEMPLATE_ID, testKey, testValue);
		assertThat("The list of validation errors is incorrect", validationErrors, hasSize(2));
		assertValidationError(validationErrors.get(0), TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
		assertValidationError(validationErrors.get(1), TEST_VALIDATION_ERROR_FOR_LIST_OF_NODE);
	}

	@Test
	public void testValidateMultipleNodes() {

		//set-up
		Node testChildNode1 = new Node();
		testChildNode1.setId(TEST_TEMPLATE_ID);
		final String testKey = "testKey";
		final String testValue = "testValue";
		testChildNode1.putValue(testKey, testValue);

		Node testChildNode2 = new Node();
		testChildNode2.setId(TEST_TEMPLATE_ID);
		testChildNode2.putValue(testKey, testValue);

		Node testRootNode = new Node();
		testRootNode.setId("anotherTemplateId");
		testRootNode.addChildNode(testChildNode1);
		testRootNode.addChildNode(testChildNode2);

		//execute
		List<ValidationError> validationErrors = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateNode, 2, TEST_TEMPLATE_ID, testKey, testValue);
		assertNodeList(nodesPassedIntoValidateNodes, 2, TEST_TEMPLATE_ID, testKey, testValue);
		assertThat("The list of validation errors is incorrect", validationErrors, hasSize(3));
		assertValidationError(validationErrors.get(0), TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
		assertValidationError(validationErrors.get(1), TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
		assertValidationError(validationErrors.get(2), TEST_VALIDATION_ERROR_FOR_LIST_OF_NODE);
	}

	@Test
	public void testNoNodes() {

		//set-up
		Node testRootNode = new Node();
		testRootNode.setId("anotherTemplateId");

		//execute
		List<ValidationError> validationErrors = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateNode, 0, null, null, null);
		assertThat("The list of nodes must be null", nodesPassedIntoValidateNodes, is(nullValue()));
	}

	private void assertNodeList(final List<Node> nodeList, final int expectedSize, final String expectedTemplateId,
	                            final String keyToQuery, final String expectedValue) {

		assertThat("The list of nodes must not be null", nodeList, is(not(nullValue())));
		assertThat("The list of nodes must have a size of " + expectedSize, nodeList, hasSize(expectedSize));

		for (Node node : nodeList) {
			assertNode(node, expectedTemplateId, keyToQuery, expectedValue);
		}
	}

	private void assertNode(final Node node, final String expectedTemplateId, final String keyToQuery,
	                        final String expectedValue) {

		assertThat("The node that was passed into the validateNode method must not be null",
		           node, is(not(nullValue())));
		assertThat("The node that was passed into the validateNode method must have an Id of " +
		           expectedTemplateId, node.getId(), is(expectedTemplateId));
		assertThat("The node that was passed into the validateNode method must not be null",
		           node.getValue(keyToQuery), is(expectedValue));
	}

	private void assertValidationError(final ValidationError validationError, final String expectedValidationErrorString) {

		assertThat("The validation error must not be null", validationError, is(not(nullValue())));
		assertThat("The validation error is incorrect", validationError.getErrorText(),
		           is(expectedValidationErrorString));
	}

	@Validator(templateId = TEST_TEMPLATE_ID, required = true)
	public static class TestValidator extends NodeValidator {

		@Override
		public void internalValidateNode(final Node node) {
			nodesPassedIntoValidateNode.add(node);
			addValidationError(new ValidationError(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE));
		}

		@Override
		public void internalValidateNodes(final List<Node> nodes) {
			nodesPassedIntoValidateNodes = nodes;
			addValidationError(new ValidationError(TEST_VALIDATION_ERROR_FOR_LIST_OF_NODE));
		}
	}
}