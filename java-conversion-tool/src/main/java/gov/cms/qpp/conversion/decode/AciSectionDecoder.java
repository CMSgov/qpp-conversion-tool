package gov.cms.qpp.conversion.decode;

import java.util.List;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.2.5")
public class AciSectionDecoder extends QppXmlDecoder {
	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		thisnode.putValue("category", "aci");
		List<Element> children = element.getChildren("entry", defaultNs);
		this.decode(children, thisnode);
		return thisnode;
	}
		
}
