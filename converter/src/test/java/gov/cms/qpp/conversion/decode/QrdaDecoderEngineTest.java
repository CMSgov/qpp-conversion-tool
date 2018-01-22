package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;

import static com.google.common.truth.Truth.assertWithMessage;

class QrdaDecoderEngineTest {

	private static boolean errorDecode = false;

	@Test
	@Ignore
	void decodeInvalidChildReturnsError() {
		Context context = new Context();
		TestHelper.mockDecoder(context, TestChildDecodeError.class, new ComponentKey(TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS, Program.ALL));
		TestHelper.mockDecoder(context, TestChildNoAction.class, new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL));

		Element testElement = new Element("testElement");
		Element testChildElement = new Element("templateId");
		testChildElement.setAttribute("root", TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS.getRoot());

		testElement.getChildren().add(testChildElement);

		QrdaDecoderEngine objectUnderTest = new QrdaDecoderEngine(context);
		objectUnderTest.decode(testElement);

		assertWithMessage("Child Node was not encountered")
				.that(errorDecode)
				.isTrue();
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