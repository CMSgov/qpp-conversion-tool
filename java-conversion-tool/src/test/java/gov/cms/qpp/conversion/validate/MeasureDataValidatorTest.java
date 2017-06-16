package gov.cms.qpp.conversion.validate;

import static gov.cms.qpp.conversion.validate.MeasureDataValidator.MISSING_AGGREGATE_COUNT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import gov.cms.qpp.ConversionTestSuite;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.xml.XmlUtils;

/**
 * Test the MeasureData Validator
 */
public class MeasureDataValidatorTest extends ConversionTestSuite {
	@Test
	public void internalValidateSingleNode() throws Exception {
		String happy = getFixture("measureDataHappy.xml");
		Node placeholder = new QppXmlDecoder().decode(XmlUtils.stringToDom(happy));
		MeasureDataValidator validator = new MeasureDataValidator();
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2);
		validator.internalValidateSingleNode(underTest);

		List<Detail> errors = validator.getDetails();
		assertThat("Expect no errors on the happy path ", errors.isEmpty(), is(true));
	}

	@Test
	public void missingAggregateCount() throws Exception {
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		List<Detail> errors = validator.getDetails();
		assertThat(errors.isEmpty(), is(false));
		assertEquals(errors.get(0).getMessage(), MISSING_AGGREGATE_COUNT);
	}

}