package gov.cms.qpp.conversion.decode;


import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Decoder to parse Advancing Care Information Section.
 * @author David Puglielli
 *
 */
@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.2.5")
public class AciSectionDecoder extends QppXmlDecoder {
	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("category", "aci");
		return DecodeResult.TREE_CONTINUE;
		/// TODO here is a case where TreeContinue could be used
	}
		
}
