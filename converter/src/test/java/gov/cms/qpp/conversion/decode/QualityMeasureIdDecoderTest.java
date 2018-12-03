package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

import static com.google.common.truth.Truth.assertThat;

/**
 * Test for the QualityMeasureIdDecoder
 */
class QualityMeasureIdDecoderTest {
	private QualityMeasureIdDecoder objectUnderTest;

	@BeforeEach
	void setup() {
		objectUnderTest = new QualityMeasureIdDecoder(new Context());
	}

	/**
	 * Tests the decoder for a valid xml fragment
	 *
	 * @throws XmlException when parsing xml fragment fails.
	 */
	@Test
	void internalDecodeValid() throws XmlException {
		Node qualityMeasureIdNode = new Node();
		Element qualityMeasureIdElement = XmlUtils.stringToDom(getXmlFragmentWithMeasureGuid("Measurement Id Value"));
		objectUnderTest.setNamespace(qualityMeasureIdElement.getNamespace());
		objectUnderTest.decode(qualityMeasureIdElement, qualityMeasureIdNode);

		String value = qualityMeasureIdNode.getValue("measureId");
		assertThat(value).isEqualTo("measurement id value");
	}

	/**
	 * Tests when the xml is missing the id node
	 *
	 * @throws XmlException when the xml fragment is not well formed
	 */
	@Test
	void internalDecodeMissingId() throws XmlException {
		String xmlFragment = getXmlFragmentWithMeasureGuid("Measurement Id Value").replace("<id ", "<noid ");

		Node qualityMeasureIdNode = new Node();
		Element qualityMeasureIdElement = XmlUtils.stringToDom(xmlFragment);
		objectUnderTest.setNamespace(qualityMeasureIdElement.getNamespace());
		DecodeResult decodeResult = objectUnderTest.decode(qualityMeasureIdElement, qualityMeasureIdNode);

		assertThat(decodeResult).isEquivalentAccordingToCompareTo(DecodeResult.TREE_CONTINUE);
		String value = qualityMeasureIdNode.getValue("measureId");
		assertThat(value).isNull();
	}

	@Test
	void incorrectRoot() throws XmlException {
		//set-up
		Element qualityMeasureIdElement = XmlUtils.stringToDom(getBadXmlFragmentWithIncorrectRoot());
		Node qualityMeasureIdNode = new Node();

		objectUnderTest.setNamespace(qualityMeasureIdElement.getNamespace());

		//execute
		DecodeResult decodeResult = objectUnderTest.decode(qualityMeasureIdElement, qualityMeasureIdNode);

		//assert
		assertThat(decodeResult).isEqualTo(DecodeResult.TREE_CONTINUE);
		assertThat(qualityMeasureIdNode.getValue("measureId")).isNull();
	}

	@Test
	void dontIgnoreStratumMeasure() throws XmlException {
		//set-up
		String nonIgnorableGuid = "40280381-528a-60ff-0152-8e089ed20376";
		Element qualityMeasureIdElement = XmlUtils.stringToDom(getXmlFragmentWithMeasureGuid(nonIgnorableGuid));

		Node qualityMeasureIdNode = new Node();

		objectUnderTest.setNamespace(qualityMeasureIdElement.getNamespace());

		//execute
		DecodeResult decodeResult = objectUnderTest.decode(qualityMeasureIdElement, qualityMeasureIdNode);

		//assert
		assertThat(decodeResult)
				.isEqualTo(DecodeResult.TREE_CONTINUE);
		String value = qualityMeasureIdNode.getValue("measureId");
		assertThat(value)
				.isEqualTo(nonIgnorableGuid);
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
