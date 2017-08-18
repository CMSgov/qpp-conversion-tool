package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Decoder;
import org.jdom2.Element;

/**
 * Decoder to parse Improvement Activity Section.
 */
@Decoder(TemplateId.IA_SECTION)
public class IaSectionDecoder extends QppXmlDecoder {

	public IaSectionDecoder(Converter converter) {
		super(converter);
	}

	/**
	 * DecodeResult reads the xml elements and stores them into the internal Node structure
	 * This will update the thisNode value
	 * @param element Top element in the XML document
	 * @param thisNode Node
	 * @return
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		thisNode.putValue("category", "ia");
		return DecodeResult.TREE_CONTINUE;
	}
}
