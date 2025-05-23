package gov.cms.qpp.conversion.decode;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.function.Consumer;

import static gov.cms.qpp.conversion.model.Constants.AGGREGATE_COUNT;

/**
 * Decoder to parse an Aggregate Count value type.
 */
@Decoder(TemplateId.PI_AGGREGATE_COUNT)
public class AggregateCountDecoder extends QrdaDecoder {

	public AggregateCountDecoder(Context context) {
		super(context);
	}

	/**
	 * Parses out the aggregateCount value from the xml fragment
	 *
	 * @param element Element
	 * @param thisnode Node
	 * @return DecodeResult.TreeFinished;
	 */
	@Override
	protected DecodeResult decode(Element element, Node thisnode) {
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
		Consumer<? super Attribute> consumer = p ->
				thisnode.putValue(AGGREGATE_COUNT, p.getValue(), false);
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}
}
