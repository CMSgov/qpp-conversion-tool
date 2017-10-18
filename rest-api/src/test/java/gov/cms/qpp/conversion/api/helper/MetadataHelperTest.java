package gov.cms.qpp.conversion.api.helper;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import java.util.Arrays;

import static com.google.common.truth.Truth.assertThat;

public class MetadataHelperTest {

	private static final String MOCK_STRING = "some random mock value";

	@Test(expected = NullPointerException.class)
	public void testGenerateMetadataForNullNodeThrowsNullPointerException() {
		MetadataHelper.generateMetadata(null, MetadataHelper.Outcome.SUCCESS);
	}

	@Test(expected = NullPointerException.class)
	public void testGenerateMetadataForNullOutcomeThrowsNullPointerException() {
		MetadataHelper.generateMetadata(new Node(), null);
	}

	@Test
	public void testExtractsCpcProgramType() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "CPCPLUS");

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getCpc()).isTrue();
	}

	@Test
	public void testExtractsCpcProgramTypeFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "CPCPLUS");
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getCpc()).isTrue();
	}

	@Test
	public void testChildLacksCpcPlus() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getCpc()).isFalse();
	}

	@Test
	public void testExtractsApm() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.ENTITY_ID, MOCK_STRING);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getApm()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testExtractsApmFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(ClinicalDocumentDecoder.ENTITY_ID, MOCK_STRING);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getApm()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testChildLacksApm() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getApm()).isNull();
	}

	@Test
	public void testExtractsTin() {
		Node node = new Node();
		node.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, MOCK_STRING);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getTin()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testExtractsTinFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, MOCK_STRING);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getTin()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testChildLacksTin() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getTin()).isNull();
	}

	@Test
	public void testExtractsNpi() {
		Node node = new Node();
		node.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER, MOCK_STRING);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getNpi()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testExtractsNpiFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER, MOCK_STRING);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getNpi()).isEqualTo(MOCK_STRING);
	}

	@Test
	public void testChildLacksNpi() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getNpi()).isNull();
	}

	@Test
	public void testSuccessOutcome() {
		Metadata metadata = MetadataHelper.generateMetadata(new Node(), MetadataHelper.Outcome.SUCCESS);

		assertThat(metadata.getOverallStatus()).isTrue();
		assertThat(metadata.getConversionStatus()).isTrue();
		assertThat(metadata.getValidationStatus()).isTrue();
	}

	@Test
	public void testConversionFailureOutcome() {
		Metadata metadata = MetadataHelper.generateMetadata(new Node(), MetadataHelper.Outcome.CONVERSION_ERROR);

		assertThat(metadata.getOverallStatus()).isFalse();
		assertThat(metadata.getConversionStatus()).isFalse();
		assertThat(metadata.getValidationStatus()).isFalse();
	}

	@Test
	public void testValidationFailureOutcome() {
		Metadata metadata = MetadataHelper.generateMetadata(new Node(), MetadataHelper.Outcome.VALIDATION_ERROR);

		assertThat(metadata.getOverallStatus()).isFalse();
		assertThat(metadata.getConversionStatus()).isTrue();
		assertThat(metadata.getValidationStatus()).isFalse();
	}

	@Test
	public void testOutcomeValueOf() {
		Arrays.stream(MetadataHelper.Outcome.values())
				.forEach(outcome -> {
					assertThat(outcome).isSameAs(MetadataHelper.Outcome.valueOf(outcome.name()));
				});
	}

}
