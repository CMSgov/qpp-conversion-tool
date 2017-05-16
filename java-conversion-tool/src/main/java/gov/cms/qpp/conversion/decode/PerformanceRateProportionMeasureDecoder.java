package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import java.util.function.Consumer;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

/**
 * Decodes a Performance Rate Proportion Measure V2
 */
@Decoder(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE_CMS_V2)
public class PerformanceRateProportionMeasureDecoder extends QppXmlDecoder {

	/**
	 * Decodes a Performance Rate Proportion Measure V2 from the current element
	 *
	 * @param element Current element to be decoded
	 * @param thisNode Current node to be decoded into
	 * @return
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		decodePerformanceRateId(element, thisNode);
		decodePerformanceRateValue(element, thisNode);

		return DecodeResult.TREE_FINISHED;
	}

	/**
	 * Decodes the current element performance rate uuid
	 *
	 * @param element current element to be decoded
	 * @param thisNode node to be decoded into
	 */
	private void decodePerformanceRateId(Element element, Node thisNode) {
		String performanceRateIdExpression = "./ns:reference/ns:externalObservation/ns:id/@root";
		Consumer<? super Attribute> consumer =
				p -> thisNode.putValue("performanceRateId", p.getValue());
		setOnNode(element, performanceRateIdExpression, consumer, Filters.attribute(), true);
	}

	/**
	 * Decodes the current element performance rate value
	 *
	 * @param element current element to be decoded
	 * @param thisNode node to be decoded into
	 */
	private void decodePerformanceRateValue(Element element, Node thisNode) {
		String performanceRateValueExpression = "./ns:value/@value";
		Consumer<? super Attribute> consumer =
				p -> thisNode.putValue("performanceRateValue", p.getValue().toLowerCase());
		setOnNode(element, performanceRateValueExpression, consumer, Filters.attribute(), true);
	}


}
