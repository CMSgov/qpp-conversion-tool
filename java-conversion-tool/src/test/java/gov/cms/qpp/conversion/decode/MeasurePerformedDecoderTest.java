package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.ConversionTestSuite;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class MeasurePerformedDecoderTest extends ConversionTestSuite {
	String xmlFragment;

	@Before
	public void setUp() throws IOException {
		xmlFragment = getFixture("MeasurePerformed.xml");
	}

	@Test
	public void testMeasurePerformed() throws XmlException {
		Node measurePerformedNode = executeMeasurePerformedDecoder(xmlFragment)
				.findFirstNode(TemplateId.MEASURE_PERFORMED);

		assertValidMeasurePerformed(measurePerformedNode);
	}

	@Test
	public void testGarbageXmlIsIgnore() throws XmlException {
		xmlFragment = xmlFragment.replaceAll("<statusCode ",
				"\n<Stuff arbitrary=\"123\">abc<newnode>Some extra stuff</newnode></Stuff>Unexpected text appears here\n\n<statusCode ");

		Node measurePerformedNode = executeMeasurePerformedDecoder(xmlFragment)
				.findFirstNode(TemplateId.MEASURE_PERFORMED);

		assertValidMeasurePerformed(measurePerformedNode);
	}

	private Node executeMeasurePerformedDecoder(String xmlFragment) throws XmlException {
		MeasurePerformedDecoder measurePerformedDecoder = new MeasurePerformedDecoder();
		return measurePerformedDecoder.decode(XmlUtils.stringToDom(xmlFragment));
	}

	private void assertValidMeasurePerformed(Node measurePerformedNode) {
		assertThat("Should have a measure perform",
				measurePerformedNode.getValue("measurePerformed"), is("Y"));
	}
}
