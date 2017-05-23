package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import java.util.List;

import static gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER;
import static gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER;
import static gov.cms.qpp.conversion.decode.MultipleTinsDecoder.NPI_TIN_ID;

/**
 * Encodes either multiple clinical documents based on npi/tin combinations
 * Or encodes one clinical document.
 */
@Encoder(TemplateId.MULTIPLE_TINS)
public class MultipleTinsEncoder extends QppOutputEncoder {

	/**
	 *
	 * @param wrapper object to encode into
	 * @param node object to encode
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) {
		List<Node> npiTinCombinations = node.findNode(NPI_TIN_ID);
		Node clinicalDocumentNode = node.findFirstNode(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		JsonOutputEncoder clinicalDocumentEncoder = ENCODERS.get(TemplateId.CLINICAL_DOCUMENT.getTemplateId());

		if (npiTinCombinations.size() > 1) {
			for (Node npiTinNode: npiTinCombinations) {
				JsonWrapper childWrapper = new JsonWrapper();
				clinicalDocumentNode.putValue(TAX_PAYER_IDENTIFICATION_NUMBER,
						npiTinNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER));

				clinicalDocumentNode.putValue(NATIONAL_PROVIDER_IDENTIFIER,
						npiTinNode.getValue(NATIONAL_PROVIDER_IDENTIFIER));

				clinicalDocumentEncoder.internalEncode(childWrapper, node);
				wrapper.putObject(childWrapper);
			}
		} else {
			clinicalDocumentEncoder.internalEncode(wrapper, clinicalDocumentNode);
		}
	}
}
