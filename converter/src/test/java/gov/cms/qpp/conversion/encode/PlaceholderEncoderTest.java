package gov.cms.qpp.conversion.encode;


import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class PlaceholderEncoderTest {

	@Test
	public void encodePlaceholderNodeNegative() throws EncodeException {
		//setup
		Node placeHolder = new Node(TemplateId.PLACEHOLDER);
		placeHolder.addChildNode(new Node());
		JsonWrapper wrapper = new JsonWrapper();
		PlaceholderEncoder encoder = new PlaceholderEncoder(new Context());

		//when
		encoder.internalEncode(wrapper, placeHolder);

		//then
		assertThat(encoder.getDetails()).hasSize(1);
	}
}
