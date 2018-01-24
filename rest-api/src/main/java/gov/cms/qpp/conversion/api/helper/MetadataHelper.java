package gov.cms.qpp.conversion.api.helper;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities for working with Metadata beans
 */
public class MetadataHelper {

	private static final ThreadLocalRandom RANDOM_HASH = ThreadLocalRandom.current();

	/**
	 * No need for constructor in this utility class
	 */
	private MetadataHelper() {
		//empty
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
		Objects.requireNonNull(outcome, "outcome");

		Metadata metadata = new Metadata();

		if (node != null) {
			metadata.setApm(findApm(node));
			metadata.setTin(findTin(node));
			metadata.setNpi(findNpi(node));
			metadata.setCpc(deriveCpcHash(node));
			metadata.setCpcProcessed(false);
		}

		outcome.setStatus(metadata);

		return metadata;
	}

	/**
	 * Retrieves the random hash for the Cpc field if this is a CPC+ conversion.
	 *
	 * @return Cpc field randomly hashed or null if this isn't a CPC+ conversion
	 */
	private static String deriveCpcHash(Node node) {
		String cpcHash = null;

		if (isCpc(node)) {
			cpcHash = Constants.CPC_DYNAMO_PARTITION_START + RANDOM_HASH.nextInt(Constants.CPC_DYNAMO_PARTITIONS);
		}

		return cpcHash;
	}

	/**
	 * Retrieves the APM Entity Id from the given node
	 *
	 * @return Apm Entity ID value
	 */
	private static String findApm(Node node) {
		return findValue(node, ClinicalDocumentDecoder.ENTITY_ID, TemplateId.CLINICAL_DOCUMENT);
	}

	/**
	 * Retrieves the Taxpayer Identification Number from the given node
	 *
	 * @return TIN value
	 */
	private static String findTin(Node node) {
		return findValue(node, ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER,
				TemplateId.CLINICAL_DOCUMENT);
	}

	/**
	 * Retrieves the National Provider Identifier from the given node
	 *
	 * @return NPI value
	 */
	private static String findNpi(Node node) {
		return findValue(node, ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER,
				TemplateId.CLINICAL_DOCUMENT);
	}

	/**
	 * Retrieves the random hash for CPC Field
	 *
	 * @return Cpc field randomly hashed
	 */
	private static boolean isCpc(Node node) {
		if (Program.isCpc(node)) {
			return true;
		}

		Node found = findPossibleChildNode(node, ClinicalDocumentDecoder.PROGRAM_NAME,
						TemplateId.CLINICAL_DOCUMENT);

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

	/**
	 * Finds all possible children within the given node for each {@link TemplateId} given
	 * filtered by children with specific keys
	 *
	 * @param node Object to search through
	 * @param key value to filter
	 * @param possibleLocations areas which the child can exist
	 * @return A child node with the correct value or null
	 */
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
