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
 * Decoder to parse an ACI Measure Performed Reference and Results.
 */
@Decoder(TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS)
public class AciMeasurePerformedRnRDecoder extends QppXmlDecoder {

	public static final String MEASURE_ID = "measureId";

	public AciMeasurePerformedRnRDecoder(Converter converter) {
		super(converter);
	}

	/**
	 * Decodes an ACI Measure Performed Reference and Results into an intermediate node
	 *
	 * @param element Element XML element that represents the ACI Measure Performed measure
	 * @param thisNode Node that represents the ACI Measure Performed measure.  It is updated in this method.
	 * @return {@code DecodeResult.TREE_CONTINUE}
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
