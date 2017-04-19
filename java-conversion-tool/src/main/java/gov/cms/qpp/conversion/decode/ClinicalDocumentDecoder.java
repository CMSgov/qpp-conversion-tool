package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Decoder;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.List;
import java.util.function.Consumer;

/**
 * Decoder to parse the root element of the Document-Level Template: QRDA Category III Report (ClinicalDocument).
 */
@Decoder(TemplateId.CLINICAL_DOCUMENT)
public class ClinicalDocumentDecoder extends QppXmlDecoder {

	private static final String PROGRAM_NAME =
			"./ns:informationRecipient/ns:intendedRecipient/"
			+ "ns:id[@root='2.16.840.1.113883.3.249.7']/@extension";

	private static final String NATIONAL_PROVIDER_ID =
			"./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/"
			+ "ns:id[@root='2.16.840.1.113883.4.6']/@extension";

	private static final String TAX_PROVIDER_TAX_ID =
			"./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/"
			+ "ns:representedOrganization/ns:id[@root='2.16.840.1.113883.4.2']/@extension";

	private static final String COMPONENT_ELEMENT =
			"./ns:component/ns:structuredBody/ns:component";

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
		Consumer<? super Attribute> consumer = p -> thisNode.putValue("programName", p.getValue().toLowerCase());
		setOnNode(element, PROGRAM_NAME, consumer, Filters.attribute(), true);
	}

	private void setNationalProviderIdOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p -> thisNode.putValue("nationalProviderIdentifier", p.getValue());
		setOnNode(element, NATIONAL_PROVIDER_ID, consumer, Filters.attribute(), true);
	}

	private void setTaxProviderTaxIdOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p -> thisNode.putValue("taxpayerIdentificationNumber", p.getValue());
		setOnNode(element, TAX_PROVIDER_TAX_ID, consumer, Filters.attribute(), true);
	}

	private void processComponentElement(Element element, Node thisNode) {
		Consumer<? super List<Element>> consumer = p -> this.decode(p, thisNode);
		setOnNode(element, COMPONENT_ELEMENT, consumer, Filters.element(), false);
	}
}