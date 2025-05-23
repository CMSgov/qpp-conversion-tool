package gov.cms.qpp.conversion.decode;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.function.Consumer;

import static gov.cms.qpp.conversion.model.Constants.*;

/**
 * Decoder to parse Reporting Parameters Act - CMS (V2).
 */
@Decoder(TemplateId.REPORTING_PARAMETERS_ACT)
public class ReportingParametersActDecoder extends QrdaDecoder {

	public ReportingParametersActDecoder(Context context) {
		super(context);
	}

	/**
	 * Decodes a given element for a reporting parameter into a specified node
	 * And returns finished.
	 *
	 * @param element XML document element
	 * @param thisNode Reporting parameter node
	 * @return Finished parsing tree result
	 */
	@Override
	protected DecodeResult decode(Element element, Node thisNode) {
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
				p -> {
					String start = p.getValue();
					thisNode.putValue(PERFORMANCE_START, start, false);
					//start is formatted as follows: yyyyMMddHHmmss
					thisNode.putValue(PERFORMANCE_YEAR, start.substring(0, YEAR_LAST_INDEX));
				};
		Consumer<? super Attribute> performanceEndConsumer =
				p -> thisNode.putValue(PERFORMANCE_END, p.getValue(), false);

		setOnNode(element, performanceStartExprStr, performanceStartConsumer, Filters.attribute(), false);
		setOnNode(element, performanceEndExprStr, performanceEndConsumer, Filters.attribute(), false);
	}
}
