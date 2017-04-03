package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;
import org.jdom2.Element;

/**
 * Decoder to read XML data for a Numerator Type Measure
 */
@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.31")
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
