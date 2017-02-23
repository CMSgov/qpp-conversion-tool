package gov.cms.qpp.conversion.parser;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TransformHandler;



@TransformHandler(elementName="observation", templateId="2.16.840.1.113883.10.20.27.3.31")
public class ACIProportionNumeratorParser extends QppXmlInputParser {

	public ACIProportionNumeratorParser() {
	}
	
	@Override
	protected Node internalParse(Element element) {
		return this.parse(element.getChild("entryRelationship"), createNode(element));
	}
	
	
	// Can we push this up into the base class?
	private Node createNode(Element element) {
		Node thisNode = new Node();

		String templateId = null;
		// we will also need the template id
		Element templateIdElement = element.getChild("templateId");

		if (null != templateIdElement) {
			templateId = templateIdElement.getAttributeValue("root");
		}

		thisNode.setId(element.getName(), templateId);
		return thisNode;
	}

}
