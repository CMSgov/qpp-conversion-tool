package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.32")
public class AciProportionDenominatorDecoder extends QppXmlDecoder {
	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		DecodeResult result = decode(element.getChild("entryRelationship", defaultNs), thisnode);
		return result;
	}
}
