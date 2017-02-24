package gov.cms.qpp.conversion.parser;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Decoder;

@Decoder(elementName="qed", templateId="Q.E.D")
public class QEDParser extends QppXmlInputParser {
	@Override
	protected Node internalParse(Element element, Node thisnode) {
		thisnode.putValue(element.getAttributeValue("resultName"), element.getAttributeValue("resultValue"));
		return thisnode;
	}
}
