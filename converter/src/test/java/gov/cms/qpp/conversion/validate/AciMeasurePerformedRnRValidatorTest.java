package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

public class AciMeasurePerformedRnRValidatorTest {

	private AciMeasurePerformedRnRValidator validator;
	private Node aciMeasurePerformedRnRNode;

	@Before
	public void init() {
		validator = new AciMeasurePerformedRnRValidator();

		aciMeasurePerformedRnRNode = new Node(TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS);
		aciMeasurePerformedRnRNode.putValue("measureId", "ACI_INFBLO_1");

		Node measurePerformed = new Node(TemplateId.MEASURE_PERFORMED);
		aciMeasurePerformedRnRNode.addChildNode(measurePerformed);
	}

	@Test
	public void testValidateGoodData() throws Exception {
		Set<Detail> errors = run();
		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testWithNoMeasureId() throws Exception {
		aciMeasurePerformedRnRNode.removeValue("measureId");
		Set<Detail> errors = run();
		assertThat("Validation error size should be 1", errors, hasSize(1));
	}

	@Test
	public void testWithNoChildren() throws Exception {
		aciMeasurePerformedRnRNode.getChildNodes().clear();
		Set<Detail> errors = run();
		assertThat("Validation error size should be 1", errors, hasSize(1));
	}

	private Set<Detail> run() {
		return validator.validateSingleNode(aciMeasurePerformedRnRNode);
	}

}