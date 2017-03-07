package gov.cms.qpp.conversion.decode;

import java.util.function.Consumer;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Decoder to parse Reporting Parameters Act - CMS (V2).
 * @author David Puglielli
 *
 */
@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.23")
public class ReportingParametersActDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		setPerformanceTimeRangeOnNode(element, thisnode);
		return DecodeResult.TreeFinished;
	}
	
	protected void setPerformanceTimeRangeOnNode(Element element, Node thisnode) {
		String performanceStartExprStr = "./ns:effectiveTime/ns:low/@value";
		String performanceEndExprStr = "./ns:effectiveTime/ns:high/@value";

		Consumer<? super Attribute> performanceStartConsumer = p -> thisnode.putValue("performanceStart", p.getValue());
		Consumer<? super Attribute> performanceEndConsumer = p -> thisnode.putValue("performanceEnd", p.getValue());

		setOnNode(element, performanceStartExprStr, performanceStartConsumer, Filters.attribute(), true);
		setOnNode(element, performanceEndExprStr, performanceEndConsumer, Filters.attribute(), true);
	}

}
