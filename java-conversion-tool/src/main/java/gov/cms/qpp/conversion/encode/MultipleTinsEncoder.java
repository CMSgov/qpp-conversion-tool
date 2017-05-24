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
@Encoder(TemplateId.MULTIPLE_TINS)
public class MultipleTinsEncoder extends QppOutputEncoder {

	/**
	 * Encodes the Clinical Document and repeats it if there are more than one
	 * NPI TIN included in the QRDA III
	 * @param wrapper object to encode into
	 * @param node object to encode
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) {
		List<Node> npiTinCombinations = node.findNode(MultipleTinsDecoder.NPI_TIN_ID);
		Node clinicalDocumentNode = node.findFirstNode(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		JsonOutputEncoder clinicalDocumentEncoder = ENCODERS.get(TemplateId.CLINICAL_DOCUMENT.getTemplateId());

		if (npiTinCombinations.size() > 1) {
			npiTinCombinations.stream().forEach(npiTinNode -> {
					JsonWrapper childWrapper = new JsonWrapper();
					clinicalDocumentNode.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER,
						npiTinNode.getValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER));
					clinicalDocumentNode.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER,
							npiTinNode.getValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER));
					clinicalDocumentEncoder.internalEncode(childWrapper, node);
					wrapper.putObject(childWrapper);
				});
		} else {
			clinicalDocumentEncoder.internalEncode(wrapper, clinicalDocumentNode);
		}
	}
}
