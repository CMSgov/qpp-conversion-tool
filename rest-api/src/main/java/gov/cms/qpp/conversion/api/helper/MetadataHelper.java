package gov.cms.qpp.conversion.api.helper;

import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;

public class MetadataHelper {

	private MetadataHelper() {
	}

	public static Metadata generateMetadata(Node node) {
		Metadata metadata = new Metadata();

		metadata.setApm(getApm(node));
		metadata.setTin(getTin(node));
		metadata.setNpi(getNpi(node));
		metadata.setCpc(Program.isCpc(node));

		return metadata;
	}

	private static String getApm(Node node) {
		return findValue(node, ClinicalDocumentDecoder.ENTITY_ID);
	}

	private static String getTin(Node node) {
		return findValue(node, MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER);
	}

	private static String getNpi(Node node) {
		return findValue(node, MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER);
	}

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
