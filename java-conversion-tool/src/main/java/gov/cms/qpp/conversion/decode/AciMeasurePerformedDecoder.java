package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Decoder;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

/**
 * Decoder to parse Improvement Activity Section.
 * @author David Puglielli
 *
 */
@Decoder(TemplateId.ACI_MEASURE_PERFORMED)
public class AciMeasurePerformedDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		setMeasurePerformedOnNode(element, thisnode);
		return DecodeResult.TREE_FINISHED;
	}

	private void setMeasurePerformedOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:value/@code";
		Consumer<? super Attribute> consumer = p -> thisnode.putValue("measurePerformed", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}
}
