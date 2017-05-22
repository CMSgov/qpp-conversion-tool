package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
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

	/*  Constants for lookups and tests */
	public static final String PROGRAM_NAME = "programName";
	static final String ENTITY_TYPE = "entityType";
	public static final String NATIONAL_PROVIDER_IDENTIFIER = "nationalProviderIdentifier";
	public static final String TAX_PAYER_IDENTIFICATION_NUMBER = "taxpayerIdentificationNumber";
	public static final String MIPS_PROGRAM_NAME = "mips";
	public static final String CPCPLUS_PROGRAM_NAME = "cpcplus";
	static final String MIPS = "MIPS";
	static final String MIPS_GROUP = "MIPS_GROUP";
	static final String MIPS_INDIVIDUAL = "MIPS_INDIV";
	static final String ENTITY_GROUP = "group";
	static final String ENTITY_INDIVIDUAL = "individual";
	static final String CPCPLUS = "CPCPLUS";

	private static final String PROGRAM_NAME_PATH =
			"./ns:informationRecipient/ns:intendedRecipient/"
			+ "ns:id[@root='2.16.840.1.113883.3.249.7']/@extension";

	private static final String NATIONAL_PROVIDER_ID_PATH =
			"./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/"
			+ "ns:id[@root='2.16.840.1.113883.4.6']/@extension";

	private static final String TAX_PROVIDER_TAX_ID_PATH =
			"./ns:documentationOf/ns:serviceEvent/ns:performer/ns:assignedEntity/"
			+ "ns:representedOrganization/ns:id[@root='2.16.840.1.113883.4.2']/@extension";

	private static final String COMPONENT_ELEMENT_PATH =
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
		Consumer<? super Attribute> consumer = p -> {
			String[] nameEntityPair = getProgramNameEntityPair(p.getValue());
			thisNode.putValue(PROGRAM_NAME, nameEntityPair[0]);
			thisNode.putValue(ENTITY_TYPE, nameEntityPair[1]);
		};
		setOnNode(element, PROGRAM_NAME_PATH, consumer, Filters.attribute(), true);
	}

	private void setNationalProviderIdOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p -> thisNode.putValue(NATIONAL_PROVIDER_IDENTIFIER, p.getValue());
		setOnNode(element, NATIONAL_PROVIDER_ID_PATH, consumer, Filters.attribute(), true);
	}

	private void setTaxProviderTaxIdOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p -> thisNode.putValue(TAX_PAYER_IDENTIFICATION_NUMBER, p.getValue());
		setOnNode(element, TAX_PROVIDER_TAX_ID_PATH, consumer, Filters.attribute(), true);
	}

	private void processComponentElement(Element element, Node thisNode) {
		Consumer<? super List<Element>> consumer = p -> this.decode(p, thisNode);
		setOnNode(element, COMPONENT_ELEMENT_PATH, consumer, Filters.element(), false);
	}

	private String[] getProgramNameEntityPair(String name) {
		String[] pairs = new String[2];
		if (MIPS.equalsIgnoreCase(name) || MIPS_INDIVIDUAL.equalsIgnoreCase(name)) {
			pairs[0] = MIPS_PROGRAM_NAME;
			pairs[1] = ENTITY_INDIVIDUAL;
		} else if (MIPS_GROUP.equalsIgnoreCase(name)) {
			pairs[0] = MIPS_PROGRAM_NAME;
			pairs[1] = ENTITY_GROUP;
		} else if (CPCPLUS.equalsIgnoreCase(name)) {
			pairs[0] = CPCPLUS_PROGRAM_NAME;
			pairs[1] = "";
		} else {
			pairs[0] = name.toLowerCase(); //Unknown case
			pairs[1] = ENTITY_INDIVIDUAL;
		}
		return pairs;
	}
}
