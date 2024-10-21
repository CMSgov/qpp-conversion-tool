package gov.cms.qpp.conversion.api.helper;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static gov.cms.qpp.conversion.model.Constants.*;

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
	 * @param node from which to extract metadata
	 * @param outcome to update with metadata
	 * @return metadata
	 */
	public static Metadata generateMetadata(Node node, Outcome outcome) {
		Objects.requireNonNull(outcome, "outcome");

		Metadata metadata = Metadata.create();

		if (node != null) {
			metadata.setTin(findTin(node));
			metadata.setNpi(findNpi(node));
			metadata.setProgramName(findProgramName(node));
			metadata.setApm(findApm(node));
			if (isPcf(node)) {
				metadata.setPcf(deriveHash(Constants.PCF_DYNAMO_PARTITION_START));
			}
			metadata.setCpcProcessed(false);
			metadata.setRtiProcessed(false);
		}

		outcome.setStatus(metadata);

		return metadata;
	}

	/**
	 * Retrieves the random hash for the CPC/PCF field if this is a CPC+ or PCF conversion respectively.
	 *
	 * @param partitionStart partition type to use
	 * @return Cpc field randomly hashed or null if this isn't a CPC+ or PCF conversion
	 */
	private static String deriveHash(String partitionStart) {
		return partitionStart + RANDOM_HASH.nextInt(Constants.CPC_DYNAMO_PARTITIONS);
	}

	/**
	 * Retrieves the APM Entity Id from the given node
	 *
	 * @param node to interrogate
	 * @return Apm Entity ID value
	 */
	private static String findApm(Node node) {
		if (isPcf(node)) {
			return findValue(node, PCF_ENTITY_ID, TemplateId.CLINICAL_DOCUMENT);
		} else {
			return findValue(node, PRACTICE_ID, TemplateId.CLINICAL_DOCUMENT);
		}
	}

	/**
	 * Retrieves the Taxpayer Identification Number from the given node
	 *
	 * @param node to interrogate
	 * @return TIN value
	 */
	private static String findTin(Node node) {
		return findValue(node, TAX_PAYER_IDENTIFICATION_NUMBER,
				TemplateId.CLINICAL_DOCUMENT);
	}

	/**
	 * Retrieves the National Provider Identifier from the given node
	 *
	 * @param node to interrogate
	 * @return NPI value
	 */
	private static String findNpi(Node node) {
		return findValue(node, NATIONAL_PROVIDER_IDENTIFIER,
				TemplateId.CLINICAL_DOCUMENT);
	}

	private static String findProgramName(Node node) {
		return findValue(node, PROGRAM_NAME,
				TemplateId.CLINICAL_DOCUMENT);
	}

	private static boolean isPcf(Node node) {
		if (Program.isPcf(node)) {
			return true;
		}

		Node found = findPossibleChildNode(node, RAW_PROGRAM_NAME,
			TemplateId.CLINICAL_DOCUMENT);

		return found != null && Program.isPcf(found);
	}

	/**
	 * Recursively searches a node for a value.
	 *
	 * @param node to interrogate
	 * @param key value name
	 * @param possibleLocations where the value might reside
	 * @return the found value or null
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
		protected void setStatus(Metadata metadata) {
			metadata.setOverallStatus(overall);
			metadata.setConversionStatus(conversion);
			metadata.setValidationStatus(validation);
		}
	}

}
