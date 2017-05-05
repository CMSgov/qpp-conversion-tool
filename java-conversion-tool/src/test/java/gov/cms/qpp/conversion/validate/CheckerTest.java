package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by clydetedrick on 4/7/17.
 */
public class CheckerTest {
	private static final String PARENT = "parent";
	private static final String VALUE = "value";
	private static final String ERROR_MESSAGE = "error message";
	private static final String OTHER_ERROR_MESSAGE = "some other error message";

	private List<ValidationError> validationErrors;

	@Before
	public void beforeEach() {
		validationErrors = new ArrayList<>();
	}

	@Test
	public void testValueFindFailure() {
		Node meepNode = new Node( PARENT );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.value( ERROR_MESSAGE, VALUE );

		assertFalse("There's an error", validationErrors.isEmpty() );
		assertEquals( "message applied is the message given", validationErrors.get( 0 ).getErrorText(), ERROR_MESSAGE );
	}

	@Test
	public void testParentFailure() {
		Node meepNode = new Node( PARENT );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.hasParent( ERROR_MESSAGE,  TemplateId.ACI_DENOMINATOR) //fails
				.hasParent( ERROR_MESSAGE,  TemplateId.ACI_DENOMINATOR); //shortcuts

		assertEquals("There's an error", 1, validationErrors.size());
		assertEquals( "message applied is the message given", validationErrors.get( 0 ).getErrorText(), ERROR_MESSAGE );
	}

	@Test
	public void testValueFindSuccess() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "Bob" );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.value( ERROR_MESSAGE, VALUE );

		assertTrue("There's no error", validationErrors.isEmpty() );
	}

	@Test
	public void testIntValueFindFailure() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "Bob" );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.intValue( ERROR_MESSAGE, VALUE );

		assertFalse("There's an error", validationErrors.isEmpty() );
	}

	@Test
	public void testIntValueFindSuccess() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "123" );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.value( ERROR_MESSAGE, VALUE );

		assertTrue("There's no error", validationErrors.isEmpty() );
	}

	@Test
	public void testChildrenFindFailure() {
		Node meepNode = new Node( PARENT );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.hasChildren( ERROR_MESSAGE );

		assertFalse("There's an error", validationErrors.isEmpty() );
	}

	@Test
	public void testChildrenFindSuccess() {
		Node meepNode = new Node( PARENT );
		meepNode.addChildNode( new Node(TemplateId.PLACEHOLDER.getTemplateId() ) );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.hasChildren( ERROR_MESSAGE );

		assertTrue("There's no error", validationErrors.isEmpty() );
	}

	@Test
	public void testChildrenMinimumFailure() {
		String templateId = TemplateId.PLACEHOLDER.getTemplateId();
		Node meepNode = new Node( PARENT );
		meepNode.addChildNode( new Node( templateId ) );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.childMinimum( ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER );

		assertFalse("There's an error", validationErrors.isEmpty() );
	}

	@Test
	public void testChildrenMinimumSuccess() {
		String templateId = TemplateId.PLACEHOLDER.getTemplateId();
		Node meepNode = new Node( PARENT );
		meepNode.addChildNodes( new Node( templateId ), new Node( templateId ) );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.childMinimum( ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER );

		assertTrue("There's no error", validationErrors.isEmpty() );
	}

	@Test
	public void testChildrenMaximumFailure() {
		String templateId = TemplateId.PLACEHOLDER.getTemplateId();
		Node meepNode = new Node( PARENT );
		meepNode.addChildNodes( new Node( templateId ),
				new Node( templateId ),
				new Node( templateId ));

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.childMaximum( ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER );

		assertFalse("There's an error", validationErrors.isEmpty() );
	}

	@Test
	public void testChildrenMaximumSuccess() {
		String templateId = TemplateId.PLACEHOLDER.getTemplateId();
		Node meepNode = new Node( PARENT );
		meepNode.addChildNodes( new Node( templateId ),
				new Node( templateId ) );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.childMaximum( ERROR_MESSAGE, 2, TemplateId.PLACEHOLDER );

		assertTrue("There's no error", validationErrors.isEmpty() );
	}

	//chaining
	@Test
	public void testValueChildrenFindFailure() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "123" );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.value( ERROR_MESSAGE, VALUE ).hasChildren( OTHER_ERROR_MESSAGE );

		assertFalse("There's an error", validationErrors.isEmpty() );
		assertEquals( "message applied is other error message", validationErrors.get( 0 ).getErrorText(), OTHER_ERROR_MESSAGE );
	}

	@Test
	public void testValueChildrenFindSuccess() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "123" );
		meepNode.addChildNode( new Node( TemplateId.PLACEHOLDER.getTemplateId() ) );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.value( ERROR_MESSAGE, VALUE ).hasChildren( OTHER_ERROR_MESSAGE );

		assertTrue("There's no error", validationErrors.isEmpty() );
	}

	@Test
	public void testChildValueChildrenFindFailure() {
		Node meepNode = new Node( PARENT );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.intValue( ERROR_MESSAGE, VALUE )
				.value( ERROR_MESSAGE, VALUE )
				.childMaximum( ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER )
				.hasChildren( OTHER_ERROR_MESSAGE );

		assertFalse("There's an error", validationErrors.isEmpty() );
	}

	@Test
	public void testValueChildrenChildMinChildMaxFindFailure() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "123" );
		meepNode.addChildNodes(
				new Node( TemplateId.PLACEHOLDER.getTemplateId() ),
				new Node( TemplateId.PLACEHOLDER.getTemplateId() ));

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.value( ERROR_MESSAGE, VALUE )
				.hasChildren( ERROR_MESSAGE )
				.childMinimum( ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER )
				.childMaximum( OTHER_ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER );

		assertFalse("There's an error", validationErrors.isEmpty() );
		assertEquals( "message applied is other error message", validationErrors.get( 0 ).getErrorText(), OTHER_ERROR_MESSAGE );
	}

	@Test
	public void testMaxFindMultipleTemplateIdsFailure() {
		Node meepNode = new Node( PARENT );
		meepNode.addChildNodes(
				new Node( TemplateId.PLACEHOLDER.getTemplateId() ),
				new Node( TemplateId.PLACEHOLDER.getTemplateId() ),
				new Node( TemplateId.DEFAULT.getTemplateId() ));

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.childMaximum( "too many children", 2, TemplateId.PLACEHOLDER, TemplateId.DEFAULT );

		assertFalse("There's an error", validationErrors.isEmpty() );
		assertEquals( "message applied is other error message", validationErrors.get( 0 ).getErrorText(), "too many children" );
	}

	@Test
	public void testMaxFindMultipleTemplateIdsSuccess() {
		Node meepNode = new Node( PARENT );
		meepNode.addChildNodes(
				new Node( TemplateId.PLACEHOLDER.getTemplateId() ),
				new Node( TemplateId.PLACEHOLDER.getTemplateId() ),
				new Node( TemplateId.DEFAULT.getTemplateId() ),
				new Node( TemplateId.ACI_AGGREGATE_COUNT.getTemplateId() ));

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.childMaximum( "too many children", 3, TemplateId.PLACEHOLDER, TemplateId.DEFAULT );

		assertTrue("There's no error", validationErrors.isEmpty() );
	}

	@Test
	public void testValueChildrenChildMinChildMaxFindSuccess() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "123" );
		meepNode.addChildNode( new Node( TemplateId.PLACEHOLDER.getTemplateId() ) );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.value( ERROR_MESSAGE, VALUE )
				.hasChildren( ERROR_MESSAGE )
				.childMinimum( ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER )
				.childMaximum( OTHER_ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER );

		assertTrue("There's no error", validationErrors.isEmpty() );
	}

	// compound checking
	@Test
	public void compoundIntValueCheckSuccess() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "123" );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.intValue( ERROR_MESSAGE, VALUE )
				.greaterThan(ERROR_MESSAGE, 122);

		assertTrue("There's no error", validationErrors.isEmpty() );
	}

	@Test
	public void compoundIntValueCheckFailure() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "123" );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.intValue( ERROR_MESSAGE, VALUE )
				.greaterThan(ERROR_MESSAGE, 124);

		assertFalse("There's an error", validationErrors.isEmpty() );
	}

	@Test
	public void compoundIntValueCheckNoContext() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "123" );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.greaterThan(ERROR_MESSAGE, 124);

		assertTrue("There's no error", validationErrors.isEmpty() );
	}

	@Test(expected = ClassCastException.class)
	public void compoundIntValueCheckCastException() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "123" );

		Checker checker = Checker.check( meepNode, validationErrors );
		checker.intValue( ERROR_MESSAGE, VALUE )
				.greaterThan(ERROR_MESSAGE, "not an Integer");

		assertFalse("There's an error", validationErrors.isEmpty() );
	}

	// thorough checking
	@Test
	public void testIntValueChildrenChildMinChildMaxFindFailure() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "Bob" );
		meepNode.addChildNodes(
				new Node( TemplateId.PLACEHOLDER.getTemplateId() ),
				new Node( TemplateId.PLACEHOLDER.getTemplateId() ));

		Checker checker = Checker.thoroughlyCheck( meepNode, validationErrors );
		checker.intValue( "int failure", VALUE )
				.hasChildren( ERROR_MESSAGE )
				.childMinimum( ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER )
				.childMaximum( "maximum failure", 1, TemplateId.PLACEHOLDER );

		assertEquals("There are errors", validationErrors.size(), 2 );
		assertEquals( "int validation error", validationErrors.get( 0 ).getErrorText(), "int failure" );
		assertEquals( "maximum validation failure", validationErrors.get( 1 ).getErrorText(), "maximum failure" );
	}

	@Test
	public void testIntValueChildrenChildMinChildMaxFindSuccess() {
		Node meepNode = new Node( PARENT );
		meepNode.putValue( VALUE, "123" );
		meepNode.addChildNodes(
				new Node( TemplateId.PLACEHOLDER.getTemplateId() ),
				new Node( TemplateId.PLACEHOLDER.getTemplateId() ));

		Checker checker = Checker.thoroughlyCheck( meepNode, validationErrors );
		checker.intValue( "int failure", VALUE )
				.hasChildren( ERROR_MESSAGE )
				.childMinimum( ERROR_MESSAGE, 1, TemplateId.PLACEHOLDER )
				.childMaximum( "maximum failure", 2, TemplateId.PLACEHOLDER );

		assertTrue("There are no errors", validationErrors.isEmpty() );
	}

	@Test
	public void testHasMeasuresSuccess() {
		String measure = "measure";
		String measureId = "measureId";
		String expectedMeasure1 = "asdf";
		String expectedMeasure2 = "jkl;";
		String anotherMeasureId = "DogCow";

		Node section = new Node(PARENT);
		Node measure1 = new Node(measure);
		measure1.putValue(measureId, expectedMeasure1);
		Node measure2 = new Node(measure);
		measure2.putValue(measureId, anotherMeasureId);
		Node measure3 = new Node(measure);
		measure3.putValue(measureId, expectedMeasure2);

		section.addChildNodes(measure1, measure2, measure3);

		Checker checker = Checker.check(section, validationErrors);

		checker.hasMeasures("measure failure", expectedMeasure1, expectedMeasure2);

		assertThat("All the measures should have been found.", validationErrors, hasSize(0));
	}

	@Test
	public void testHasMeasuresFailure() {
		String measure = "measure";
		String measureId = "measureId";
		String expectedMeasure = "DogCow";
		String anotherMeausure1 = "asdf";
		String anotherMeausure2 = "jkl;";
		String anotherMeausure3 = "qwerty";
		String validationError = "measure failure";

		Node section = new Node(PARENT);
		Node measure1 = new Node(measure);
		measure1.putValue(measureId, anotherMeausure1);
		Node measure2 = new Node(measure);
		measure2.putValue(measureId, anotherMeausure2);
		Node measure3 = new Node(measure);
		measure3.putValue(measureId, anotherMeausure3);

		section.addChildNodes(measure1, measure2, measure3);

		Checker checker = Checker.check(section, validationErrors);

		checker.hasMeasures(validationError, expectedMeasure);

		assertThat("A measure should not have been found.", validationErrors, hasSize(1));
		assertThat("The validation error string did not match up.", validationErrors.get(0).getErrorText(), is(validationError));
	}

	@Test
	public void testHasChildrenWithTemplateIdSuccess() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION.getTemplateId());
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());
		iaSectionNode.addChildNode(iaMeasureNode);

		Checker checker = Checker.check(iaSectionNode, validationErrors);
		checker.onlyHasChildren(ERROR_MESSAGE, TemplateId.IA_MEASURE);

		assertTrue("There are no errors", validationErrors.isEmpty());
	}

	@Test
	public void testHasChildrenWithTemplateIdFailure() {
		Node iaSectionNode = new Node(TemplateId.IA_SECTION.getTemplateId());
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());
		iaSectionNode.addChildNode(iaMeasureNode);

		Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		iaSectionNode.addChildNode(aggregateCountNode);

		Checker checker = Checker.check(iaSectionNode, validationErrors);
		checker.onlyHasChildren(ERROR_MESSAGE, TemplateId.IA_MEASURE);

		assertThat("There should be an error", validationErrors.get(0).getErrorText(), is(ERROR_MESSAGE));
	}
}
