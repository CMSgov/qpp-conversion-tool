package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SupplementalData.SupplementalType;
import org.jdom2.Element;

/**
 * Decoder for Supplemental Data Ethnicity Element
 */
@Decoder(TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
public class SupplementalDataEthnicityDecoder extends QrdaXmlDecoder {

	public static final String SUPPLEMENTAL_DATA_CODE = "code";
	public static final String SUPPLEMENTAL_DATA_KEY = "supplementalData";

	public SupplementalDataEthnicityDecoder(Context context) {
		super(context);
	}

	/**
	 * Decodes Ethnicity Supplemental Code into a Node and continues on the decoding tree
	 *
	 * @param element Top element in the XML document
	 * @param thisNode Top node created in the XML document
	 * @return Continuation of tree traversal
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setSupplementalDataOnNode(element, thisNode, SupplementalType.ETHNICITY);

		return DecodeResult.TREE_CONTINUE;
	}
}
