package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.XmlDecoderNew;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.List;
import java.util.function.Consumer;

/**
 * Decoder to parse the root element of the Document-Level Template: QRDA Category III Report (ClinicalDocument).

 */
@XmlDecoderNew(TemplateId.CLINICAL_DOCUMENT)
public class ClinicalDocumentDecoder extends QppXmlDecoder {

	/**
	 * internalDecode parses the xml fragment into thisNode
	 * @param element Element
	 * @param thisNode Node
	 * @return DecodeResult.TreeFinished thisNode gets the newly parsed xml fragment
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setProgramNameOnNode(element, thisNode);

		setNationalProviderIdOnNode(element, thisNode);

		setTaxProviderTaxIdOnNode(element, thisNode);

		processComponentElement(element, thisNode);

		return DecodeResult.TREE_FINISHED;
	}

	private void setProgramNameOnNode(Element element, Node thisNode) {
		String expressionStr = "./ns:informationRecipient/ns:intendedRecipient/ns:id[@root='2.16.840.1.113883.3.249.7']/@extension";
		Consumer<? super Attribute> consumer = p -> thisNode.putValue("programName", p.getValue().toLowerCase());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}

	private void setNationalProviderIdOnNode(Element element, Node thisNode) {
		String expressionStr = "./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:id[@root='2.16.840.1.113883.4.6']/@extension";
		Consumer<? super Attribute> consumer = p -> thisNode.putValue("nationalProviderIdentifier", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}

	private void setTaxProviderTaxIdOnNode(Element element, Node thisNode) {
		String expressionStr = "./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/ns:representedOrganization/ns:id[@root='2.16.840.1.113883.4.2']/@extension";
		Consumer<? super Attribute> consumer = p -> thisNode.putValue("taxpayerIdentificationNumber", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}

	private void processComponentElement(Element element, Node thisNode) {
		String expressionStr = "./ns:component/ns:structuredBody/ns:component";
		Consumer<? super List<Element>> consumer = p -> this.decode(p, thisNode);
		setOnNode(element, expressionStr, consumer, Filters.element(), false);
	}
}
