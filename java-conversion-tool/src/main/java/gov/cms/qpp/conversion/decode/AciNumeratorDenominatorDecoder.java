package gov.cms.qpp.conversion.decode;

import java.util.Optional;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.3")
public class AciNumeratorDenominatorDecoder extends QppXmlDecoder {

	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		
		XPathExpression<Attribute> valueExpr = XPathFactory.instance()
				.compile("./ns:value/@value", Filters.attribute(), null, xpathNs);
		
		// for the rate aggregation element, we want to get the
		// value element and then the value attribute

		// we'll store the value with the key: rateAggregationDenominator
		// and the value that we find
		Optional.ofNullable(valueExpr.evaluateFirst(element))
					.ifPresent(p -> thisnode.putValue("aciNumeratorDenominator", p.getValue()));

		return thisnode;
	}

}
