package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SupplementalData.SupplementalType;
import org.jdom2.Element;

/**
 * Decoder for Supplemental Data Sex Element
 */
@Decoder(TemplateId.SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
public class SupplementalDataSexDecoder extends QrdaXmlDecoder {

	public SupplementalDataSexDecoder(Context context) {
		super(context);
	}

	/**
	 *
	 * @param element Top element in the XML document
	 * @param thisNode Top node created in the XML document
	 * @return
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setSupplementalDataOnNode(element, thisNode, SupplementalType.SEX);

		return DecodeResult.TREE_CONTINUE;
	}
}
