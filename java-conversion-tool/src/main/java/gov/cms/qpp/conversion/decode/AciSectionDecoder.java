package gov.cms.qpp.conversion.decode;


import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Decoder;
import org.jdom2.Element;

/**
 * Decoder to read XML Data for an ACI Section.
 */
@Decoder(TemplateId.ACI_SECTION)
public class AciSectionDecoder extends QppXmlDecoder {

	public AciSectionDecoder(Converter converter) {
		super(converter);
	}

	/**
	 * Decodes an ACI Section into the intermediate Node format
	 *
	 * @param element XML element that represents the ACI Section
	 * @param thisNode Node represents the aci section
	 * @return {@code DecodeResult.TREE_CONTINUE} to continue down the parsed XML
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		thisNode.putValue("category", "aci");
		return DecodeResult.TREE_CONTINUE;
	}
}
