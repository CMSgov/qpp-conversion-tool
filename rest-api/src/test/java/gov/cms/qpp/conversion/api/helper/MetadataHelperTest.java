package gov.cms.qpp.conversion.api.helper;

import org.junit.Test;

import com.google.common.truth.Truth;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

public class MetadataHelperTest {

	private static final String MOCK_STRING = "some random mock value";

	@Test(expected = NullPointerException.class)
	public void testGenerateMetadataForNullNodeThrowsNullPointerException() {
		MetadataHelper.generateMetadata(null);
	}

	@Test
	public void testExtractsCpcProgramType() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "CPCPLUS");

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getCpc()).isTrue();
	}

	@Test
	public void testExtractsCpcProgramTypeFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "CPCPLUS");
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getCpc()).isTrue();
	}

	@Test
	public void testChildLacksCpcPlus() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getCpc()).isFalse();
	}

	@Test
	public void testExtractsApm() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.ENTITY_ID, MOCK_STRING);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getApm()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testExtractsApmFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(ClinicalDocumentDecoder.ENTITY_ID, MOCK_STRING);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getApm()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testChildLacksApm() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getApm()).isNull();
	}

	@Test
	public void testExtractsTin() {
		Node node = new Node();
		node.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, MOCK_STRING);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getTin()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testExtractsTinFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, MOCK_STRING);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getTin()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testChildLacksTin() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getTin()).isNull();
	}

	@Test
	public void testExtractsNpi() {
		Node node = new Node();
		node.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER, MOCK_STRING);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getNpi()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testExtractsNpiFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER, MOCK_STRING);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getNpi()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testChildLacksNpi() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node);
		Truth.assertThat(metadata.getNpi()).isNull();
	}

}
