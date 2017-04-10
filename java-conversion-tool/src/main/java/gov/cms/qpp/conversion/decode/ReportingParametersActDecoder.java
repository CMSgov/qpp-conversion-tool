package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.XmlDecoderNew;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

/**
 * Decoder to parse Reporting Parameters Act - CMS (V2).
 * @author David Puglielli
 *
 */
@XmlDecoderNew(TemplateId.REPORTING_PARAMETERS_ACT)
public class ReportingParametersActDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		setPerformanceTimeRangeOnNode(element, thisnode);
		return DecodeResult.TREE_FINISHED;
	}
	
	private void setPerformanceTimeRangeOnNode(Element element, Node thisnode) {
		String performanceStartExprStr = "./ns:effectiveTime/ns:low/@value";
		String performanceEndExprStr = "./ns:effectiveTime/ns:high/@value";

		Consumer<? super Attribute> performanceStartConsumer = p -> thisnode.putValue("performanceStart", p.getValue());
		Consumer<? super Attribute> performanceEndConsumer = p -> thisnode.putValue("performanceEnd", p.getValue());

		setOnNode(element, performanceStartExprStr, performanceStartConsumer, Filters.attribute(), true);
		setOnNode(element, performanceEndExprStr, performanceEndConsumer, Filters.attribute(), true);
	}
}
