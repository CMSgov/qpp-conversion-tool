package gov.cms.qpp.conversion.decode;

import java.util.Optional;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlRootDecoder;

@XmlRootDecoder(rootElement = "ClinicalDocument")
public class ClinicalDocumentDecoder extends QppXmlDecoder {
	
	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		XPathExpression<Attribute> templateIdExpr = XPathFactory.instance()
				.compile("./ns:templateId[@root='2.16.840.1.113883.10.20.27.1.2']/@root", 
						Filters.attribute(), null,  xpathNs);
		
		XPathExpression<Element> componentExpr = XPathFactory.instance()
				.compile("./ns:component/ns:structuredBody/ns:component", 
						Filters.element(), null, xpathNs);
		
		Optional.ofNullable(templateIdExpr.evaluateFirst(element)).ifPresent(p -> thisnode.setId(p.getValue()));
		
		Optional.ofNullable(componentExpr.evaluate(element)).ifPresent(p -> this.decode(p, thisnode));
		
		return thisnode;
	}

}
