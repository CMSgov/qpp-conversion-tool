package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

/**
 * Decoder to parse ACI Numerator Denominator Type Measure reference and results.
 */
@Decoder(TemplateId.ACI_NUMERATOR_DENOMINATOR)
public class AciNumeratorDenominatorDecoder extends QppXmlDecoder {

	private static final String MEASURE_ID = "measureId";

	public AciNumeratorDenominatorDecoder(Converter converter) {
		super(converter);
	}

	/**
	 * Decodes an ACI Numerator Denominator Type Measure into an intermediate node
	 *
	 * @param element Element XML element that represents the ACI Numerator Denominator Type Measure
	 * @param thisNode Node that represents the ACI Numerator Denominator Type Measure.
	 *  It is updated in this method.
	 * @return DecodeResult
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setMeasureIdOnNode(element, thisNode);

		return DecodeResult.TREE_CONTINUE;
	}

	/**
	 * Sets the measure name id
	 *
	 * @param element Object that holds the XML representation of measure id
	 * @param thisNode Object that will retrieve the parsed measure id
	 */
	private void setMeasureIdOnNode(Element element, Node thisNode) {
		String expressionStr = getXpath(MEASURE_ID);
		Consumer<? super Attribute> consumer = p ->
				thisNode.putValue(MEASURE_ID, p.getValue(), false);
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}
}
