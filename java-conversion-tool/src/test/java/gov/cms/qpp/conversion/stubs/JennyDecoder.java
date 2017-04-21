package gov.cms.qpp.conversion.stubs;

import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.placeholder.DefaultDecoder;
import gov.cms.qpp.conversion.model.Node;
import org.jdom2.Element;


public class JennyDecoder extends DefaultDecoder {
	public JennyDecoder() {
		super("default decoder for Jenny");
	}

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("DefaultDecoderFor", "Jenny");
		thisnode.setId("867.5309");
		if (element.getChildren().size() > 1) {
			thisnode.putValue( "problem", "too many children" );
		}
		return DecodeResult.TREE_CONTINUE;
	}
}
