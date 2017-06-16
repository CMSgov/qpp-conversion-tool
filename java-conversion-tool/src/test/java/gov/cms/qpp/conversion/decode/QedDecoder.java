package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

@Decoder(TemplateId.QED)
public class QedDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue(element.getAttributeValue("resultName"), element.getAttributeValue("resultValue"));
		return DecodeResult.TREE_FINISHED;
	}
}
