package gov.cms.qpp.conversion.decode;

import java.util.function.Consumer;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Decoder to parse an Aggregate Count value type.
 * @author Scott Fradkin
 *
 */
@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.3")
public class AggregateCount extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		setSciNumeratorDenominatorOnNode(element, thisnode);
		return DecodeResult.TreeFinished;
	}
	
	protected void setSciNumeratorDenominatorOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:value/@value";
		Consumer<? super Attribute> consumer = p -> thisnode.putValue("aggregateCount", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}

}
