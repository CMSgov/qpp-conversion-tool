package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId="null.return")
public class NullReturnDecoder extends QppXmlDecoder {
	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		return null;
	}
}
