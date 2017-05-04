package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Decoder;
import org.jdom2.Element;

/**
 * Decoder to parse Improvement Activity Section.
 * @author David Puglielli
 *
 */
@Decoder(TemplateId.IA_SECTION)
public class IaSectionDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("category", "ia");
		return DecodeResult.TREE_CONTINUE;
	}
}
