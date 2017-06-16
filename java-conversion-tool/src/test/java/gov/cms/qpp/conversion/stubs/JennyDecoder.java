package gov.cms.qpp.conversion.stubs;

import org.jdom2.Element;

import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;


public class JennyDecoder extends DefaultDecoder {
	public JennyDecoder() {
		super("default decoder for Jenny");
	}

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("DefaultDecoderFor", "Jenny");
		if (element.getChildren().size() > 1) {
			thisnode.putValue( "problem", "too many children" );
		}
		return DecodeResult.TREE_CONTINUE;
	}
}
