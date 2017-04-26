package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.jdom2.Element;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test for the QualityMeasureIdDecoder
 */
public class QualityMeasureIdDecoderTest {
	/**
	 * Tests the decoder for a valid xml fragment
	 *
	 * @throws XmlException when parsing xml fragment fails.
	 */
	@Test
	public void internalDecodeValid() throws XmlException {
		QualityMeasureIdDecoder decoder = new QualityMeasureIdDecoder();

		Node qualityMeasureIdNode = new Node();
		Element qualityMeasureIdElement = XmlUtils.stringToDom(getXmlFragment());
		decoder.setNamespace(qualityMeasureIdElement, decoder);
		decoder.internalDecode(qualityMeasureIdElement, qualityMeasureIdNode);

		String value = qualityMeasureIdNode.getValue("measureId");
		assertThat("Expect to have a value", value, is("Measurement Id Value"));
	}

	/**
	 * Tests when the xml is missing the id node
	 *
	 * @throws XmlException when the xml fragment is not well formed
	 */
	@Test
	public void internalDecodeMissingId() throws XmlException {
		String xmlFragment = getXmlFragment().replace("<id ", "<noid ");
		QualityMeasureIdDecoder decoder = new QualityMeasureIdDecoder();

		Node qualityMeasureIdNode = new Node();
		Element qualityMeasureIdElement = XmlUtils.stringToDom(xmlFragment);
		decoder.setNamespace(qualityMeasureIdElement, decoder);
		decoder.internalDecode(qualityMeasureIdElement, qualityMeasureIdNode);

		String value = qualityMeasureIdNode.getValue("measureId");
		assertThat("Expect to not have a value", value, is(nullValue()));
	}


	private String getXmlFragment() {
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">"
				+ "	<templateId root=\"2.16.840.1.113883.10.20.27.3.17\" extension=\"2016-11-01\"/> "
				+ "	<reference typeCode=\"REFR\"> "
				+ " 	<externalDocument classCode=\"DOC\" moodCode=\"EVN\"> "
				+ " 		<id root=\"2.16.840.1.113883.4.738\" extension=\"Measurement Id Value\"/> "
				+ "		</externalDocument> "
				+ " </reference> "
				+ " </root>";
	}

}