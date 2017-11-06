package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import org.jdom2.Element;

@Decoder(TemplateId.QED)
public class QedDecoder extends QppXmlDecoder {

	public QedDecoder(Context context) {
		super(context);
	}

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue(element.getAttributeValue("resultName"), element.getAttributeValue("resultValue"));
		return DecodeResult.TREE_FINISHED;
	}
}
