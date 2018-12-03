package gov.cms.qpp.conversion.api.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.api.helper.MetadataHelper.Outcome;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.test.enums.EnumContract;
import gov.cms.qpp.test.helper.HelperContract;

import static com.google.common.truth.Truth.assertThat;

class MetadataHelperTest implements HelperContract {

	private static final String MOCK_STRING = "some random mock value";

	@Test
	void testGenerateMetadataForNullNodeReturnsSkinnyMetadata() {
		MetadataHelper.Outcome outcome = MetadataHelper.Outcome.VALIDATION_ERROR;
		Metadata comparison = new Metadata();
		comparison.setOverallStatus(false);
		comparison.setConversionStatus(true);
		comparison.setValidationStatus(false);

		Metadata metadata = MetadataHelper.generateMetadata(null, outcome);
		metadata.setCreatedDate(comparison.getCreatedDate());

		assertThat(metadata).isEqualTo(comparison);
	}

	@Test
	void testGenerateMetadataForNullOutcomeThrowsNullPointerException() {
		Assertions.assertThrows(NullPointerException.class, () ->
			MetadataHelper.generateMetadata(new Node(), null));
	}

	@Test
	void testExtractsCpcProgramType() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "CPCPLUS");

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getCpc()).startsWith(Constants.CPC_DYNAMO_PARTITION_START);
	}

	@Test
	void testExtractsCpcProgramTypeFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "CPCPLUS");
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getCpc()).startsWith(Constants.CPC_DYNAMO_PARTITION_START);
	}

	@Test
	void testChildLacksCpcPlus() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getCpc()).isNull();
	}

	@Test
	void testExtractsApm() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.ENTITY_ID, MOCK_STRING);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getApm()).isEqualTo(MOCK_STRING);
	}

	@Test
	void testExtractsApmFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(ClinicalDocumentDecoder.ENTITY_ID, MOCK_STRING);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getApm()).isEqualTo(MOCK_STRING);
	}

	@Test
	void testChildLacksApm() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getApm()).isNull();
	}

	@Test
	void testExtractsTin() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, MOCK_STRING);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getTin()).isEqualTo(MOCK_STRING);
	}

	@Test
	void testExtractsTinFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, MOCK_STRING);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getTin()).isEqualTo(MOCK_STRING);
	}

	@Test
	void testChildLacksTin() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getTin()).isNull();
	}

	@Test
	void testExtractsNpi() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER, MOCK_STRING);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getNpi()).isEqualTo(MOCK_STRING);
	}

	@Test
	void testExtractsNpiFromChild() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		child.putValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER, MOCK_STRING);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getNpi()).isEqualTo(MOCK_STRING);
	}

	@Test
	void testChildLacksNpi() {
		Node node = new Node();
		Node child = new Node();
		child.setType(TemplateId.CLINICAL_DOCUMENT);
		node.addChildNode(child);

		Metadata metadata = MetadataHelper.generateMetadata(node, MetadataHelper.Outcome.SUCCESS);
		assertThat(metadata.getNpi()).isNull();
	}

	@Test
	void testSuccessOutcome() {
		Metadata metadata = MetadataHelper.generateMetadata(new Node(), MetadataHelper.Outcome.SUCCESS);

		assertThat(metadata.getOverallStatus()).isTrue();
		assertThat(metadata.getConversionStatus()).isTrue();
		assertThat(metadata.getValidationStatus()).isTrue();
	}

	@Test
	void testConversionFailureOutcome() {
		Metadata metadata = MetadataHelper.generateMetadata(new Node(), MetadataHelper.Outcome.CONVERSION_ERROR);

		assertThat(metadata.getOverallStatus()).isFalse();
		assertThat(metadata.getConversionStatus()).isFalse();
		assertThat(metadata.getValidationStatus()).isFalse();
	}

	@Test
	void testValidationFailureOutcome() {
		Metadata metadata = MetadataHelper.generateMetadata(new Node(), MetadataHelper.Outcome.VALIDATION_ERROR);

		assertThat(metadata.getOverallStatus()).isFalse();
		assertThat(metadata.getConversionStatus()).isTrue();
		assertThat(metadata.getValidationStatus()).isFalse();
	}

	@Nested
	static class OutcomeTest implements EnumContract {

		@Override
		public Class<? extends Enum<?>> getEnumType() {
			return Outcome.class;
		}

	}

	@Override
	public Class<?> getHelperClass() {
		return MetadataHelper.class;
	}

}
