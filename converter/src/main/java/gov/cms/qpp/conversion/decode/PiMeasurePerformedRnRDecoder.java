package gov.cms.qpp.conversion.decode;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.function.Consumer;

import static gov.cms.qpp.conversion.model.Constants.MEASURE_ID;

/**
 * Decoder to parse an PI Measure Performed Reference and Results.
 */
@Decoder(TemplateId.PI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS)
public class PiMeasurePerformedRnRDecoder extends QrdaDecoder {

	public PiMeasurePerformedRnRDecoder(Context context) {
		super(context);
	}

	/**
	 * Decodes an PI Measure Performed Reference and Results into an intermediate node
	 *
	 * @param element Element XML element that represents the PI Measure Performed measure
	 * @param thisNode Node that represents the PI Measure Performed measure.  It is updated in this method.
	 * @return {@code DecodeResult.TREE_CONTINUE}
	 */
	@Override
	protected DecodeResult decode(Element element, Node thisNode) {
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
