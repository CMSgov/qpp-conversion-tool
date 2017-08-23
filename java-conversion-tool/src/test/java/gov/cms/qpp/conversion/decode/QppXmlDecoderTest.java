package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.jdom2.Element;
import org.junit.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;

public class QppXmlDecoderTest {

	private static boolean errorDecode = false;

	@Test
	public void decodeResultNoAction() throws Exception {
		assertThat("DecodeResult is incorrect",
				new QppXmlDecoder(new Context()).internalDecode(null, null),
				is(DecodeResult.NO_ACTION));
	}

	@Test
	public void nullElementDecodeReturnsError() {
		// Element nullElement = null;
		assertThat("DecodeResult is incorrect",
				new QppXmlDecoder(new Context()).decode((Element) null, null),
				is(DecodeResult.ERROR));
	}

	@Test
	public void decodeInvalidChildReturnsError() {
		Context context = new Context();
		context.getRegistry(Decoder.class, XmlInputDecoder.class).register(new ComponentKey(TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS, Program.ALL), TestChildDecodeError.class);
		context.getRegistry(Decoder.class, XmlInputDecoder.class).register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), TestChildNoAction.class);

		Element testElement = new Element("testElement");
		Element testChildElement = new Element("templateId");
		testChildElement.setAttribute("root", TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS.getRoot());

		testElement.getChildren().add(testChildElement);
		Node testNode = new Node();

		QppXmlDecoder objectUnderTest = new DefaultQppXmlDecoder(context);
		objectUnderTest.decode(testElement, testNode);

		assertThat("Child Node was not encountered", errorDecode, is(true));
	}

	@Test
	public void testChildDecodeErrorResultTest() throws Exception {
		DecodeResult result = DecodeResult.ERROR;
		DecodeResult returnValue = runTestChildDecodeResult(result);
		assertThat("Should get an invalid Decode Result", returnValue, is(nullValue()));

	}

	@Test
	public void testChildDecodeInvalidResultTest() throws Exception {
		DecodeResult result = DecodeResult.NO_ACTION;
		DecodeResult returnValue = runTestChildDecodeResult(result);
		assertThat("Should get an invalid Decode Result", returnValue, is(nullValue()));
	}

	private DecodeResult runTestChildDecodeResult(DecodeResult code) throws Exception {
		QppXmlDecoder objectUnderTest = new QppXmlDecoder(new Context());
		Element childElement = new Element("childElement");
		Node childNode = new Node();
		String methodName = "testChildDecodeResult";
		Method testChildDecodeResult = null;
		Method[] methods = QppXmlDecoder.class.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				testChildDecodeResult = method;
				break;
			}
		}

		testChildDecodeResult.setAccessible(true);
		DecodeResult returnValue = (DecodeResult) testChildDecodeResult.invoke(objectUnderTest, code, childElement,
				childNode);
		return returnValue;
	}

	public static class DefaultQppXmlDecoder extends QppXmlDecoder {
		public DefaultQppXmlDecoder(Context context) {
			super(context);
		}
	}

	public static class TestChildDecodeError extends QppXmlDecoder {

		public TestChildDecodeError(Context context) {
			super(context);
		}

		@Override
		public DecodeResult internalDecode(Element element, Node childNode) {
			errorDecode = true;
			return DecodeResult.ERROR;
		}
	}

	public static class TestChildNoAction extends QppXmlDecoder {

		public TestChildNoAction(Context context) {
			super(context);
		}

		@Override
		public DecodeResult internalDecode(Element element, Node childNode) {
			return DecodeResult.NO_ACTION;
		}
	}
}
