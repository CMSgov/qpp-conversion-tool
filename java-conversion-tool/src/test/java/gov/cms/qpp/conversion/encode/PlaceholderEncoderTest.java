package gov.cms.qpp.conversion.encode;


import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PlaceholderEncoderTest {

	@Test
	public void encodePlaceholderNodeNegative() throws EncodeException {
		//setup
		Node placeHolder = new Node(TemplateId.PLACEHOLDER.getTemplateId());
		placeHolder.addChildNode(new Node("meep"));
		JsonWrapper wrapper = new JsonWrapper();
		PlaceholderEncoder encoder = new PlaceholderEncoder();

		//when
		encoder.internalEncode(wrapper, placeHolder);

		//then
		assertThat(encoder.getValidationErrors().size(), is(1));
	}
}
