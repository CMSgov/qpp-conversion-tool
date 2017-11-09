package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SupplementalData;
import java.util.function.Consumer;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

/**
 * Decoder for Supplemental Data Payer Element
 */
@Decoder(TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
public class SupplementalDataPayerDecoder extends QppXmlDecoder {

	public SupplementalDataPayerDecoder(Context context) {
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
		setPayerOnNode(element, thisNode);
		return DecodeResult.TREE_CONTINUE;
	}

	/**
	 * Sets the Payer Code the current Node
	 *
	 * @param element XML element that represents SupplementalDataCode
	 * @param thisNode Current Node to decode into
	 */
	private void setPayerOnNode(Element element, Node thisNode) {
		String expressionStr = getXpath(SupplementalDataEthnicityDecoder.SUPPLEMENTAL_DATA_CODE);
		Consumer<? super Attribute> consumer = attr -> {
			String code = attr.getValue();
			thisNode.putValue(SupplementalData.getCategoryNameByCode(code), code, false);
		};
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}
}
