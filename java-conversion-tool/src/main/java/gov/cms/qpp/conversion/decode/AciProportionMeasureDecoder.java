package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

/**
 * Decoder to parse ACI Numerator Denominator Type Measure reference and results.
 */
@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.28")
public class AciProportionMeasureDecoder extends QppXmlDecoder {

	/**
	 * Decodes an ACI Numerator Denominator Type Measure into an intermediate node
	 *
	 * @param element Element XML element that represents the ACI Numerator Denominator Type Measure
	 * @param thisNode Node that represents the ACI Numerator Denominator Type Measure. It is updated in this method.
	 * @return DecodeResult
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {

		setMeasureIdOnNode(element, thisNode);

		return DecodeResult.TREE_CONTINUE;
	}

	private void setMeasureIdOnNode(Element element, Node thisNode) {
		String expressionStr = "./ns:reference/ns:externalDocument/ns:id/@extension";
		Consumer<? super Attribute> consumer = p -> thisNode.putValue("measureId", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}
}
