package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Decoder;
import org.jdom2.Element;

@Decoder(TemplateId.NULL_RETURN)
public class NullReturnDecoder extends QppXmlDecoder {

	public NullReturnDecoder(Converter converter) {
		super(converter);
	}

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		return null;
	}
}
