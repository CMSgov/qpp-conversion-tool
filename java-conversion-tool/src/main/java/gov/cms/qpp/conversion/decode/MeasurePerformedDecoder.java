package gov.cms.qpp.conversion.decode;

import java.util.function.Consumer;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Decoder to parse Improvement Activity Section.
 * @author David Puglielli
 *
 */
@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.27")
public class MeasurePerformedDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		setMeasurePerformedOnNode(element, thisnode);
		return DecodeResult.TreeFinished;
	}
	
	protected void setMeasurePerformedOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:value/@code";
		Consumer<? super Attribute> consumer = p -> thisnode.putValue("measurePerformed", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}

}
