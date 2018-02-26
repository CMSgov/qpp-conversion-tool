package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Decoder to read XML data for a Numerator Type Measure
 */
@Decoder(TemplateId.ACI_NUMERATOR)
public class AciProportionNumeratorDecoder extends QrdaDecoder {

	public AciProportionNumeratorDecoder(Context context) {
		super(context);
	}

	/**
	 *  Decodes an ACI Numerator Measure into an intermediate node
	 *
	 * @param element XML element that represents the ACI Numerator
	 * @param thisNode Node that represents the ACI Numerator Measure
	 * @return carry on
	 */
	@Override
	protected DecodeResult decode(Element element, Node thisNode) {
		thisNode.putValue("name", "aciProportionNumerator");
		return DecodeResult.TREE_CONTINUE;
	}
}
