package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jdom2.Element;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;

public class AciProportionNumeratorDecoderTest {

	@Test
	public void testInternalDecode() {
		Element element = new Element("testElement");
		Node node = new Node();

		AciProportionNumeratorDecoder aciProportionNumeratorDecoder = new AciProportionNumeratorDecoder();
		DecodeResult decodeResult = aciProportionNumeratorDecoder.internalDecode(element, node);

		assertThat("Must continue on tree", decodeResult, is(DecodeResult.TreeContinue));
		assertThat("", node.getValue("name"), is("aciProportionNumerator"));
	}
}
