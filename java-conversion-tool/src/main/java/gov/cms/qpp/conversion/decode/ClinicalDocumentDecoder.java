package gov.cms.qpp.conversion.decode;

import java.util.List;
import java.util.function.Consumer;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId = ClinicalDocumentDecoder.ROOT_TEMPLATEID)
public class ClinicalDocumentDecoder extends QppXmlDecoder {
	
	static final String ROOT_TEMPLATEID = "2.16.840.1.113883.10.20.27.1.2";

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		setProgramNameOnNode(element, thisnode);

		setNationalProviderIdOnNode(element, thisnode);
		
		setTaxProviderTaxIdOnNode(element, thisnode);

		processComponentElement(element, thisnode);

		return DecodeResult.TreeFinished;
	}

	protected void setProgramNameOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:informationRecipient/ns:intendedRecipient/ns:id[@root='2.16.840.1.113883.3.249.7']/@extension";
		Consumer<? super Attribute> consumer = p -> thisnode.putValue("programName", p.getValue().toLowerCase());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}

	protected void setNationalProviderIdOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:id[@root='2.16.840.1.113883.4.6']/@extension";
		Consumer<? super Attribute> consumer = p -> thisnode.putValue("nationalProviderIdentifier", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}

	protected void setTaxProviderTaxIdOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:representedOrganization/ns:id[@root='2.16.840.1.113883.4.2']/@extension";
		Consumer<? super Attribute> consumer = p -> thisnode.putValue("taxpayerIdentificationNumber", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}

	protected void processComponentElement(Element element, Node thisnode) {
		String expressionStr = "./ns:component/ns:structuredBody/ns:component";
		Consumer<? super List<Element>> consumer = p -> this.decode(p, thisnode);
		setOnNode(element, expressionStr, consumer, Filters.element(), false);
	}

}
