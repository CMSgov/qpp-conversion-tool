package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.2.4")
public class IaSectionDecoder extends QppXmlDecoder {
	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("category", "ia");
		decode(element.getChild("entry", defaultNs), thisnode);
		return DecodeResult.TreeFinished; // TODO maybe use TreeContineu and not call decode
	}
		
}
