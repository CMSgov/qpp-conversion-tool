package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

/**
 * Decoder to parse Reporting Parameters Act - CMS (V2).
 */
@Decoder(TemplateId.REPORTING_PARAMETERS_ACT)
public class ReportingParametersActDecoder extends QppXmlDecoder {
	public static final String PERFORMANCE_START = "performanceStart";
	public static final String PERFORMANCE_END = "performanceEnd";

	/**
	 * Decodes a given element for a reporting parameter into a specified node
	 * And returns finished.
	 *
	 * @param element XML document element
	 * @param thisNode Reporting parameter node
	 * @return Finished parsing tree result
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setPerformanceTimeRangeOnNode(element, thisNode);
		return DecodeResult.TREE_FINISHED;
	}

	/**
	 * Acquires the reporting parameters within the xml and inserts into a given node
	 *
	 * @param element XML document that contains the reporting parameters act
	 * @param thisNode Reporting parameter node
	 */
	private void setPerformanceTimeRangeOnNode(Element element, Node thisNode) {
		String performanceStartExprStr = getXpath(PERFORMANCE_START);
		String performanceEndExprStr = getXpath(PERFORMANCE_END);

		Consumer<? super Attribute> performanceStartConsumer =
				p -> thisNode.putValue(PERFORMANCE_START, p.getValue());
		Consumer<? super Attribute> performanceEndConsumer =
				p -> thisNode.putValue(PERFORMANCE_END, p.getValue());

		setOnNode(element, performanceStartExprStr, performanceStartConsumer, Filters.attribute(), true);
		setOnNode(element, performanceEndExprStr, performanceEndConsumer, Filters.attribute(), true);
	}
}
