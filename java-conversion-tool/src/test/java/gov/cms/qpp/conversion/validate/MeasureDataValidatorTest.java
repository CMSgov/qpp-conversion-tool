package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
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
		validator.internalValidateSingleNode(placeholder);

		List<ValidationError> errors = validator.getValidationErrors();
		assertThat("Expect no errors on the happy path ", errors.isEmpty(), is(true));
	}

}