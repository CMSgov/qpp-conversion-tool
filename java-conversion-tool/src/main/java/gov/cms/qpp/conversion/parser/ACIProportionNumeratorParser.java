package gov.cms.qpp.conversion.parser;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TransformHandler;

@TransformHandler(elementName="observation", templateId="2.16.840.1.113883.10.20.27.3.31")
public class ACIProportionNumeratorParser extends QppXmlInputParser {
	@Override
	protected Node internalParse(Element element, Node thisnode) {
		return this.parse(element.getChild("entryRelationship"), thisnode);
	}
}
