package gov.cms.qpp.conversion.stubs;

import org.jdom2.Element;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.QrdaDecoder;
import gov.cms.qpp.conversion.model.Node;

public class JennyDecoder extends QrdaDecoder {

	public JennyDecoder(Context context) {
		super(context);
	}

	@Override
	protected DecodeResult decode(Element element, Node thisnode) {
		thisnode.putValue("DefaultDecoderFor", "Jenny");
		if (element.getChildren().size() > 1) {
			thisnode.putValue( "problem", "too many children" );
		}
		return DecodeResult.TREE_CONTINUE;
	}
}
