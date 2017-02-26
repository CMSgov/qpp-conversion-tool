package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.2.5")
public class AciSectionDecoder extends QppXmlDecoder {
	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		thisnode.putValue("category", "aci");
		this.decode(element.getChild("entry", defaultNs), thisnode);
		return thisnode;
	}
		
}
