package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertWithMessage;

@RunWith(PowerMockRunner.class)
@PrepareForTest(QrdaValidator.class)
@PowerMockIgnore({"org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*" })
public class QrdaValidatorTest {

	private static List<Node> nodesPassedIntoValidateSingleNode;

	private QrdaValidator objectUnderTest;

	private static final TemplateId TEST_REQUIRED_TEMPLATE_ID = TemplateId.ACI_NUMERATOR_DENOMINATOR;
	private static final TemplateId TEST_OPTIONAL_TEMPLATE_ID = TemplateId.PLACEHOLDER;

	private static final Detail TEST_VALIDATION_ERROR_FOR_SINGLE_NODE =
		new Detail("single node validation error");

	@Before
	public void beforeEachTest() throws Exception {
		nodesPassedIntoValidateSingleNode = new ArrayList<>();

		Context context = new Context();
		objectUnderTest = TestHelper.mockValidator(context, RequiredTestValidator.class, new ComponentKey(TEST_REQUIRED_TEMPLATE_ID, Program.ALL), true);
		objectUnderTest = TestHelper.mockValidator(context, OptionalTestValidator.class, new ComponentKey(TEST_OPTIONAL_TEMPLATE_ID, Program.ALL), false, objectUnderTest);
	}

	@Test
	public void testValidateSingleNode() {

		//set-up
		Node testRootNode = new Node(TEST_REQUIRED_TEMPLATE_ID);
		final String testKey = "testKey";
		final String testValue = "testValue";
		testRootNode.putValue(testKey, testValue);

		//execute
		List<Detail> details = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateSingleNode, 1, TEST_REQUIRED_TEMPLATE_ID, testKey, testValue);
		assertWithMessage("The validation errors is missing items from the expected templateId")
				.that(details).contains(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
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
		List<Detail> details = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateSingleNode, 2, TEST_REQUIRED_TEMPLATE_ID, testKey, testValue);
		assertWithMessage("The validation errors is missing the specific number of single node errors")
				.that(details)
				.containsExactly(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE, TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
	}

	@Test
	public void testNoNodes() {
		//set-up
		Node testRootNode = new Node();

		//execute
		List<Detail> details = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateSingleNode, 0, null, null, null);
		assertWithMessage("The validation errors (incorrectly) has a single node error and an error from the  and optional templateId")
				.that(details).doesNotContain(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
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
		List<Detail> details = objectUnderTest.validate(testRootNode);

		//assert
		assertNodeList(nodesPassedIntoValidateSingleNode, 0, null, null, null);
		assertWithMessage("The validation errors (incorrectly) has a single node error and an error from the  and optional templateId")
				.that(details).doesNotContain(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
	}

	private void assertNodeList(List<Node> nodeList, int expectedSize, TemplateId expectedTemplateId,
			String keyToQuery, String expectedValue) {

		assertWithMessage("The list of nodes must not be null")
				.that(nodeList).isNotNull();
		assertWithMessage("The list of nodes must have a size of %s", expectedSize)
				.that(nodeList).hasSize(expectedSize);

		for (Node node : nodeList) {
			assertNode(node, expectedTemplateId, keyToQuery, expectedValue);
		}
	}

	private void assertNode(Node node, TemplateId expectedTemplateId, String keyToQuery,
			String expectedValue) {

		assertWithMessage("The node must not be null")
				.that(node).isNotNull();
		assertWithMessage("The node's Id is incorrect")
				.that(node.getType()).isSameAs(expectedTemplateId);
		assertWithMessage("The node's key/value is incorrect")
				.that(node.getValue(keyToQuery)).isSameAs(expectedValue);
	}

	public static class RequiredTestValidator extends NodeValidator {

		@Override
		public void internalValidateSingleNode(final Node node) {
			nodesPassedIntoValidateSingleNode.add(node);
			addValidationError(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
		}
	}

	public static class OptionalTestValidator extends NodeValidator {

		@Override
		public void internalValidateSingleNode(final Node node) {
			nodesPassedIntoValidateSingleNode.add(node);
			addValidationError(TEST_VALIDATION_ERROR_FOR_SINGLE_NODE);
		}
	}
}