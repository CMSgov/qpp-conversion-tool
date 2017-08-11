package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.correlation.model.Template;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import java.util.Set;

import static gov.cms.qpp.conversion.validate.MeasureDataValidator.MISSING_AGGREGATE_COUNT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test the MeasureData Validator
 */
public class MeasureDataValidatorTest extends BaseTest {
	@Test
	public void internalValidateSingleNode() throws Exception {
		String happy = getFixture("measureDataHappy.xml");
		Node placeholder = new QppXmlDecoder().decode(XmlUtils.stringToDom(happy));
		MeasureDataValidator validator = new MeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		Set<Detail> errors = validator.getDetails();
		assertThat("Expect no errors on the happy path ", errors.isEmpty(), is(true));
	}

	@Test
	public void missingAggregateCount() throws Exception {
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		Set<Detail> errors = validator.getDetails();
		assertThat(errors.isEmpty(), is(false));
		assertEquals(errors.iterator().next().getMessage(), MISSING_AGGREGATE_COUNT);
	}

	@Test
	public void invalidAggregateCount() throws Exception {
		Node aggregateCount = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		testNode.addChildNode(aggregateCount);
		aggregateCount.putValue("aggregateCount", "error");
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		Set<Detail> errors = validator.getDetails();
		assertThat(errors.iterator().next().getMessage(), is(AggregateCountValidator.TYPE_ERROR));
	}

	@Test
	public void duplicateAggregateCountsFails() throws Exception {
		Node aggregateCount = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCount.putValue("aggregateCount", "100");
		aggregateCount.putValue("aggregateCount", "200", false);
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		testNode.addChildNodes(aggregateCount);
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		Set<Detail> errors = validator.getDetails();
		assertThat(errors.iterator().next().getMessage(), is(AggregateCountValidator.VALUE_ERROR));
	}

	@Test
	public void negativeAggregateCountsFails() throws Exception {
		Node aggregateCount = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCount.putValue("aggregateCount", "-1");
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		testNode.addChildNodes(aggregateCount);
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		Set<Detail> errors = validator.getDetails();
		assertThat(errors.iterator().next().getMessage(), is(MeasureDataValidator.INVALID_VALUE));
	}

}