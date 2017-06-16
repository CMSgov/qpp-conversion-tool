package gov.cms.qpp.conversion.decode;

import java.util.function.Consumer;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Decoder to parse Improvement Activity Section.
 */
@Decoder(TemplateId.MEASURE_PERFORMED)
public class MeasurePerformedDecoder extends QppXmlDecoder {

	/**
	 * Decodes a measure performed element into a node.
	 *
	 * @param element XML parsed representation of measure performed
	 * @param thisNode Object to hold the measure performed
	 * @return
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setMeasurePerformedOnNode(element, thisNode);
		return DecodeResult.TREE_FINISHED;
	}

	/**
	 * Decodes a measure performed element into a node with a boolean code
	 *
	 * @param element XML parsed representation of measure performed
	 * @param thisNode Object to hold the measure performed
	 */
	private void setMeasurePerformedOnNode(Element element, Node thisNode) {
		String expressionStr = getXpath("measurePerformed");
		Consumer<? super Attribute> consumer = p -> thisNode.putValue("measurePerformed", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}
}
