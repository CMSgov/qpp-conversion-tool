package gov.cms.qpp.conversion.api.helper;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;

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
	 * @return
	 */
	public static Metadata generateMetadata(Node node) {
		Metadata metadata = new Metadata();

		metadata.setApm(findApm(node));
		metadata.setTin(findTin(node));
		metadata.setNpi(findNpi(node));
		metadata.setCpc(Program.isCpc(node));

		return metadata;
	}

	private static String findApm(Node node) {
		return findValue(node, ClinicalDocumentDecoder.ENTITY_ID);
	}

	private static String findTin(Node node) {
		return findValue(node, MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER);
	}

	private static String findNpi(Node node) {
		return findValue(node, MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER);
	}

	/**
	 * Recursively searches a node for a value.
	 *
	 * @param node
	 * @param key
	 * @return
	 */
	private static String findValue(Node node, String key) {
		String value = node.getValue(key);
		if (value != null) {
			return value;
		}

		return node.getChildNodes(child -> child.hasValue(key))
				.findFirst()
				.map(child -> child.getValue(key))
				.orElse(null);
	}

}
