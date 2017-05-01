package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import java.util.List;

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
		Node underTest = placeholder.findFirstNode(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		validator.internalValidateSingleNode(underTest);

		List<ValidationError> errors = validator.getValidationErrors();
		assertThat("Expect no errors on the happy path ", errors.isEmpty(), is(true));
	}

	@Test
	public void missingAggregateCount() throws Exception {
		Node testNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		MeasureDataValidator validator = new MeasureDataValidator();
		validator.internalValidateSingleNode(testNode);

		List<ValidationError> errors = validator.getValidationErrors();
		assertThat(errors.isEmpty(), is(false));
		assertEquals(errors.get(0).getErrorText(), MISSING_AGGREGATE_COUNT);
	}

}