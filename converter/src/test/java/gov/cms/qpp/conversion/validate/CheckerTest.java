package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsMessageEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.common.truth.Truth.assertWithMessage;

/**
 * This will test the Checker functionality
 */
public class CheckerTest {

	private static final String VALUE = "value";
	private static final String ERROR_MESSAGE = "error message";
	private static final String OTHER_ERROR_MESSAGE = "some other error message";

	private Set<Detail> details;

	@Before
	public void beforeEach() {
		details = new LinkedHashSet<>();
	}

	@Test
	public void testValueFindFailure() {
		Node meepNode = new Node();

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE);

		assertWithMessage("There's an error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testParentFailure() {
		Node meepNode = new Node();

		Checker checker = Checker.check(meepNode, details);
		checker.hasParent(ERROR_MESSAGE, TemplateId.ACI_DENOMINATOR) //fails
				.hasParent(ERROR_MESSAGE, TemplateId.ACI_DENOMINATOR); //shortcuts

		assertWithMessage("message applied is the message given")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testValueFindSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "Bob");

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE);

		assertWithMessage("There's no error")
				.that(details).isEmpty();
	}

	@Test
	public void testIntValueFindFailure() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "Bob");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE);

		assertWithMessage("There's an error")
				.that(details).hasSize(1);
	}

	@Test
	public void testIntValueFindSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE);

		assertWithMessage("There's no error")
				.that(details).isEmpty();
	}

	@Test
	public void testChildrenFindFailure() {
		Node meepNode = new Node();

		Checker checker = Checker.check(meepNode, details);
		checker.hasChildren(ERROR_MESSAGE);

		assertWithMessage("There's an error")
				.that(details).hasSize(1);
	}

	@Test
	public void testChildrenFindSuccess() {
		Node meepNode = new Node();
		meepNode.addChildNode(new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.hasChildren(ERROR_MESSAGE);

		assertWithMessage("There's no error")
				.that(details).isEmpty();
	}

	@Test
	public void testChildrenMinimumFailure() {
		Node meepNode = new Node();
		meepNode.addChildNode(new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.childMinimum(ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER);

		assertWithMessage("There's an error")
				.that(details).hasSize(1);
	}

	@Test
	public void testChildrenMinimumSuccess() {
		Node meepNode = new Node();
		meepNode.addChildNodes(new Node(TemplateId.PLACEHOLDER), new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.childMinimum(ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER);

		assertWithMessage("There's no error")
				.that(details).isEmpty();
	}

	@Test
	public void testChildrenMaximumFailure() {
		Node meepNode = new Node();
		meepNode.addChildNodes(new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.childMaximum(ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER);

		assertWithMessage("There's an error")
				.that(details).hasSize(1);
	}

	@Test
	public void testChildrenMaximumSuccess() {
		Node meepNode = new Node();
		meepNode.addChildNodes(new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.childMaximum(ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER);

		assertWithMessage("There's no error")
				.that(details).isEmpty();
	}

	//chaining
	@Test
	public void testValueChildrenFindFailure() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE).hasChildren(OTHER_ERROR_MESSAGE);

		assertWithMessage("message applied is other error message")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(OTHER_ERROR_MESSAGE);
	}

	@Test
	public void testValueChildrenFindSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");
		meepNode.addChildNode(new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE).hasChildren(OTHER_ERROR_MESSAGE);

		assertWithMessage("There's no error")
				.that(details).isEmpty();
	}

	@Test
	public void testChildValueChildrenFindFailure() {
		Node meepNode = new Node();
		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE)
				.value(ERROR_MESSAGE, VALUE)
				.childMaximum(ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER)
				.hasChildren(OTHER_ERROR_MESSAGE);

		assertWithMessage("message applied is other error message")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testValueChildrenChildMinChildMaxFindFailure() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");
		meepNode.addChildNodes(
				new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE)
				.hasChildren(ERROR_MESSAGE)
				.childMinimum(ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER)
				.childMaximum(OTHER_ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER);

		assertWithMessage("message applied is other error message")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(OTHER_ERROR_MESSAGE);
	}

	@Test
	public void testMaxFindMultipleTemplateIdsFailure() {
		Node meepNode = new Node();
		meepNode.addChildNodes(
				new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.DEFAULT));

		Checker checker = Checker.check(meepNode, details);
		checker.childMaximum("too many children", 2, TemplateId.PLACEHOLDER, TemplateId.DEFAULT);

		assertWithMessage("message applied is other error message")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly("too many children");
	}

	@Test
	public void testMaxFindMultipleTemplateIdsSuccess() {
		Node meepNode = new Node();
		meepNode.addChildNodes(
				new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.DEFAULT),
				new Node(TemplateId.ACI_AGGREGATE_COUNT));

		Checker checker = Checker.check(meepNode, details);
		checker.childMaximum("too many children", 3, TemplateId.PLACEHOLDER, TemplateId.DEFAULT);

		assertWithMessage("There should be no errors.")
				.that(details).isEmpty();
	}

	@Test
	public void testValueChildrenChildMinChildMaxFindSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");
		meepNode.addChildNode(new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE)
				.hasChildren(ERROR_MESSAGE)
				.childMinimum(ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER)
				.childMaximum(OTHER_ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER);

		assertWithMessage("There should be no errors.")
				.that(details).isEmpty();
	}

	// compound checking
	@Test
	public void compoundIntValueCheckSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE).greaterThan(ERROR_MESSAGE, 122);

		assertWithMessage("There should be no errors.")
				.that(details).isEmpty();
	}

	@Test
	public void compoundIntValueCheckFailure() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE).greaterThan(ERROR_MESSAGE, 124);

		assertWithMessage("There should be one error.")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void compoundIntValueCheckNoContext() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.greaterThan(ERROR_MESSAGE, 124);

		assertWithMessage("There should be no errors.")
				.that(details).isEmpty();
	}

	@Test(expected = ClassCastException.class)
	public void compoundIntValueCheckCastException() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE).greaterThan(ERROR_MESSAGE, "not an Integer");

		assertWithMessage("There should be no errors.")
				.that(details).isEmpty();
	}

	@Test
	public void testCompoundIntValueLessThanOrEqualToCheckSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE).lessThanOrEqualTo(ERROR_MESSAGE, 123);
		assertWithMessage("There should be no errors.")
				.that(details).isEmpty();
	}

	@Test
	public void testCompoundIntValueLessThanCheckSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE).lessThanOrEqualTo(ERROR_MESSAGE, 124);
		assertWithMessage("There should be no errors.")
				.that(details).isEmpty();
	}

	@Test
	public void testCompoundIntValueLessThanOrEqualToCheckFailure() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE).lessThanOrEqualTo(ERROR_MESSAGE, 122);
		assertWithMessage("There should be no errors.")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testCompoundIntValueLessThanOrEqualToCheckFailureShortcut() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");
		details.add(new Detail("test", "testPath"));

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE).lessThanOrEqualTo(ERROR_MESSAGE, 122);
		assertWithMessage("There should be no errors.")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly("test");
	}

	@Test
	public void testValueWithinDecimalRange() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "0.5");

		Checker checker = Checker.check(meepNode, details);
		checker.inDecimalRangeOf(ERROR_MESSAGE,VALUE, 0f,  1f);
		assertWithMessage("There should be no errors.")
				.that(details).isEmpty();
	}

	@Test
	public void testValueOutsideStartDecimalRange() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "-1");

		Checker checker = Checker.check(meepNode, details);
		checker.inDecimalRangeOf(ERROR_MESSAGE,VALUE, 0f,  1f);
		assertWithMessage("There should be one error.")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testValueOutsideDecimalRange() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "2");
		Checker checker = Checker.check(meepNode, details);
		checker.inDecimalRangeOf(ERROR_MESSAGE,VALUE, 0f,  1f);

		assertWithMessage("There should be one error.")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testInDecimalRangeOfIncorrectType() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "String");

		Checker checker = Checker.check(meepNode, details);
		checker.inDecimalRangeOf(ERROR_MESSAGE,VALUE, 0f,  1f);
		assertWithMessage("There should be one error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testInDecimalRangeNullValue() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, null);

		Checker checker = Checker.check(meepNode, details);
		checker.inDecimalRangeOf(ERROR_MESSAGE,VALUE, 0f,  1f);
		assertWithMessage("There should be one error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testInDecimalRangeOfShortcut() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "-1");
		details.add(new Detail("test", "testPath"));

		Checker checker = Checker.check(meepNode, details);
		checker.inDecimalRangeOf(ERROR_MESSAGE,VALUE, 0f,  1f);
		assertWithMessage("There should be one error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly("test");
	}

	// thorough checking
	@Test
	public void testIntValueChildrenChildMinChildMaxFindFailure() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "Bob");
		meepNode.addChildNodes(
				new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.thoroughlyCheck(meepNode, details);
		checker.intValue("int failure", VALUE)
				.hasChildren(ERROR_MESSAGE)
				.childMinimum(ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER)
				.childMaximum("maximum failure", 1, TemplateId.PLACEHOLDER);

		assertWithMessage("int validation error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly("int failure" ,"maximum failure");
	}

	@Test
	public void testIntValueChildrenChildMinChildMaxFindSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");
		meepNode.addChildNodes(
				new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.thoroughlyCheck(meepNode, details);
		checker.intValue("int failure", VALUE)
				.hasChildren(ERROR_MESSAGE)
				.childMinimum(ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER)
				.childMaximum("maximum failure", 2, TemplateId.PLACEHOLDER);

		assertWithMessage("There should be no errors.")
				.that(details).isEmpty();
	}

	@Test
	public void testHasMeasuresSuccess() {
		String measureId = "measureId";
		String expectedMeasure1 = "asdf";
		String expectedMeasure2 = "jkl;";
		String anotherMeasureId = "DogCow";

		Node section = new Node();
		Node measure1 = new Node();
		measure1.putValue(measureId, expectedMeasure1);
		Node measure2 = new Node();
		measure2.putValue(measureId, anotherMeasureId);
		Node measure3 = new Node();
		measure3.putValue(measureId, expectedMeasure2);

		section.addChildNodes(measure1, measure2, measure3);

		Checker checker = Checker.check(section, details);
		checker.hasMeasures("measure failure", expectedMeasure1, expectedMeasure2);

		assertWithMessage("All the measures should have been found.")
				.that(details).isEmpty();
	}

	@Test
	public void testHasMeasuresFailure() {
		String measureId = "measureId";
		String expectedMeasure = "DogCow";
		String anotherMeausure1 = "asdf";
		String anotherMeausure2 = "jkl;";
		String anotherMeausure3 = "qwerty";
		String validationError = "measure failure";

		Node section = new Node();
		Node measure1 = new Node();
		measure1.putValue(measureId, anotherMeausure1);
		Node measure2 = new Node();
		measure2.putValue(measureId, anotherMeausure2);
		Node measure3 = new Node();
		measure3.putValue(measureId, anotherMeausure3);

		section.addChildNodes(measure1, measure2, measure3);

		Checker checker = Checker.check(section, details);

		checker.hasMeasures(validationError, expectedMeasure);

		assertWithMessage("The validation error string did not match up.")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(validationError);
	}

	@Test
	public void testCheckerHasMeasuresShortCut() {
		Set<Detail> errors = new LinkedHashSet<>();
		Detail err = new Detail("test");
		errors.add(err);
		Node root = new Node();
		Checker.check(root, errors)
				.hasMeasures("Some Message", "MeasureId");

		assertWithMessage("Checker should return one validation error")
				.that(errors).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly("test");
	}

	@Test
	public void testCheckerHasInvalidMeasure() {
		Set<Detail> errors = new LinkedHashSet<>();

		Node root = new Node();
		Node measure = new Node(TemplateId.CLINICAL_DOCUMENT, root);
		measure.putValue("NotAMeasure", "0");
		root.addChildNode(measure);
		Checker.check(root, errors)
				.hasMeasures("Some Message", "MeasureId");

		assertWithMessage("Checker should return one validation error")
				.that(errors).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly("Some Message");
	}

	@Test
	public void testHasChildrenWithTemplateIdSuccess() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION);
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE);
		iaSectionNode.addChildNode(iaMeasureNode);

		Checker checker = Checker.check(iaSectionNode, details);
		checker.onlyHasChildren(ERROR_MESSAGE, TemplateId.IA_MEASURE);

		assertWithMessage("There should be no errors")
				.that(details).isEmpty();
	}

	@Test
	public void testHasChildrenWithTemplateIdFailure() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION);
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE);
		iaSectionNode.addChildNode(iaMeasureNode);

		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		iaSectionNode.addChildNode(aggregateCountNode);

		Checker checker = Checker.check(iaSectionNode, details);
		checker.onlyHasChildren(ERROR_MESSAGE, TemplateId.IA_MEASURE);

		assertWithMessage("There should be an error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testValueIn() throws Exception {
		String key = "My Key";
		String value = "My Value";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, key, "No Value" , "Some Value", "My Value");

		assertWithMessage("There should be no errors")
				.that(details).isEmpty();
	}

	@Test
	public void testValueInNot() throws Exception {
		String key = "My Key";
		String value = "My Value Not";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, key, "No Value" , "Some Value", "My Value");

		assertWithMessage("There should be an error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}
	@Test
	public void testValueInNull() throws Exception {
		String key = "My Key";
		Node testNode = makeTestNode(key, null);
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, key, null);

		assertWithMessage("There should be an error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}
	@Test
	public void testValueInKeyNull() throws Exception {
		String key = "My Key";
		String value = "My Value";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, null, null);

		assertWithMessage("There should be an error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testValueInNulls() throws Exception {
		String key = "My Key";
		String value = "My Value";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, key, null);

		assertWithMessage("There should be an error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testValueInShouldShortCut() throws Exception {
		String key = "My Key";
		Node testNode = makeTestNode(key, null);
		details.add(new Detail(ERROR_MESSAGE));
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, key, null , "Some Value", "My Value");

		assertWithMessage("There should be an error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testHappyValueIsEmptyAsNull() throws Exception {
		String key = "My Key";
		Node testNode = makeTestNode(key, null);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsEmpty(ERROR_MESSAGE, key);
		assertWithMessage("There should be no errors")
				.that(details).isEmpty();
	}

	@Test
	public void testHappyValueIsEmptyAsString() throws Exception {
		String key = "My Key";
		String value = "";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsEmpty(ERROR_MESSAGE, key);

		assertWithMessage("There should be no errors")
				.that(details).isEmpty();
	}

	@Test
	public void testUnhappyValueIsEmpty() throws Exception {
		String key = "My Key";
		String value = "Not Null Value";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsEmpty(ERROR_MESSAGE, key);

		assertWithMessage("There should be no errors")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testHappyValueIsNotEmptyAsString() throws Exception {
		String key = "My Key";
		String value = "Not Empty";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsNotEmpty(ERROR_MESSAGE, key);
		assertWithMessage("There should be no errors")
				.that(details).isEmpty();
	}

	@Test
	public void testUnhappyValueIsNotEmptyAsString() throws Exception {
		String key = "My Key";
		String value = "";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsNotEmpty(ERROR_MESSAGE, key);

		assertWithMessage("There should be no errors")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	@Test
	public void testUnhappyValueIsNotEmptyAsNull() throws Exception {
		String key = "My Key";
		String value = null;
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsNotEmpty(ERROR_MESSAGE, key);
		assertWithMessage("There should be no errors")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(ERROR_MESSAGE);
	}

	private Node makeTestNode(String key, String value) {
		Node testNode = new Node();
		testNode.putValue(key, value);
		return testNode;
	}

}
