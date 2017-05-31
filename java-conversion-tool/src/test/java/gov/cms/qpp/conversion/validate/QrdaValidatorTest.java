package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.AnnotationMockHelper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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

@RunWith(PowerMockRunner.class)
@PrepareForTest(QrdaValidator.class)
@PowerMockIgnore({"org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*" })
public class QrdaValidatorTest {

	private QrdaValidator objectUnderTest;
	private static boolean activated = false;
	private static List<Node> nodesPassedIntoValidateSingleNode;

	private static List<Node> nodesPassedIntoRequiredValidateTemplateIdNodes;

	private static final TemplateId TEST_REQUIRED_TEMPLATE_ID = TemplateId.ACI_NUMERATOR_DENOMINATOR;
	private static final TemplateId TEST_OPTIONAL_TEMPLATE_ID = TemplateId.CMS_AGGREGATE_COUNT;

	private static final ValidationError TEST_VALIDATION_ERROR_FOR_SINGLE_NODE =
		new ValidationError("single node validation error");
	private static final ValidationError TEST_VALIDATION_ERROR_FOR_REQUIRED_TEMPLATE_ID_NODES =
		new ValidationError("list of nodes required validation error");
	private static final ValidationError TEST_VALIDATION_ERROR_FOR_OPTIONAL_TEMPLATE_ID_NODES =
		new ValidationError("list of nodes optional validation error");

	@Before
	public void beforeEachTest() throws Exception {
		nodesPassedIntoValidateSingleNode = new ArrayList<>();
		nodesPassedIntoRequiredValidateTemplateIdNodes = null;

		objectUnderTest = AnnotationMockHelper.mockValidator(TEST_REQUIRED_TEMPLATE_ID, RequiredTestValidator.class, true);
		objectUnderTest = AnnotationMockHelper.mockValidator(TEST_OPTIONAL_TEMPLATE_ID, OptionalTestValidator.class, false, objectUnderTest);

		activated = true;
	}

	@After
	public void afterEachTest() {
		activated = false;
	}

	@Test
	public void testValidateSingleNode() {

		//set-up
		Node testRootNode = new Node(TEST_REQUIRED_TEMPLATE_ID);
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
		Node testChildNode1 = new Node(TEST_REQUIRED_TEMPLATE_ID);
		final String testKey = "testKey";
		final String testValue = "testValue";
		testChildNode1.putValue(testKey, testValue);

		Node testChildNode2 = new Node(TEST_REQUIRED_TEMPLATE_ID);
		testChildNode2.putValue(testKey, testValue);

		Node testRootNode = new Node();
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
		testRootNode.setType(TEST_OPTIONAL_TEMPLATE_ID);
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

	private void assertNodeList(List<Node> nodeList, int expectedSize, TemplateId expectedTemplateId,
			String keyToQuery, String expectedValue) {

		assertThat("The list of nodes must not be null", nodeList, is(not(nullValue())));
		assertThat("The list of nodes must have a size of " + expectedSize, nodeList, hasSize(expectedSize));

		for (Node node : nodeList) {
			assertNode(node, expectedTemplateId, keyToQuery, expectedValue);
		}
	}

	private void assertNode(Node node, TemplateId expectedTemplateId, String keyToQuery,
			String expectedValue) {

		assertThat("The node must not be null",
		           node, is(not(nullValue())));
		assertThat("The node's Id is incorrect", node.getType(), is(expectedTemplateId));
		assertThat("The node's key/value is incorrect", node.getValue(keyToQuery), is(expectedValue));
	}

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

	public static class OptionalTestValidator extends NodeValidator {

		@Override
		public void internalValidateSingleNode(final Node node) {
			nodesPassedIntoValidateSingleNode.add(node);
			addValidationError(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
		}

		@Override
		public void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
			addValidationError(TEST_VALIDATION_ERROR_FOR_OPTIONAL_TEMPLATE_ID_NODES);
		}
	}
}