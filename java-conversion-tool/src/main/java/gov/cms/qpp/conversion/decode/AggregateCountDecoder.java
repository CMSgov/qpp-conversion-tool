package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;
import java.util.function.Consumer;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

/**
 * Decoder to parse an Aggregate Count value type.
 *
 * @author Scott Fradkin
 *
 */
@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.3")
public class AggregateCountDecoder extends QppXmlDecoder {
    /**
     * Parses out the aggregateCount value from the xml fragment
     *
     * @param element Element
     * @param thisnode Node
     * @return DecodeResult.TreeFinished;
     */
    @Override
    protected DecodeResult internalDecode(Element element, Node thisnode) {
        setSciNumeratorDenominatorOnNode(element, thisnode);
        return DecodeResult.TreeFinished;
    }

    /**
     * Sets the aggregateCount value into the element
     *
     * @param element Element
     * @param thisnode Node
     */
    protected void setSciNumeratorDenominatorOnNode(Element element, Node thisnode) {
        String expressionStr = "./ns:value/@value";
        Consumer<? super Attribute> consumer = p -> thisnode.putValue("aggregateCount", p.getValue());
        setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
    }

}
