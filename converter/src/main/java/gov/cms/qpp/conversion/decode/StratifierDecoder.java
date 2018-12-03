package gov.cms.qpp.conversion.decode;


import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.function.Consumer;

/**
 * Decoder for Quality Measure Stratifiers
 */
@Decoder(TemplateId.REPORTING_STRATUM_CMS)
public class StratifierDecoder extends QrdaDecoder {

	public static final String STRATIFIER_ID = "populationId";

	public StratifierDecoder(Context context) {
		super(context);
	}

	/**
	 * Decodes a measure stratifier element into a node.
	 *
	 * @param element XML parsed representation of measure stratifier
	 * @param thisNode Object to hold the measure stratifier
	 * @return cue to caller about how to proceed after this node of the xml document is decoded
	 */
	@Override
	protected DecodeResult decode(Element element, Node thisNode) {
		setStratifierId(element, thisNode);
		return DecodeResult.TREE_FINISHED;
	}

	/**
	 * Dig up the stratifier's id and assign it to thisNode
	 * @param element DOM element
	 * @param thisNode current node
	 */
	private void setStratifierId(Element element, Node thisNode) {
		String expressionStr = getXpath(STRATIFIER_ID);
		Consumer<? super Attribute> consumer = attr -> {
			String code = attr.getValue();
			thisNode.putValue(STRATIFIER_ID, code, false);
		};
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}

}
