package gov.cms.qpp.conversion.encode;

import java.util.List;

import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

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
		List<Node> npiTinCombinations = node.findNode(TemplateId.NPI_TIN_ID);

		if (npiTinCombinations.size() > 1) {
			encodeNpiTinCombinations(wrapper, npiTinCombinations, node);
		} else {
			encodeSingleNpiTinCombination(wrapper, node);
		}
	}

	/**
	 * Creates a new clinical document from a single clinical document encoding for each NPI/TIN combination.
	 *
	 * @param wrapper object to be encoded too
	 * @param npiTinCombinations object holding the of National Provider Identifier/Taxpayer Identifier combinations
	 * @param node object to encode from
	 */
	private void encodeNpiTinCombinations(JsonWrapper wrapper, List<Node> npiTinCombinations, Node node) {
		JsonOutputEncoder clinicalDocumentEncoder = ENCODERS.get(TemplateId.CLINICAL_DOCUMENT);
		Node clinicalDocumentNode = node.findFirstNode(TemplateId.CLINICAL_DOCUMENT);

		JsonWrapper clinicalDocWrapper = new JsonWrapper();
		clinicalDocumentEncoder.internalEncode(clinicalDocWrapper, clinicalDocumentNode);

		npiTinCombinations.forEach(npiTinNode -> {
			JsonWrapper childWrapper = new JsonWrapper(clinicalDocWrapper);
			childWrapper.putString(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER,
					npiTinNode.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER));
			childWrapper.putString(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER,
					npiTinNode.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER));
			wrapper.putObject(childWrapper);
		});
	}

	/**
	 * Encodes a clinical document for a single NPI/TIN
	 *
	 * @param wrapper object to be encoded
	 * @param node object to encode from
	 */
	private void encodeSingleNpiTinCombination(JsonWrapper wrapper, Node node) {
		Node clinicalDocumentNode = node.findFirstNode(TemplateId.CLINICAL_DOCUMENT);
		JsonOutputEncoder clinicalDocumentEncoder = ENCODERS.get(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentEncoder.internalEncode(wrapper, clinicalDocumentNode);
	}
}
