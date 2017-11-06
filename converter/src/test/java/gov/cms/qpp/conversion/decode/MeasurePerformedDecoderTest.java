package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.TestHelper;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static com.google.common.truth.Truth.assertWithMessage;

public class MeasurePerformedDecoderTest {

	private Context context;
	private String xmlFragment;

	@BeforeEach
	void setUp() throws IOException {
		context = new Context();
		xmlFragment = TestHelper.getFixture("MeasurePerformed.xml");
	}

	@Test
	void testMeasurePerformed() throws XmlException {
		Node measurePerformedNode = executeMeasurePerformedDecoder(xmlFragment)
				.findFirstNode(TemplateId.MEASURE_PERFORMED);

		assertValidMeasurePerformed(measurePerformedNode);
	}

	@Test
	void testGarbageXmlIsIgnore() throws XmlException {
		xmlFragment = xmlFragment.replaceAll("<statusCode ",
				"\n<Stuff arbitrary=\"123\">abc<newnode>Some extra stuff</newnode></Stuff>Unexpected text appears here\n\n<statusCode ");

		Node measurePerformedNode = executeMeasurePerformedDecoder(xmlFragment)
				.findFirstNode(TemplateId.MEASURE_PERFORMED);

		assertValidMeasurePerformed(measurePerformedNode);
	}

	private Node executeMeasurePerformedDecoder(String xmlFragment) throws XmlException {
		MeasurePerformedDecoder measurePerformedDecoder = new MeasurePerformedDecoder(context);
		return measurePerformedDecoder.decode(XmlUtils.stringToDom(xmlFragment));
	}

	private void assertValidMeasurePerformed(Node measurePerformedNode) {
		assertWithMessage("Should have a measure perform")
				.that(measurePerformedNode.getValue("measurePerformed"))
				.isEqualTo("Y");
	}
}
