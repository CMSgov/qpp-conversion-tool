package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.jdom2.Element;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static gov.cms.qpp.conversion.decode.MultipleTinsDecoder.NPI_TIN_ID;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test class for MultipleTinsDecoder
 */
public class MultipleTinsDecoderTest extends BaseTest {
	private final String TEST_NPI1 = "NPI-1";
	private final String TEST_NPI2 = "NPI-2";
	private final String TEST_NPI3 = "NPI-3";
	private final String TEST_TIN1 = "TIN-1";
	private final String TEST_TIN2 = "TIN-2";
	private final String TEST_TIN3 = "TIN-3";

	@Test
	public void internalDecode() throws Exception {
		Element multipleTinsElement = makeTestElement();
		List<Node> children = getTestChildren(multipleTinsElement);
		assertThat("Expect that there are four children", children, hasSize(4));
		int matches = 0;
		String value = null;
		for (Node child : children) {
			if (child.getId().equals(NPI_TIN_ID)) {
				value = child.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER);
				if (TEST_TIN1.equals(value) ||
						TEST_TIN2.equals(value) ||
						TEST_TIN3.equals(value)) {
					matches++;
				}
			}
			if (child.getType().getTemplateId().equals(TemplateId.CLINICAL_DOCUMENT.getTemplateId())) {
				matches++;
			}
		}
		// Assert that one child TIN is TIN-1
		// Assert that one child TIN is TIN-2
		// Assert that one child TIN is TIN-3
		// Assert that one child is ClinicalDocument
		assertThat("The correct children were decoded", matches, is(4));
	}

	@Test
	public void testNullNPI() throws Exception {

		Element multipleTinsElement = makeTestElementMissingNPI();
		List<Node> children = getTestChildren(multipleTinsElement);
		assertThat("Expect that there are three children", children, hasSize(3));
		int matches = 0;
		for (Node child : children) {
			if (child.getId().equals(NPI_TIN_ID)) {
//				if ( TESTTIN1.equals(child.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))){
//					matches++;
//				}
				if (TEST_TIN2.equals(child.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))) {
					matches++;
				}
				if (TEST_TIN3.equals(child.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))) {
					matches++;
				}
			}
			if (child.getType().getTemplateId().equals(TemplateId.CLINICAL_DOCUMENT.getTemplateId())) {
				matches++;
			}
		}
		// Assert that one child NPI is test1
		// Assert that one child NPI is test2
		// Assert that one child is ClinicalDocument
		assertThat("The correct children were decoded", matches, is(3));
	}

	@Test
	public void testNullTIN() throws Exception {
		Element multipleTinsElement = makeTestElementMissingTIN();
		List<Node> children = getTestChildren(multipleTinsElement);
		assertThat("Expect that there are three children", children, hasSize(3));
		int matches = 0;
		for (Node child : children) {
			if (child.getId().equals(NPI_TIN_ID)) {
//				if ( TESTTIN1.equals(child.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))){
//					matches++;
//				}
				if (TEST_TIN2.equals(child.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))) {
					matches++;
				}
				if (TEST_TIN3.equals(child.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))) {
					matches++;
				}
			}
			if (child.getType().getTemplateId().equals(TemplateId.CLINICAL_DOCUMENT.getTemplateId())) {
				matches++;
			}
		}
		// Assert that one child NPI is test1
		// Assert that one child NPI is test2
		// Assert that one child is ClinicalDocument
		assertThat("The correct children were decoded", matches, is(3));
	}

	@Test
	public void testNullTINID() throws Exception {
		Element multipleTinsElement = makeTestElementMissingTINID();
		List<Node> children = getTestChildren(multipleTinsElement);
		assertThat("Expect that there are three children", children, hasSize(3));
	}

	@Test
	public void testNullNPIID() throws Exception {
		Element multipleTinsElement = makeTestElementMissingNPIID();
		List<Node> children = getTestChildren(multipleTinsElement);
		assertThat("Expect that there are three children", children, hasSize(3));
	}

	@Test
	public void testNullTaxEl() throws Exception {
		Element multipleTinsElement = makeTestElementMissingTaxEl();
		List<Node> children = getTestChildren(multipleTinsElement);
		assertThat("Expect that there are three children", children, hasSize(3));
	}

	private List<Node> getTestChildren(Element multipleTinsElement) {
		Node mulipleTinsNode = new Node();
		MultipleTinsDecoder decoder = new MultipleTinsDecoder();
		decoder.setNamespace(multipleTinsElement, decoder);
		decoder.internalDecode(multipleTinsElement, mulipleTinsNode);
		return mulipleTinsNode.getChildNodes();
	}

	private Element makeTestElement() throws IOException, XmlException {
		String xmlFragment = getFixture("multiTinMinimal.xml");
		Element ele = XmlUtils.stringToDom(xmlFragment);
		return ele;
	}

	private Element makeTestElementMissingNPI() throws IOException, XmlException {
		String xmlFragment = getFixture("multiTinMinimal.xml");
		xmlFragment = xmlFragment.replace("<id root=\"2.16.840.1.113883.4.6\" extension=\"NPI-1\"/>", "");
		Element ele = XmlUtils.stringToDom(xmlFragment);
		return ele;
	}

	private Element makeTestElementMissingTIN() throws IOException, XmlException {
		String xmlFragment = getFixture("multiTinMinimal.xml");
		xmlFragment = xmlFragment.replace("<id root=\"2.16.840.1.113883.4.2\" extension=\"TIN-1\"/>", "");
		Element ele = XmlUtils.stringToDom(xmlFragment);
		return ele;
	}

	private Element makeTestElementMissingTaxEl() throws IOException, XmlException {
		String xmlFragment = getFixture("multiTinMinimal.xml");
		xmlFragment = xmlFragment.replaceFirst("<representedOrganization>", "")
				.replaceFirst("</representedOrganization>", "");
		Element ele = XmlUtils.stringToDom(xmlFragment);
		return ele;
	}

	private Element makeTestElementMissingNPIID() throws IOException, XmlException {
		String xmlFragment = getFixture("multiTinMinimal.xml");
		xmlFragment = xmlFragment.replace("extension=\"NPI-1\"", "");
		Element ele = XmlUtils.stringToDom(xmlFragment);
		return ele;
	}

	private Element makeTestElementMissingTINID() throws IOException, XmlException {
		String xmlFragment = getFixture("multiTinMinimal.xml");
		xmlFragment = xmlFragment.replace("extension=\"TIN-1\"", "");
		Element ele = XmlUtils.stringToDom(xmlFragment);
		return ele;
	}
}