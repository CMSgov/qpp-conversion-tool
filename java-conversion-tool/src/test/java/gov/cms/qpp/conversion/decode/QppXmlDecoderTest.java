package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.AnnotationMockHelper;
import gov.cms.qpp.conversion.model.Node;
import org.jdom2.Element;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QppXmlDecoderTest extends QppXmlDecoder {

	private static boolean errorDecode = false;

	@Test
	public void decodeResultNoAction() throws Exception {
		assertThat("DecodeResult is incorrect", new QppXmlDecoder().internalDecode(null, null),
				is(DecodeResult.NO_ACTION));
	}

	@Test
	public void nullElementDecodeReturnsError() {
		//Element nullElement = null;
		assertThat("DecodeResult is incorrect", new QppXmlDecoder().decode((Element)null, null),
				is(DecodeResult.ERROR));
	}

	@Test
	public void decodeInvalidChildReturnsError() {
		AnnotationMockHelper.mockDecoder("errorDecoder", TestChildDecodeError.class);
		AnnotationMockHelper.mockDecoder("noActionDecoder", TestChildNoAction.class);

		Element testElement = new Element("testElement");
		Element testChildElement = new Element("templateId");
		testChildElement.setAttribute("root", "errorDecoder");

		testElement.getChildren().add(testChildElement);
		Node testNode = new Node();

		QppXmlDecoder objectUnderTest = new QppXmlDecoderTest();
		objectUnderTest.decode(testElement, testNode);

		//List<String> validations = objectUnderTest.getValidationsById("errorDecoder");

		assertThat("Child Node was not encountered" , errorDecode, is(true));
	}
	
	public static class TestChildDecodeError extends QppXmlDecoder {

		@Override
		public DecodeResult internalDecode(Element element, Node childNode) {
			errorDecode = true;
			return DecodeResult.ERROR;
		}
	}

	public static class TestChildNoAction extends QppXmlDecoder {

		@Override
		public DecodeResult internalDecode(Element element, Node childNode) {
			return DecodeResult.NO_ACTION;
		}
	}
}
