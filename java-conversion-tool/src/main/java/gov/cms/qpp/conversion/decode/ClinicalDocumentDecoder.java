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
		
		XPathExpression<Attribute> programeNameExpr = XPathFactory.instance()
				.compile("./ns:informationRecipient/ns:intendedRecipient/ns:id[@root='2.16.840.1.113883.3.249.7']/@extension", 
						Filters.attribute(), null,  xpathNs);
		
		XPathExpression<Attribute> nationalProviderExpr = XPathFactory.instance()
				.compile("./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:id[@root='2.16.840.1.113883.4.6']/@extension", 
						Filters.attribute(), null,  xpathNs);
		
		XPathExpression<Attribute> taxIdExpr = XPathFactory.instance()
				.compile("./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:representedOrganization/ns:id[@root='2.16.840.1.113883.4.2']/@extension", 
						Filters.attribute(), null,  xpathNs);
		
		String effTimeStr = "./ns:component/ns:structuredBody/ns:component/ns:section[*[local-name()='templateId' and @root='2.16.840.1.113883.10.20.27.2.6']]/ns:entry/ns:act/ns:effectiveTime";
		XPathExpression<Attribute> performanceStartExpr = XPathFactory.instance()
				.compile(effTimeStr + "/ns:low/@value", Filters.attribute(), null,  xpathNs);
		XPathExpression<Attribute> performanceEndExpr = XPathFactory.instance()
				.compile(effTimeStr + "/ns:high/@value", Filters.attribute(), null,  xpathNs);
		
		XPathExpression<Element> componentExpr = XPathFactory.instance()
				.compile("./ns:component/ns:structuredBody/ns:component", 
						Filters.element(), null, xpathNs);
		
		Optional.ofNullable(templateIdExpr.evaluateFirst(element)).ifPresent(p -> thisnode.setId(p.getValue()));
		
		Optional.ofNullable(programeNameExpr.evaluateFirst(element)).ifPresent(p -> thisnode.putValue("programName", p.getValue().toLowerCase()));

		Optional.ofNullable(nationalProviderExpr.evaluateFirst(element)).ifPresent(p -> thisnode.putValue("nationalProviderIdentifier", p.getValue()));
		
		Optional.ofNullable(taxIdExpr.evaluateFirst(element)).ifPresent(p -> thisnode.putValue("taxpayerIdentificationNumber", p.getValue()));

		Optional.ofNullable(performanceStartExpr.evaluateFirst(element)).ifPresent(p -> thisnode.putValue("performanceStart", p.getValue()));
		Optional.ofNullable(performanceEndExpr.evaluateFirst(element)).ifPresent(p -> thisnode.putValue("performanceEnd", p.getValue()));
		
		Optional.ofNullable(componentExpr.evaluate(element)).ifPresent(p -> this.decode(p, thisnode));

		return thisnode;
	}

}
