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
	QualityMeasureIdDecoder objectUnderTest = new QualityMeasureIdDecoder();

	/**
	 * Tests the decoder for a valid xml fragment
	 *
	 * @throws XmlException when parsing xml fragment fails.
	 */
	@Test
	public void internalDecodeValid() throws XmlException {
		QualityMeasureIdDecoder decoder = new QualityMeasureIdDecoder();

		Node qualityMeasureIdNode = new Node();
		Element qualityMeasureIdElement = XmlUtils.stringToDom(getXmlFragmentWithMeasureGuid("Measurement Id Value"));
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
		String xmlFragment = getXmlFragmentWithMeasureGuid("Measurement Id Value").replace("<id ", "<noid ");
		QualityMeasureIdDecoder decoder = new QualityMeasureIdDecoder();

		Node qualityMeasureIdNode = new Node();
		Element qualityMeasureIdElement = XmlUtils.stringToDom(xmlFragment);
		decoder.setNamespace(qualityMeasureIdElement, decoder);
		DecodeResult decodeResult = decoder.internalDecode(qualityMeasureIdElement, qualityMeasureIdNode);

		assertThat("The incorrect DecodeResult was returned.", decodeResult, is(DecodeResult.TREE_CONTINUE));
		String value = qualityMeasureIdNode.getValue("measureId");
		assertThat("Expect to not have a value", value, is(nullValue()));
	}

	@Test
	public void incorrectRoot() throws XmlException {
		//set-up
		Element qualityMeasureIdElement = XmlUtils.stringToDom(getBadXmlFragmentWithIncorrectRoot());
		Node qualityMeasureIdNode = new Node();

		objectUnderTest.setNamespace(qualityMeasureIdElement, objectUnderTest);

		//execute
		DecodeResult decodeResult = objectUnderTest.internalDecode(qualityMeasureIdElement, qualityMeasureIdNode);

		//assert
		assertThat("The incorrect DecodeResult was returned.", decodeResult, is(DecodeResult.TREE_CONTINUE));
		assertThat("The node should not have a value.", qualityMeasureIdNode.getValue("measureId"), is(nullValue()));
	}

	@Test
	public void ignoreStratumMeasure1() throws XmlException {
		ignoreStratumMeasure(getXmlFragmentWithMeasureGuid("40280381-528a-60ff-0152-8e089ed20376"));
	}

	@Test
	public void ignoreStratumMeasure2() throws XmlException {
		ignoreStratumMeasure(getXmlFragmentWithMeasureGuid("40280381-51f0-825b-0152-22b52da917ba"));
	}

	@Test
	public void ignoreStratumMeasure3() throws XmlException {
		ignoreStratumMeasure(getXmlFragmentWithMeasureGuid("40280381-51f0-825b-0152-22b695b217dc"));
	}

	@Test
	public void ignoreStratumMeasure4() throws XmlException {
		ignoreStratumMeasure(getXmlFragmentWithMeasureGuid("40280381-52fc-3a32-0153-1f6962df0f9c"));
	}

	private void ignoreStratumMeasure(String xmlFragment) throws XmlException {
		//set-up
		QualityMeasureIdDecoder decoder = new QualityMeasureIdDecoder();

		Element qualityMeasureIdElement = XmlUtils.stringToDom(xmlFragment);
		Node qualityMeasureIdNode = new Node();

		decoder.setNamespace(qualityMeasureIdElement, decoder);

		//execute
		DecodeResult decodeResult = decoder.internalDecode(qualityMeasureIdElement, qualityMeasureIdNode);

		//assert
		assertThat("The incorrect DecodeResult was returned.", decodeResult, is(DecodeResult.TREE_ESCAPED));
		assertThat("The node should not have a value.", qualityMeasureIdNode.getValue("measureId"), is(nullValue()));
	}

	private String getXmlFragmentWithMeasureGuid(String measureGuid) {
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			       + "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">"
			       + "	<templateId root=\"2.16.840.1.113883.10.20.27.3.17\" extension=\"2016-11-01\"/> "
			       + "	<reference typeCode=\"REFR\"> "
			       + " 	<externalDocument classCode=\"DOC\" moodCode=\"EVN\"> "
			       + " 		<id root=\"2.16.840.1.113883.4.738\" extension=\"" + measureGuid + "\"/> "
			       + "		</externalDocument> "
			       + " </reference> "
			       + " </root>";
	}

	private String getBadXmlFragmentWithIncorrectRoot() {
		return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
			       + "<root xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">"
			       + "	<templateId root=\"2.16.840.1.113883.10.20.27.3.17\" extension=\"2016-11-01\"/> "
			       + "	<reference typeCode=\"REFR\"> "
			       + " 	<externalDocument classCode=\"DOC\" moodCode=\"EVN\"> "
			       + " 		<id root=\"2.16.840.1.113883.4.739\" extension=\"measure GUID\"/> "
			       + "		</externalDocument> "
			       + " </reference> "
			       + " </root>";
	}
}
