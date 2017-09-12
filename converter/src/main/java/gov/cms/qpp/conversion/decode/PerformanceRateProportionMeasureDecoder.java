package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import java.util.function.Consumer;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

/**
 * Decodes the Performance Rate Proportion Measure from the Measure Section
 */
@Decoder(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
public class PerformanceRateProportionMeasureDecoder extends QppXmlDecoder {

	public static final String PERFORMANCE_RATE = "rate";


	public PerformanceRateProportionMeasureDecoder(Context context) {
		super(context);
	}

	/**
	 * Decodes the performance rate
	 * Add a null check to see if the Performance rate is found.
	 * If not then will check the secondary xpath
	 *
	 * @param element Top element in the XML document
	 * @param thisNode Top node created in the XML document
	 * @return
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		String expression = getXpath(PERFORMANCE_RATE);
		Consumer<? super Attribute> consumer = attr -> {
			String value = attr.getValue();
			thisNode.putValue(PERFORMANCE_RATE, value, false);
		};
		setOnNode(element, expression, consumer, Filters.attribute(), true);

		return DecodeResult.TREE_CONTINUE;
	}
}
