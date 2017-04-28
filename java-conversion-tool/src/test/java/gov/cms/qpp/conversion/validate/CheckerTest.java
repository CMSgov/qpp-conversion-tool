package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
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
}
