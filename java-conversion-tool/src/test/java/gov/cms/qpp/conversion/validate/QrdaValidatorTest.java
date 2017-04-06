package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class QrdaValidatorTest {

	private QrdaValidator objectUnderTest;
	private static boolean activated = false;
	private static List<Node> nodesPassedIntoValidateSingleNode;

	private static List<Node> nodesPassedIntoRequiredValidateTemplateIdNodes;
	private static List<Node> nodesPassedIntoOptionalValidateTemplateIdNodes;

	private static final String TEST_REQUIRED_TEMPLATE_ID = "testRequiredTemplateId";
	private static final String TEST_OPTIONAL_TEMPLATE_ID = "testOptionalTemplateId";

	private static final ValidationError TEST_VALIDATION_ERROR_FOR_SINGLE_NODE =
		new ValidationError("single node validation error");
	private static final ValidationError TEST_VALIDATION_ERROR_FOR_REQUIRED_TEMPLATE_ID_NODES =
		new ValidationError("list of nodes required validation error");
	private static final ValidationError TEST_VALIDATION_ERROR_FOR_OPTIONAL_TEMPLATE_ID_NODES =
		new ValidationError("list of nodes optional validation error");

	@Before
	public void beforeEachTest() {
		objectUnderTest = new QrdaValidator();
		nodesPassedIntoValidateSingleNode = new ArrayList<>();
		nodesPassedIntoRequiredValidateTemplateIdNodes = null;
		nodesPassedIntoOptionalValidateTemplateIdNodes = null;
		activated = true;
	}

	@After
	public void afterEachTest() {
		activated = false;
	}

	@Test
	public void testValidateSingleNode() {

		//set-up
		Node testRootNode = new Node();
		testRootNode.setId(TEST_REQUIRED_TEMPLATE_ID);
		final String testKey = "testKey";
		final String testValue = "testValue";
		testRootNode.putValue(testKey, testValue);

		//execute
		List<ValidationError> validationErrors = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateSingleNode, 1, TEST_REQUIRED_TEMPLATE_ID, testKey, testValue);
		assertThat("The validation errors is missing items from the expected templateId",
		           validationErrors, hasItems(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE,
		                                      TEST_VALIDATION_ERROR_FOR_REQUIRED_TEMPLATE_ID_NODES));
		assertThat("The validation errors (incorrectly) has an error from the optional templateId",
		           validationErrors, not(hasItem(TEST_VALIDATION_ERROR_FOR_OPTIONAL_TEMPLATE_ID_NODES)));
	}

	@Test
	public void testValidateMultipleNodes() {

		//set-up
		Node testChildNode1 = new Node();
		testChildNode1.setId(TEST_REQUIRED_TEMPLATE_ID);
		final String testKey = "testKey";
		final String testValue = "testValue";
		testChildNode1.putValue(testKey, testValue);

		Node testChildNode2 = new Node();
		testChildNode2.setId(TEST_REQUIRED_TEMPLATE_ID);
		testChildNode2.putValue(testKey, testValue);

		Node testRootNode = new Node();
		testRootNode.setId("anotherTemplateId");
		testRootNode.addChildNode(testChildNode1);
		testRootNode.addChildNode(testChildNode2);

		//execute
		List<ValidationError> validationErrors = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateSingleNode, 2, TEST_REQUIRED_TEMPLATE_ID, testKey, testValue);
		assertNodeList(nodesPassedIntoRequiredValidateTemplateIdNodes, 2, TEST_REQUIRED_TEMPLATE_ID, testKey, testValue);
		assertThat("The validation errors is missing the specific number of single node errors",
		           Collections.frequency(validationErrors, TEST_VALIDATION_ERROR_FOR_SINGLE_NODE), is(2));
		assertThat("The validation errors is missing the specific number of required templateId errors",
		           Collections.frequency(validationErrors, TEST_VALIDATION_ERROR_FOR_REQUIRED_TEMPLATE_ID_NODES), is(1));
		assertThat("The validation errors (incorrectly) has an error from the optional templateId",
		           validationErrors, not(hasItem(TEST_VALIDATION_ERROR_FOR_OPTIONAL_TEMPLATE_ID_NODES)));
	}

	@Test
	public void testNoNodes() {

		//set-up
		Node testRootNode = new Node();
		testRootNode.setId("anotherTemplateId");

		//execute
		List<ValidationError> validationErrors = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateSingleNode, 0, null, null, null);
		assertThat("The list of nodes has an incorrect size", nodesPassedIntoRequiredValidateTemplateIdNodes, hasSize(0));
		assertThat("The validation errors is missing an item from the expected templateId",
		           validationErrors, hasItem(TEST_VALIDATION_ERROR_FOR_REQUIRED_TEMPLATE_ID_NODES));
		assertThat("The validation errors (incorrectly) has a single node error and an error from the  and optional templateId",
		           validationErrors, not(hasItems(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE,
		                                          TEST_VALIDATION_ERROR_FOR_OPTIONAL_TEMPLATE_ID_NODES)));

	}

	@Test
	public void testOptionalValidation() {

		//set-up
		Node testRootNode = new Node();
		testRootNode.setId(TEST_OPTIONAL_TEMPLATE_ID);
		final String testKey = "testKey";
		final String testValue = "testValue";
		testRootNode.putValue(testKey, testValue);

		//execute
		List<ValidationError> validationErrors = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateSingleNode, 0, null, null, null);
		assertThat("The list of nodes has an incorrect size", nodesPassedIntoRequiredValidateTemplateIdNodes, hasSize(0));
		assertThat("The validation errors is missing an item from the expected templateId",
		           validationErrors, hasItem(TEST_VALIDATION_ERROR_FOR_REQUIRED_TEMPLATE_ID_NODES));
		assertThat("The validation errors (incorrectly) has a single node error and an error from the  and optional templateId",
		           validationErrors, not(hasItems(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE,
		                                          TEST_VALIDATION_ERROR_FOR_OPTIONAL_TEMPLATE_ID_NODES)));
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

		assertThat("The node must not be null",
		           node, is(not(nullValue())));
		assertThat("The node's Id is incorrect", node.getId(), is(expectedTemplateId));
		assertThat("The node's key/value is incorrect", node.getValue(keyToQuery), is(expectedValue));
	}

	private void assertValidationError(final ValidationError validationError, final ValidationError expectedValidationError) {

		assertThat("The validation error must not be null", validationError, is(not(nullValue())));
		assertThat("The validation error is incorrect", validationError,
		           is(expectedValidationError));
	}

	@Validator(templateId = TEST_REQUIRED_TEMPLATE_ID, required = true)
	public static class RequiredTestValidator extends NodeValidator {

		@Override
		public void internalValidateSingleNode(final Node node) {
			nodesPassedIntoValidateSingleNode.add(node);
			addValidationError(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
		}

		@Override
		public void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
			nodesPassedIntoRequiredValidateTemplateIdNodes = nodes;
			if ( activated ) {
				addValidationError(TEST_VALIDATION_ERROR_FOR_REQUIRED_TEMPLATE_ID_NODES);
			}
		}
	}

	@Validator(templateId = TEST_OPTIONAL_TEMPLATE_ID, required = false)
	public static class OptionalTestValidator extends NodeValidator {

		@Override
		public void internalValidateSingleNode(final Node node) {
			nodesPassedIntoValidateSingleNode.add(node);
			addValidationError(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
		}

		@Override
		public void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
			nodesPassedIntoOptionalValidateTemplateIdNodes = nodes;
			addValidationError(TEST_VALIDATION_ERROR_FOR_OPTIONAL_TEMPLATE_ID_NODES);
		}
	}
}