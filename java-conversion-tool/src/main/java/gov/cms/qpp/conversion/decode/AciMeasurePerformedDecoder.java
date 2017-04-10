package gov.cms.qpp.conversion.decode;

import java.util.function.Consumer;

import gov.cms.qpp.conversion.model.TemplateId;
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
@XmlDecoder(templateId = TemplateId.ACI_MEASURE_PERFORMED)
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
