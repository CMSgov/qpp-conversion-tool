package gov.cms.qpp.conversion.decode;

import java.util.Optional;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.28")
public class AciProportionMeasureDecoder extends QppXmlDecoder {


	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		
		XPathExpression<Attribute> measureIdExpr = XPathFactory.instance()
				.compile("./ns:reference/ns:externalDocument/ns:id/@extension", Filters.attribute(), null, xpathNs);

		Optional.ofNullable(measureIdExpr.evaluateFirst(element))
				.ifPresent(p -> thisnode.putValue("measureId", p.getValue()));

		// getChildren always returns at least an empty list
		for (Element child : element.getChildren("component", defaultNs)) {
			this.decode(child, thisnode);
		}

		return thisnode;

	}

}
