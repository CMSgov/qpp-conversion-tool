package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

@Decoder(TemplateId.NULL_RETURN)
class NullReturnDecoder extends QppXmlDecoder {

	public NullReturnDecoder(Context context) {
		super(context);
	}

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		return null;
	}
}
