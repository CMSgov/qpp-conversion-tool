package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Element;

/**
 * Decoder for Supplemental Data Ethnicity Element
 */
@Decoder(TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
public class SupplementalDataEthnicityDecoder extends QppXmlDecoder{

	public SupplementalDataEthnicityDecoder(Context context) {
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
		return super.internalDecode(element, thisNode);
	}
}
