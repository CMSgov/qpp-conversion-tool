package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Element;

/**
 * Decoder to read XML data for a Numerator Type Measure
 */
@Decoder(TemplateId.ACI_NUMERATOR)
public class AciProportionNumeratorDecoder extends QppXmlDecoder {

	/**
	 *  Decodes an ACI Numerator Measure into an intermediate node
	 *
	 * @param element XML element that represents the ACI Numerator
	 * @param thisNode Node that represents the ACI Numerator Measure
	 * @return
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		thisNode.putValue("name", "aciProportionNumerator");
		return DecodeResult.TREE_CONTINUE;
	}
}
