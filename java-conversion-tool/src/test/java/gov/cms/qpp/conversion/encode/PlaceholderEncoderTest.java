package gov.cms.qpp.conversion.encode;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

public class PlaceholderEncoderTest {

	@Test
	public void encodePlaceholderNodeNegative() throws EncodeException {
		//setup
		Node placeHolder = new Node(TemplateId.PLACEHOLDER);
		placeHolder.addChildNode(new Node());
		JsonWrapper wrapper = new JsonWrapper();
		PlaceholderEncoder encoder = new PlaceholderEncoder();

		//when
		encoder.internalEncode(wrapper, placeHolder);

		//then
		assertThat(encoder.getDetails().size(), is(1));
	}
}
