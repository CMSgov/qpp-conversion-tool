package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Decoder to parse Improvement Activity Section.
 * @author David Puglielli
 *
 */
@XmlDecoder(templateId = TemplateId.IA_SECTION)
public class IaSectionDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("category", "ia");
		decode(element.getChild("entry", defaultNs), thisnode);
		return DecodeResult.TREE_FINISHED; // TODO maybe use TreeContinue and not call decode
	}
}
