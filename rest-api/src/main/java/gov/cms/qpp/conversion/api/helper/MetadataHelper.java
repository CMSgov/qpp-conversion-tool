package gov.cms.qpp.conversion.api.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;

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
		Objects.requireNonNull(node, "node");

		Metadata metadata = new Metadata();

		metadata.setApm(findApm(node));
		metadata.setTin(findTin(node));
		metadata.setNpi(findNpi(node));
		metadata.setCpc(isCpc(node));

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

		if (found == null) {
			return false;
		}

		return Program.isCpc(found);
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

}
