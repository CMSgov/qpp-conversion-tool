package gov.cms.qpp.conversion.validate;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

public class AciMeasurePerformedRnRValidatorTest {

	private AciMeasurePerformedRnRValidator validator;
	private Node aciMeasurePerformedRnRNode;
	private Node measurePerformed;

	@Before
	public void init() {
		validator = new AciMeasurePerformedRnRValidator();

		aciMeasurePerformedRnRNode = new Node(TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS);
		aciMeasurePerformedRnRNode.putValue("measureId", "ACI_INFBLO_1");

		measurePerformed = new Node(TemplateId.MEASURE_PERFORMED);
		aciMeasurePerformedRnRNode.addChildNode(measurePerformed);
	}

	@Test
	public void testValidateGoodData() throws Exception {
		List<Detail> errors = run();
		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testWithNoMeasureId() throws Exception {
		aciMeasurePerformedRnRNode.removeValue("measureId");
		List<Detail> errors = run();
		assertThat("Validation error size should be 1", errors, hasSize(1));
	}

	@Test
	public void testWithNoChildren() throws Exception {
		aciMeasurePerformedRnRNode.getChildNodes().clear();
		List<Detail> errors = run();
		assertThat("Validation error size should be 2", errors, hasSize(2));
	}

	private List<Detail> run() {
		return validator.validateSingleNode(aciMeasurePerformedRnRNode);
	}

}