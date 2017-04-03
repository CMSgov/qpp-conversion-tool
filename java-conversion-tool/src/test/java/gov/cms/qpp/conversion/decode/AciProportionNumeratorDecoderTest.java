package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import org.jdom2.Element;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AciProportionNumeratorDecoderTest {

	private static final String NUMERATOR_NODE_NAME = "aciProportionNumerator";

	@Test
	public void testInternalDecode() {
		Element element = new Element("testElement");
		Node node = new Node();

		AciProportionNumeratorDecoder aciProportionNumeratorDecoder = new AciProportionNumeratorDecoder();
		DecodeResult decodeResult = aciProportionNumeratorDecoder.internalDecode(element, node);

		assertThat("Must continue on tree", decodeResult, is(DecodeResult.TREE_CONTINUE));
		assertThat("The node name must be " + NUMERATOR_NODE_NAME, node.getValue("name"), is(NUMERATOR_NODE_NAME));
	}
}
