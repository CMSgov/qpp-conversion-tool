package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Decoder;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

/**
 * Decoder to parse an Aggregate Count value type.
 *
 * @author Scott Fradkin
 *
 */
@Decoder(TemplateId.ACI_AGGREGATE_COUNT)
public class AggregateCountDecoder extends QppXmlDecoder {

	public static final String AGGREGATE_COUNT = "aggregateCount";

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
		return DecodeResult.TREE_FINISHED;
	}

	/**
	 * Sets the aggregateCount value into the element
	 *
	 * @param element Element
	 * @param thisnode Node
	 */
	protected void setSciNumeratorDenominatorOnNode(Element element, Node thisnode) {
		String expressionStr = getXpath(AGGREGATE_COUNT);
		Consumer<? super Attribute> consumer = p -> thisnode.putValue(AGGREGATE_COUNT, p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}
}
