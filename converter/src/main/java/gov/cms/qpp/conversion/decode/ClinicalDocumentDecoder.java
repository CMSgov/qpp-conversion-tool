package gov.cms.qpp.conversion.decode;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static gov.cms.qpp.conversion.model.Constants.*;

/**
 * Decoder to parse the root element of the Document-Level Template: QRDA Category III Report (ClinicalDocument).
 */
@Decoder(TemplateId.CLINICAL_DOCUMENT)
public class ClinicalDocumentDecoder extends QrdaDecoder {

	public ClinicalDocumentDecoder(Context context) {
		super(context);
	}

	/**
	 * internalDecode parses the xml fragment into thisNode
	 *
	 * @param element Element
	 * @param thisNode Node
	 * @return DecodeResult.TreeFinished thisNode gets the newly parsed xml fragment
	 */
	@Override
	protected DecodeResult decode(Element element, Node thisNode) {
		setProgramNameOnNode(element, thisNode);
		setPracticeSiteAddress(element, thisNode);
		setCehrtOnNode(element, thisNode);
		String entityType = thisNode.getValueOrDefault(ENTITY_TYPE, "");
		if (MVP_ENTITIES.contains(entityType) && Program.isMips(thisNode)) {
			setValueOnNode(element, thisNode, MVP_ID);
			if (ENTITY_SUBGROUP.equalsIgnoreCase(entityType)) {
				setValueOnNode(element, thisNode, SUBGROUP_ID);
			}
		}
		if (ENTITY_APM.equalsIgnoreCase(entityType)) {
			setEntityIdOnNode(element, thisNode);
			setMultipleNationalProviderIdsOnNode(element, thisNode);
			setMultipleTaxProviderTaxIdsOnNode(element, thisNode);
		} else {
			setTaxProviderTaxIdOnNode(element, thisNode);
			if (ENTITY_INDIVIDUAL.equals(entityType)) {
				setNationalProviderIdOnNode(element, thisNode);
			}
			if (ENTITY_VIRTUAL_GROUP.equals(entityType)) {
				setEntityIdOnNode(element, thisNode, VG_ID);
			}
		}

		return DecodeResult.TREE_CONTINUE;
	}

	/**
	 * Looks up the entity Id from the element if the program name is CPC+
	 * {@code <id root="2.16.840.1.113883.3.249.5.1" extension="AR000000"/>}
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setEntityIdOnNode(Element element, Node thisNode) {
		if (Program.isPcf(thisNode)) {
			Consumer<Attribute> consumer = id ->
				thisNode.putValue(PCF_ENTITY_ID, id.getValue(), false);
			setOnNode(element, getXpath(PCF_ENTITY_ID), consumer, Filters.attribute(), false);
		} else {
			setEntityIdOnNode(element, thisNode, APM_ENTITY_ID);
		}
	}

	private void setEntityIdOnNode(Element element, Node thisNode, String entityLocationId) {
		Consumer<? super Attribute> consumer = p -> thisNode.putValue(ENTITY_ID, p.getValue());
		setOnNode(element, getXpath(entityLocationId), consumer, Filters.attribute(), true);
	}

	/**
	 * Looks up the Practice Site address from the element if the program name is CPC+
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setPracticeSiteAddress(Element element, Node thisNode) {
		if (Program.isPcf(thisNode)) {
			Consumer<Element> consumer = p ->
					thisNode.putValue(PRACTICE_SITE_ADDR, p.getValue().trim(), false);
			setOnNode(element, getXpath(PRACTICE_SITE_ADDR), consumer, Filters.element(), false);
		}
	}

	/**
	 * Looks up the CEHRT from the element
	 * {@code <id root="2.16.840.1.113883.3.2074.1" extension="0014ABC1D1EFG1H"/>}
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setCehrtOnNode(Element element, Node thisNode) {
		Consumer<Attribute> consumer = cehrt ->
			thisNode.putValue(CEHRT, cehrt.getValue(), false);
		setOnNode(element, getXpath(CEHRT), consumer, Filters.attribute(), false);
	}

	/**
	 * Sets a specific value as an element on the Node class decoder
	 * @param element current xml element to find the value via xpath
	 * @param thisNode current node
	 * @param currentValue to be added to the Node
	 */
	private void setValueOnNode(Element element, Node thisNode, String currentValue) {
		Consumer<? super Attribute> consumer = p -> thisNode.putValue(currentValue, p.getValue());
		setOnNode(element, getXpath(currentValue), consumer, Filters.attribute(), true);
	}

	/**
	 * Will decode the program name from the xml
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setProgramNameOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p -> {
			Pair<String, String> nameEntityPair = getProgramNameEntityPair(p.getValue());
			thisNode.putValue(PROGRAM_NAME, nameEntityPair.getLeft(), false);
			System.out.println("Program name: " + PROGRAM_NAME);
			thisNode.putValue(ENTITY_TYPE, nameEntityPair.getRight(), false);
			System.out.println("Entity Type: " + ENTITY_TYPE);
			thisNode.putValue(RAW_PROGRAM_NAME, p.getValue(), false);
			System.out.println("Raw Program name: " + RAW_PROGRAM_NAME);
		};
		setOnNode(element, getXpath(PROGRAM_NAME), consumer, Filters.attribute(), false);
		context.setProgram(Program.extractProgram(thisNode));
	}

	/**
	 * Will decode the NPI from the xml
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setNationalProviderIdOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p ->
				thisNode.putValue(NATIONAL_PROVIDER_IDENTIFIER, p.getValue());
			setOnNode(element, getXpath(NATIONAL_PROVIDER_IDENTIFIER),
				consumer, Filters.attribute(), true);
	}

	/**
	 * Will decode multiple NPIs from the xml into a list and remove all brackets.
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setMultipleNationalProviderIdsOnNode(Element element, Node thisNode) {
		Consumer<List<String>> consumer = p ->
			thisNode.putValue(NATIONAL_PROVIDER_IDENTIFIER,
				p.toString().substring(1, p.toString().length() - 1).trim());
		setMultipleAttributesOnNode(element, getXpath(NATIONAL_PROVIDER_IDENTIFIER),
			consumer, Filters.attribute());
	}

	/**
	 * Will decode the TIN from the xml
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setTaxProviderTaxIdOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p ->
				thisNode.putValue(TAX_PAYER_IDENTIFICATION_NUMBER,
						p.getValue());
		setOnNode(element, getXpath(TAX_PAYER_IDENTIFICATION_NUMBER),
			consumer, Filters.attribute(), true);
	}

	/**
	 * Will decode multiple TINs from the xml into a list and remove all brackets.
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setMultipleTaxProviderTaxIdsOnNode(Element element, Node thisNode) {
		Consumer<List<String>> consumer = p ->
			thisNode.putValue(TAX_PAYER_IDENTIFICATION_NUMBER,
				p.toString().substring(1, p.toString().length() - 1).trim());
		setMultipleAttributesOnNode(element, getXpath(TAX_PAYER_IDENTIFICATION_NUMBER),
			consumer, Filters.attribute());
	}

	/**
	 * decodes the program name and entity type from the name
	 *
	 * @param name String one of MIPS, MIPS_GROUP, MIPS_INDIV, SSP_PI_INDIVIDUAL or CPCPLUS
	 * @return array of String program name, entity type
	 */
	private Pair<String, String> getProgramNameEntityPair(String name) {
		Pair<String, String> pair;
		switch (name.toUpperCase(Locale.ENGLISH)) {
			case MIPS_INDIVIDUAL:
				pair = new ImmutablePair<>(MIPS_PROGRAM_NAME, ENTITY_INDIVIDUAL);
				break;

			case APP_INDIVIDUAL:
				pair = new ImmutablePair<>(APP_PROGRAM_NAME, ENTITY_INDIVIDUAL);
				break;

			case MIPS_GROUP:
				pair = new ImmutablePair<>(MIPS_PROGRAM_NAME, ENTITY_GROUP);
				break;

			case APP_GROUP:
				pair = new ImmutablePair<>(APP_PROGRAM_NAME, ENTITY_GROUP);
				break;

 			case CPCPLUS:
				pair = new ImmutablePair<>(CPCPLUS_PROGRAM_NAME, ENTITY_APM);
				break;

			case MIPS_VIRTUAL_GROUP:
				pair = new ImmutablePair<>(MIPS_PROGRAM_NAME, ENTITY_VIRTUAL_GROUP);
				break;

			case MIPS_APM:
				pair = new ImmutablePair<>(MIPS_PROGRAM_NAME, ENTITY_APM);
				break;

			case APP_APM:
				pair = new ImmutablePair<>(APP_PROGRAM_NAME, ENTITY_APM);
				break;

			case PCF:
				pair = new ImmutablePair<>(PCF_PROGRAM_NAME, ENTITY_APM);
				break;

			case MIPS_SUBGROUP:
				pair = new ImmutablePair<>(MIPS_PROGRAM_NAME, ENTITY_SUBGROUP);
				break;

			case APP_PLUS_INDIVIDUAL:
				pair = new ImmutablePair<>(APP_PLUS_PROGRAM_NAME, ENTITY_INDIVIDUAL);
				break;

			case APP_PLUS_GROUP:
				pair = new ImmutablePair<>(APP_PLUS_PROGRAM_NAME, ENTITY_GROUP);
				break;

			case APP_PLUS_APM:
				pair = new ImmutablePair<>(APP_PLUS_PROGRAM_NAME, ENTITY_APM);
				break;

			case SSP_PI_INDIVIDUAL:
				pair = new ImmutablePair<>(SSP_PROGRAM_NAME, ENTITY_INDIVIDUAL);
				break;

			case SSP_PI_GROUP:
				pair = new ImmutablePair<>(SSP_PROGRAM_NAME, ENTITY_GROUP);
				break;

			case SSP_PI_APM:
				pair = new ImmutablePair<>(SSP_PROGRAM_NAME, ENTITY_APM);
				break;

			default:
				pair = new ImmutablePair<>(name.toLowerCase(Locale.ENGLISH), ENTITY_INDIVIDUAL);
				break;
		}
		return pair;
	}
}
