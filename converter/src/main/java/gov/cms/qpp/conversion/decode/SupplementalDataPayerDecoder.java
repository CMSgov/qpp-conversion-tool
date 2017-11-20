package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SupplementalData.SupplementalType;
import org.jdom2.Element;

/**
 * Decoder for Supplemental Data Payer Element
 */
@Decoder(TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
public class SupplementalDataPayerDecoder extends QppXmlDecoder {

	public static final String SUPPLEMENTAL_DATA_PAYER_CODE = "payerCode";

	public SupplementalDataPayerDecoder(Context context) {
		super(context);
	}

	/**
	 * Decodes Supplemental Payer data into the current node
	 *
	 * @param element Top element in the XML document
	 * @param thisNode Top node created in the XML document
	 * @return Continuation of tree traversal
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setSupplementalDataOnNode(element, thisNode, SupplementalType.PAYER);
		return DecodeResult.TREE_CONTINUE;
	}
}
