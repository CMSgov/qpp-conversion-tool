package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.XmlDecoderNew;
import org.jdom2.Element;

@XmlDecoderNew(TemplateId.NULL_RETURN)
public class NullReturnDecoder extends QppXmlDecoder {
	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		return null;
	}
}
