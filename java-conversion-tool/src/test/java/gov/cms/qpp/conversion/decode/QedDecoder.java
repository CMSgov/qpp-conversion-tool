package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId="Q.E.D")
public class QedDecoder extends QppXmlDecoder {
	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		thisnode.putValue(element.getAttributeValue("resultName"), element.getAttributeValue("resultValue"));
		return thisnode;
	}
}
