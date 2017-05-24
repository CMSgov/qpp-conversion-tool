package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.List;

/**
 * Encodes either multiple clinical documents based on npi/tin combinations
 * Or encodes one clinical document.
 */
@Encoder(TemplateId.QRDA_CATEGORY_III_REPORT_V3)
public class MultipleTinsEncoder extends QppOutputEncoder {

	/**
	 * Encodes the Clinical Document and repeats it if there are more than one
	 * NPI TIN included in the QRDA III
	 *
	 * @param wrapper object to encode into
	 * @param node object to encode
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) {
		List<Node> npiTinCombinations = node.findNode(MultipleTinsDecoder.NPI_TIN_ID);

		if (npiTinCombinations.size() > 1) {
			npiTinCombinations.forEach(npiTinNode -> encodeNpiTinCombinations(wrapper, node, npiTinNode));
		} else {
			encodeSingleNpiTinCombination(wrapper, node);
		}
	}

	/**
	 * Encodes a new clinical document for each NPI/TIN combination.
	 *
	 * @param wrapper object to be encoded too
	 * @param node object to encode from
	 * @param npiTinNode current npi/tin combination node to encode
	 */
	private void encodeNpiTinCombinations(JsonWrapper wrapper, Node node, Node npiTinNode) {
		JsonWrapper childWrapper = new JsonWrapper();
		JsonOutputEncoder clinicalDocumentEncoder = ENCODERS.get(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		Node clinicalDocumentNode = node.findFirstNode(TemplateId.CLINICAL_DOCUMENT.getTemplateId());

		clinicalDocumentNode.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER,
			npiTinNode.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER));
		clinicalDocumentNode.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER,
				npiTinNode.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER));
		clinicalDocumentEncoder.internalEncode(childWrapper, node);
		wrapper.putObject(childWrapper);
	}

	/**
	 * Encodes a clinical document for a single NPI/TIN
	 *
	 * @param wrapper object to be encoded
	 * @param node object to encode from
	 */
	private void encodeSingleNpiTinCombination(JsonWrapper wrapper, Node node) {
		Node clinicalDocumentNode = node.findFirstNode(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		JsonOutputEncoder clinicalDocumentEncoder = ENCODERS.get(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentEncoder.internalEncode(wrapper, clinicalDocumentNode);
	}
}
