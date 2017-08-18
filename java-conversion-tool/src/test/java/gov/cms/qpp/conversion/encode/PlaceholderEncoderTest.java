package gov.cms.qpp.conversion.encode;


import gov.cms.qpp.ConverterTestHelper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PlaceholderEncoderTest {

	@Test
	public void encodePlaceholderNodeNegative() throws EncodeException {
		//setup
		Node placeHolder = new Node(TemplateId.PLACEHOLDER);
		placeHolder.addChildNode(new Node());
		JsonWrapper wrapper = new JsonWrapper();
		PlaceholderEncoder encoder = new PlaceholderEncoder(ConverterTestHelper.newMockConverter());

		//when
		encoder.internalEncode(wrapper, placeHolder);

		//then
		assertThat(encoder.getDetails().size(), is(1));
	}
}
