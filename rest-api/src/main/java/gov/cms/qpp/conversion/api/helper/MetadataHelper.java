package gov.cms.qpp.conversion.api.helper;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Utilities for working with Metadata beans
 */
public class MetadataHelper {

	private MetadataHelper() {
	}

	/**
	 * Generates a {@link Metadata} object from a {@link Node}.
	 * This {@link Metadata} does not contain data not found in a standard {@link Node}.
	 *
	 * @param node
	 * @param outcome
	 * @return
	 */
	public static Metadata generateMetadata(Node node, Outcome outcome) {
		Objects.requireNonNull(node, "node");
		Objects.requireNonNull(outcome, "outcome");

		Metadata metadata = new Metadata();

		metadata.setApm(findApm(node));
		metadata.setTin(findTin(node));
		metadata.setNpi(findNpi(node));
		metadata.setCpc(isCpc(node));
		metadata.setCpcProcessed(false);
		outcome.setStatus(metadata);

		return metadata;
	}

	private static String findApm(Node node) {
		return findValue(node, ClinicalDocumentDecoder.ENTITY_ID, TemplateId.CLINICAL_DOCUMENT);
	}

	private static String findTin(Node node) {
		return findValue(node, MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER,
				TemplateId.QRDA_CATEGORY_III_REPORT_V3, TemplateId.CLINICAL_DOCUMENT);
	}

	private static String findNpi(Node node) {
		return findValue(node, MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER,
				TemplateId.QRDA_CATEGORY_III_REPORT_V3, TemplateId.CLINICAL_DOCUMENT);
	}

	private static boolean isCpc(Node node) {
		if (Program.isCpc(node)) {
			return true;
		}

		Node found = findPossibleChildNode(node, ClinicalDocumentDecoder.PROGRAM_NAME,
						TemplateId.CLINICAL_DOCUMENT, TemplateId.QRDA_CATEGORY_III_REPORT_V3);

		return found != null && Program.isCpc(found);
	}

	/**
	 * Recursively searches a node for a value.
	 *
	 * @param node
	 * @param key
	 * @param possibleLocations
	 * @return
	 */
	private static String findValue(Node node, String key, TemplateId... possibleLocations) {
		String value = node.getValue(key);
		if (value != null) {
			return value;
		}

		Node found = findPossibleChildNode(node, key, possibleLocations);
		return found == null ? null : found.getValue(key);
	}

	private static Node findPossibleChildNode(Node node, String key, TemplateId... possibleLocations) {
		return Arrays.stream(possibleLocations)
			.distinct()
			.map(node::findNode)
			.flatMap(List::stream)
			.filter(child -> child.hasValue(key))
			.findFirst()
			.orElse(null);
	}

	/**
	 * Potential states of conversion outcomes.
	 */
	public enum Outcome {
		SUCCESS(true, true, true),
		CONVERSION_ERROR(false, false, false),
		VALIDATION_ERROR(false, true, false);

		private boolean overall;
		private boolean conversion;
		private boolean validation;

		Outcome(boolean overall, boolean conversion, boolean validation) {
			this.overall = overall;
			this.conversion = conversion;
			this.validation = validation;
		}

		/**
		 * Set status of conversion on {@link Metadata} instance
		 *
		 * @param metadata to update
		 */
		private void setStatus(Metadata metadata) {
			metadata.setOverallStatus(overall);
			metadata.setConversionStatus(conversion);
			metadata.setValidationStatus(validation);
		}
	}

}
