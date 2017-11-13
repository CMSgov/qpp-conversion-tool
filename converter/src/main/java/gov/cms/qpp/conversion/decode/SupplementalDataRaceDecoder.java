package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import java.util.function.Consumer;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

/**
 * Decoder for Supplemental Data Race Element
 */
@Decoder(TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
public class SupplementalDataRaceDecoder extends QppXmlDecoder {

	public static final String RACE_NAME = "race";

	public SupplementalDataRaceDecoder(Context context) {
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
		setRaceOnNode(element, thisNode);
		return DecodeResult.TREE_CONTINUE;
	}

	/**
	 * Sets the Race Code in the current Node
	 *
	 * @param element XML element that represents SupplementalDataCode
	 * @param thisNode Current Node to decode into
	 */
	private void setRaceOnNode(Element element, Node thisNode) {
		String expressionStr = getXpath(SupplementalDataEthnicityDecoder.SUPPLEMENTAL_DATA_CODE);
		Consumer<? super Attribute> consumer = attr -> {
			String code = attr.getValue();
			thisNode.putValue(RACE_NAME, code, false);
		};
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}
}
