package gov.cms.qpp.conversion.decode;

import java.util.List;
import java.util.function.Consumer;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlRootDecoder;

@XmlRootDecoder(rootElement = "ClinicalDocument")
public class ClinicalDocumentDecoder extends QppXmlDecoder {
	
	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		setTemplateIdOnNode(element, thisnode);
		
		setProgramNameOnNode(element, thisnode);

		setNationalProviderIdOnNode(element, thisnode);
		
		setTaxProviderTaxIdOnNode(element, thisnode);

		setPerformanceTimeRangeOnNode(element, thisnode);
		
		processComponentElement(element, thisnode);

		return thisnode;
	}

	protected void setTemplateIdOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:templateId[@root='2.16.840.1.113883.10.20.27.1.2']/@root";
		Consumer<? super List<Attribute>> consumer = p -> thisnode.setId(p.get(0).getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute());
	}

	protected void setProgramNameOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:informationRecipient/ns:intendedRecipient/ns:id[@root='2.16.840.1.113883.3.249.7']/@extension";
		Consumer<? super List<Attribute>> consumer = p -> thisnode.putValue("programName", p.get(0).getValue().toLowerCase());
		setOnNode(element, expressionStr, consumer, Filters.attribute());
	}

	protected void setNationalProviderIdOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:id[@root='2.16.840.1.113883.4.6']/@extension";
		Consumer<? super List<Attribute>> consumer = p -> thisnode.putValue("nationalProviderIdentifier", p.get(0).getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute());
	}

	protected void setTaxProviderTaxIdOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:representedOrganization/ns:id[@root='2.16.840.1.113883.4.2']/@extension";
		Consumer<? super List<Attribute>> consumer = p -> thisnode.putValue("taxpayerIdentificationNumber", p.get(0).getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute());
	}

	protected void setPerformanceTimeRangeOnNode(Element element, Node thisnode) {
		String effTimeStr = "./ns:component/ns:structuredBody/ns:component/ns:section[*[local-name()='templateId' and @root='2.16.840.1.113883.10.20.27.2.6']]/ns:entry/ns:act/ns:effectiveTime";
		String performanceStartExprStr = effTimeStr + "/ns:low/@value";
		String performanceEndExprStr = effTimeStr + "/ns:high/@value";

		Consumer<? super List<Attribute>> performanceStartConsumer = p -> thisnode.putValue("performanceStart", p.get(0).getValue());
		Consumer<? super List<Attribute>> performanceEndConsumer = p -> thisnode.putValue("performanceEnd", p.get(0).getValue());

		setOnNode(element, performanceStartExprStr, performanceStartConsumer, Filters.attribute());
		setOnNode(element, performanceEndExprStr, performanceEndConsumer, Filters.attribute());
	}

	protected void processComponentElement(Element element, Node thisnode) {
		String expressionStr = "./ns:component/ns:structuredBody/ns:component";
		Consumer<? super List<Element>> consumer = p -> this.decode(p, thisnode);
		setOnNode(element, expressionStr, consumer, Filters.element());
	}

}
