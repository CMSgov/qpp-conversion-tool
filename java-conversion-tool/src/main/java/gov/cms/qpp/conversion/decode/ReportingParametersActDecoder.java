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
 * @author David Puglielli
 *
 */
@Decoder(TemplateId.REPORTING_PARAMETERS_ACT)
public class ReportingParametersActDecoder extends QppXmlDecoder {
	private static final String PERFORMANCE_START = "performanceStart";
	private static final String PERFORMANCE_END = "performanceEnd";

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		setPerformanceTimeRangeOnNode(element, thisnode);
		return DecodeResult.TREE_FINISHED;
	}
	
	private void setPerformanceTimeRangeOnNode(Element element, Node thisnode) {
		String performanceStartExprStr = getXpath(PERFORMANCE_START);
		String performanceEndExprStr = getXpath(PERFORMANCE_END);

		Consumer<? super Attribute> performanceStartConsumer =
				p -> thisnode.putValue(PERFORMANCE_START, p.getValue());
		Consumer<? super Attribute> performanceEndConsumer =
				p -> thisnode.putValue(PERFORMANCE_END, p.getValue());

		setOnNode(element, performanceStartExprStr, performanceStartConsumer, Filters.attribute(), true);
		setOnNode(element, performanceEndExprStr, performanceEndConsumer, Filters.attribute(), true);
	}
}
