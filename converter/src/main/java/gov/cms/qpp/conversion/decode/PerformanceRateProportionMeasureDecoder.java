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
	public static final String NULL_PERFORMANCE_RATE = "nullRate";
	public static final String PERFORMANCE_RATE_ID = "performanceRateUuid";

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
		setNameOnNode(element, thisNode, PERFORMANCE_RATE);
		if (isFirstExpressionUnsuccessful(thisNode)) {
			setNameOnNode(element, thisNode, NULL_PERFORMANCE_RATE);
		}
		setNameOnNode(element, thisNode, PERFORMANCE_RATE_ID);

		return DecodeResult.TREE_CONTINUE;
	}

	/**
	 * Check if the first expression successfully found a performance rate value
	 *
	 * @param performanceRateNode
	 * @return
	 */
	private boolean isFirstExpressionUnsuccessful(Node performanceRateNode) {
		return null == performanceRateNode.getValue(PERFORMANCE_RATE);
	}

	/**
	 * Finds the Xpath associated with the given name and puts it in node
	 *
	 * @param element Object the xpath will be evaluated upon
	 * @param node Object to hold the value found
	 * @param name Attribute name associated with the correct xpath
	 * @return
	 */
	private void setNameOnNode(Element element, Node node, final String name) {
		String expression = getXpath(name);
		Consumer<? super Attribute> consumer = attr -> {
			String value = attr.getValue();
			node.putValue(name, value);
		};

		setOnNode(element, expression, consumer, Filters.attribute(), true);
	}
}
