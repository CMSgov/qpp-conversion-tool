package gov.cms.qpp.conversion.decode;

import java.util.Optional;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.27")
public class IaMeasurePerformed extends QppXmlDecoder {
	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		
		XPathExpression<Attribute> valueExpr = XPathFactory.instance()
				.compile("./ns:value/@code", Filters.attribute(), null, xpathNs);
		
		Optional.ofNullable(valueExpr.evaluateFirst(element))
					.ifPresent(p -> thisnode.putValue("iaMeasureNode", p.getValue()));

		return thisnode;
	}
	
}
