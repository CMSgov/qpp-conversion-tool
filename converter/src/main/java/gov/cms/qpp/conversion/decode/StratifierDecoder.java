package gov.cms.qpp.conversion.decode;


import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

@Decoder(TemplateId.REPORTING_STRATUM_CMS)
public class StratifierDecoder extends QppXmlDecoder {

	public static final String STRATIFIER_ID = "populationId";

	public StratifierDecoder(Context context) {
		super(context);
	}

	/**
	 * Decodes a measure performed element into a node.
	 *
	 * @param element XML parsed representation of measure performed
	 * @param thisNode Object to hold the measure performed
	 * @return
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setStratifierId(element, thisNode);
		return DecodeResult.TREE_FINISHED;
	}

	private void setStratifierId(Element element, Node thisNode) {
		String expressionStr = getXpath(STRATIFIER_ID);
		Consumer<? super Attribute> consumer = attr -> {
			String code = attr.getValue();
			thisNode.putValue(STRATIFIER_ID, code, false);
		};
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}

}
