package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;

import java.lang.reflect.Method;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class QrdaDecoderEngineTest {

	private static boolean errorDecode = false;

	@Test
	void decodeResultNoAction() throws Exception {
		assertThat(new QrdaDecoderEngine(new Context()).internalDecode(null, null))
				.isEqualTo(DecodeResult.NO_ACTION);
	}

	@Test
	void nullElementDecodeReturnsError() {
		// Element nullElement = null;
		assertThat(new QrdaDecoderEngine(new Context()).decode((Element) null, null))
				.isEqualTo(DecodeResult.ERROR);
	}

	@Test
	void decodeInvalidChildReturnsError() {
		Context context = new Context();
		TestHelper.mockDecoder(context, TestChildDecodeError.class, new ComponentKey(TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS, Program.ALL));
		TestHelper.mockDecoder(context, TestChildNoAction.class, new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL));

		Element testElement = new Element("testElement");
		Element testChildElement = new Element("templateId");
		testChildElement.setAttribute("root", TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS.getRoot());

		testElement.getChildren().add(testChildElement);
		Node testNode = new Node();

		QrdaDecoderEngine objectUnderTest = new DefaultQrdaXmlDecoder(context);
		objectUnderTest.decode(testElement, testNode);

		assertWithMessage("Child Node was not encountered")
				.that(errorDecode)
				.isTrue();
	}

	@Test
	void testChildDecodeErrorResultTest() throws Exception {
		DecodeResult result = DecodeResult.ERROR;
		DecodeResult returnValue = runTestChildDecodeResult(result);
		assertThat(returnValue).isNull();

	}

	@Test
	void testChildDecodeInvalidResultTest() throws Exception {
		DecodeResult result = DecodeResult.NO_ACTION;
		DecodeResult returnValue = runTestChildDecodeResult(result);
		assertThat(returnValue).isNull();
	}

	private DecodeResult runTestChildDecodeResult(DecodeResult code) throws Exception {
		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(new Context());
		Element childElement = new Element("childElement");
		Node childNode = new Node();

		String methodName = "testChildDecodeResult";
		Method testChildDecodeResult =
				QrdaDecoderEngine.class.getDeclaredMethod(methodName, DecodeResult.class, Element.class, Node.class);
		testChildDecodeResult.setAccessible(true);

		DecodeResult returnValue = (DecodeResult) testChildDecodeResult.invoke(objectUnderTest, code, childElement,
				childNode);
		return returnValue;
	}

	public static class DefaultQrdaXmlDecoder extends QrdaDecoderEngine {
		public DefaultQrdaXmlDecoder(Context context) {
			super(context);
		}
	}

	public static class TestChildDecodeError extends QrdaDecoder {

		public TestChildDecodeError(Context context) {
			super(context);
		}

		@Override
		public DecodeResult decode(Element element, Node childNode) {
			errorDecode = true;
			return DecodeResult.ERROR;
		}
	}

	public static class TestChildNoAction extends QrdaDecoder {

		public TestChildNoAction(Context context) {
			super(context);
		}

		@Override
		public DecodeResult decode(Element element, Node childNode) {
			return DecodeResult.NO_ACTION;
		}
	}
}