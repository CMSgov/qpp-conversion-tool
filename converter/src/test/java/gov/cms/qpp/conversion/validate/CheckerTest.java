package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.validationErrorTextMatches;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

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

		assertThat("There's an error", details, hasSize(1));
		assertThat("message applied is the message given", details.iterator().next(),
				validationErrorTextMatches(ERROR_MESSAGE));
	}

	@Test
	public void testParentFailure() {
		Node meepNode = new Node();

		Checker checker = Checker.check(meepNode, details);
		checker.hasParent(ERROR_MESSAGE, TemplateId.ACI_DENOMINATOR) //fails
				.hasParent(ERROR_MESSAGE, TemplateId.ACI_DENOMINATOR); //shortcuts

		assertThat("There's an error", details, hasSize(1));
		assertThat("message applied is the message given", details.iterator().next(),
				validationErrorTextMatches(ERROR_MESSAGE));
	}

	@Test
	public void testValueFindSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "Bob");

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE);
		assertThat("There's no error", details, empty());
	}

	@Test
	public void testIntValueFindFailure() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "Bob");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE);

		assertThat("There's an error", details, hasSize(1));
	}

	@Test
	public void testIntValueFindSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE);

		assertThat("There's no error", details, empty());
	}

	@Test
	public void testChildrenFindFailure() {
		Node meepNode = new Node();

		Checker checker = Checker.check(meepNode, details);
		checker.hasChildren(ERROR_MESSAGE);

		assertThat ("There's an error", details, hasSize(1));
	}

	@Test
	public void testChildrenFindSuccess() {
		Node meepNode = new Node();
		meepNode.addChildNode(new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.hasChildren(ERROR_MESSAGE);

		assertThat("There's no error", details, empty());
	}

	@Test
	public void testChildrenMinimumFailure() {
		Node meepNode = new Node();
		meepNode.addChildNode(new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.childMinimum(ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER);

		assertThat("There's an error", details, hasSize(1));
	}

	@Test
	public void testChildrenMinimumSuccess() {
		Node meepNode = new Node();
		meepNode.addChildNodes(new Node(TemplateId.PLACEHOLDER), new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.childMinimum(ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER);

		assertThat("There's no error", details, empty());
	}

	@Test
	public void testChildrenMaximumFailure() {
		Node meepNode = new Node();
		meepNode.addChildNodes(new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.childMaximum(ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER);

		assertThat("There's an error", details, hasSize(1));
	}

	@Test
	public void testChildrenMaximumSuccess() {
		Node meepNode = new Node();
		meepNode.addChildNodes(new Node(TemplateId.PLACEHOLDER),
				new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.childMaximum(ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER);

		assertThat("There's no error", details, empty());
	}

	//chaining
	@Test
	public void testValueChildrenFindFailure() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE).hasChildren(OTHER_ERROR_MESSAGE);

		assertThat("There's an error", details, hasSize(1));
		assertThat("message applied is other error message", details.iterator().next(),
				validationErrorTextMatches(OTHER_ERROR_MESSAGE));
	}

	@Test
	public void testValueChildrenFindSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");
		meepNode.addChildNode(new Node(TemplateId.PLACEHOLDER));

		Checker checker = Checker.check(meepNode, details);
		checker.value(ERROR_MESSAGE, VALUE).hasChildren(OTHER_ERROR_MESSAGE);

		assertThat("There's no error", details, empty());
	}

	@Test
	public void testChildValueChildrenFindFailure() {
		Node meepNode = new Node();

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE)
				.value(ERROR_MESSAGE, VALUE)
				.childMaximum(ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER)
				.hasChildren(OTHER_ERROR_MESSAGE);
		assertThat("There's an error", details, hasSize(1));
		assertThat("message applied is other error message", details.iterator().next(),
				validationErrorTextMatches(ERROR_MESSAGE));
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

		assertThat("There's an error", details, hasSize(1));
		assertThat("message applied is other error message", details.iterator().next(),
				validationErrorTextMatches(OTHER_ERROR_MESSAGE));
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

		assertThat("There's an error", details, hasSize(1));
		assertThat("message applied is other error message", details.iterator().next(),
				validationErrorTextMatches("too many children"));
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
		assertThat("There should be no errors.", details, empty());
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
		assertThat("There should be no errors.", details, empty());
	}

	// compound checking
	@Test
	public void compoundIntValueCheckSuccess() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE).greaterThan(ERROR_MESSAGE, 122);
		assertThat("There should be no errors.", details, empty());
	}

	@Test
	public void compoundIntValueCheckFailure() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE).greaterThan(ERROR_MESSAGE, 124);
		assertThat("There should be one error.", details, hasSize(1));
	}

	@Test
	public void compoundIntValueCheckNoContext() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.greaterThan(ERROR_MESSAGE, 124);
		assertThat("There should be no errors.", details, empty());
	}

	@Test(expected = ClassCastException.class)
	public void compoundIntValueCheckCastException() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "123");

		Checker checker = Checker.check(meepNode, details);
		checker.intValue(ERROR_MESSAGE, VALUE).greaterThan(ERROR_MESSAGE, "not an Integer");
		assertThat("There should be no errors.", details, empty());
	}

	@Test
	public void testValueWithinDecimalRange() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "0.5");

		Checker checker = Checker.check(meepNode, details);
		checker.inDecimalRangeOf(ERROR_MESSAGE,VALUE, 0f,  1f);
		assertThat("There should be no errors.", details, empty());
	}

	@Test
	public void testValueOutsideDecimalRange() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "2");

		Checker checker = Checker.check(meepNode, details);
		checker.inDecimalRangeOf(ERROR_MESSAGE,VALUE, 0f,  1f);
		assertThat("There should be one error.", details, hasSize(1));
	}

	@Test
	public void testInDecimalRangeOfIncorrectType() {
		Node meepNode = new Node();
		meepNode.putValue(VALUE, "String");

		Checker checker = Checker.check(meepNode, details);
		checker.inDecimalRangeOf(ERROR_MESSAGE,VALUE, 0f,  1f);
		assertThat("There should be one error", details, hasSize(1));
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

		assertThat("There are errors", details, hasSize(2));
		assertThat("int validation error", details,
				hasValidationErrorsIgnoringPath("int failure" ,"maximum failure"));
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

		assertThat("There should be no errors.", details, empty());
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

		assertThat("All the measures should have been found.", details, empty());
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

		assertThat("A measure should not have been found.", details, hasSize(1));
		assertThat("The validation error string did not match up.", details.iterator().next(),
				validationErrorTextMatches(validationError));
	}

	@Test
	public void testCheckerHasMeasuresShortCut() {
		Set<Detail> errors = new LinkedHashSet<>();
		Detail err = new Detail();
		errors.add(err);
		Node root = new Node();
		Checker.check(root, errors)
				.hasMeasures("Some Message", "MeasureId");

		assertThat("Checker should return one validation error", errors, hasSize(1));

	}

	@Test
	public void testCheckerHasInvalidMeasure() {
		Set<Detail> errors = new LinkedHashSet<>();

		Node root = new Node();
		Node measure = new Node(TemplateId.CLINICAL_DOCUMENT, root);
		measure.putValue("NotAmeasure", "0");
		root.addChildNode(measure);
		Checker checker = Checker.check(root, errors)
				.hasMeasures("Some Message", "MeasureId");

		assertThat("Checker should return one validation error", errors, hasSize(1));

	}

	@Test
	public void testHasChildrenWithTemplateIdSuccess() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION);
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE);
		iaSectionNode.addChildNode(iaMeasureNode);

		Checker checker = Checker.check(iaSectionNode, details);
		checker.onlyHasChildren(ERROR_MESSAGE, TemplateId.IA_MEASURE);
		assertThat("There should be no errors", details, empty());
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

		assertThat("There should be an error",
				details.iterator().next(), validationErrorTextMatches(ERROR_MESSAGE));
	}

	@Test
	public void testValueIn() throws Exception {
		String key = "My Key";
		String value = "My Value";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, key, "No Value" , "Some Value", "My Value");
		assertThat("There should be no errors", details, empty());
	}

	@Test
	public void testValueInNot() throws Exception {
		String key = "My Key";
		String value = "My Value Not";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, key, "No Value" , "Some Value", "My Value");
		assertThat("There should be 1 error", details, hasSize(1));
		assertThat("There should be an error",
				details.iterator().next(), validationErrorTextMatches(ERROR_MESSAGE));
	}
	@Test
	public void testValueInNull() throws Exception {
		String key = "My Key";
		String value = null;
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, key, null);
		assertThat("There should be 1 error", details, hasSize(1));
		assertThat("There should be an error",
				details.iterator().next(), validationErrorTextMatches(ERROR_MESSAGE));
	}
	@Test
	public void testValueInKeyNull() throws Exception {
		String key = "My Key";
		String value = "My Value";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, null, null);
		assertThat("There should be 1 error", details, hasSize(1));
		assertThat("There should be an error",
				details.iterator().next(), validationErrorTextMatches(ERROR_MESSAGE));
	}
	@Test
	public void testValueInNulls() throws Exception {
		String key = "My Key";
		String value = "My Value";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, key, null);
		assertThat("There should be 1 error", details, hasSize(1));
		assertThat("There should be an error",
				details.iterator().next(), validationErrorTextMatches(ERROR_MESSAGE));
	}
	@Test
	public void testValueInShouldShortCut() throws Exception {
		String key = "My Key";
		String value = null;
		Node testNode = makeTestNode(key, value);
		details.add(new Detail(ERROR_MESSAGE));
		Checker checker = Checker.check(testNode, details);
		checker.valueIn(ERROR_MESSAGE, key, null , "Some Value", "My Value");
		assertThat("There should be 1 error", details, hasSize(1));
		assertThat("There should be an error",
				details.iterator().next(), validationErrorTextMatches(ERROR_MESSAGE));
	}
	@Test
	public void testHappyValueIsEmptyAsNull() throws Exception {
		String key = "My Key";
		String value = null;
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsEmpty(ERROR_MESSAGE, key);
		assertThat("There should be no errors", details, hasSize(0));
	}
	@Test
	public void testHappyValueIsEmptyAsString() throws Exception {
		String key = "My Key";
		String value = "";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsEmpty(ERROR_MESSAGE, key);
		assertThat("There should be no errors", details, hasSize(0));
	}
	@Test
	public void testUnhappyValueIsEmpty() throws Exception {
		String key = "My Key";
		String value = "Not Null Value";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsEmpty(ERROR_MESSAGE, key);
		assertThat("There should be no errors",
				details.iterator().next().getMessage(), is(ERROR_MESSAGE));
	}
	@Test
	public void testHappyValueIsNotEmptyAsString() throws Exception {
		String key = "My Key";
		String value = "Not Empty";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsNotEmpty(ERROR_MESSAGE, key);
		assertThat("There should be no errors", details, hasSize(0));
	}
	@Test
	public void testUnhappyValueIsNotEmptyAsString() throws Exception {
		String key = "My Key";
		String value = "";
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsNotEmpty(ERROR_MESSAGE, key);
		assertThat("There should be no errors",
				details.iterator().next().getMessage(), is(ERROR_MESSAGE));
	}
	@Test
	public void testUnhappyValueIsNotEmptyAsNull() throws Exception {
		String key = "My Key";
		String value = null;
		Node testNode = makeTestNode(key, value);
		Checker checker = Checker.check(testNode, details);
		checker.valueIsNotEmpty(ERROR_MESSAGE, key);
		assertThat("There should be no errors",
				details.iterator().next().getMessage(), is(ERROR_MESSAGE));
	}

	private Node makeTestNode(String key, String value) {
		Node testNode = new Node();
		testNode.putValue(key, value);
		return testNode;
	}

}
