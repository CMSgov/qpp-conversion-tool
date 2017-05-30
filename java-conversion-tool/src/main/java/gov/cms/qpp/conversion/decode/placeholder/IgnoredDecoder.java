package gov.cms.qpp.conversion.decode.placeholder;

import org.jdom2.Element;

import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.model.Node;

public class IgnoredDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		return DecodeResult.TREE_CONTINUE; // TODO Adam Edwards 5/30/2017 - should this be continue?
	}

}