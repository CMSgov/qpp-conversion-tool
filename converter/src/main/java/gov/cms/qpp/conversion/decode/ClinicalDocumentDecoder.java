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

/**
 * Decoder to parse the root element of the Document-Level Template: QRDA Category III Report (ClinicalDocument).
 */
@Decoder(TemplateId.CLINICAL_DOCUMENT)
public class ClinicalDocumentDecoder extends QrdaDecoder {

	/*  Constants for lookups and tests */
	public static final String NATIONAL_PROVIDER_IDENTIFIER = "nationalProviderIdentifier";
	public static final String TAX_PAYER_IDENTIFICATION_NUMBER = "taxpayerIdentificationNumber";
	public static final String PROGRAM_NAME = "programName";
	public static final String RAW_PROGRAM_NAME = "rawProgramName";
	public static final String ENTITY_TYPE = "entityType";
	public static final String MIPS_PROGRAM_NAME = "mips";
	public static final String CPCPLUS_PROGRAM_NAME = "cpcPlus";
	public static final String PRACTICE_ID = "practiceId";
	public static final String PRACTICE_SITE_ADDR = "practiceSiteAddr";
	public static final String CEHRT = "cehrtId";
	public static final String MIPS = "MIPS";
	private static final String MIPS_GROUP = "MIPS_GROUP";
	private static final String MIPS_INDIVIDUAL = "MIPS_INDIV";
	public static final String MIPS_VIRTUAL_GROUP = "MIPS_VIRTUALGROUP";
	public static final String ENTITY_APM = "apm";
	static final String ENTITY_GROUP = "group";
	static final String ENTITY_INDIVIDUAL = "individual";
	public static final String ENTITY_VIRTUAL_GROUP = "virtualGroup";
	public static final String ENTITY_ID = "entityId";
	public static final String CPCPLUS = "CPCPLUS";

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
		setEntityIdOnNode(element, thisNode);
		setPracticeSiteAddress(element, thisNode);
		setCehrtOnNode(element, thisNode);
		String entityType = thisNode.getValue(ENTITY_TYPE);
		if (ENTITY_APM.equalsIgnoreCase(entityType)){
			setMultipleNationalProviderIdsOnNode(element, thisNode);
			setMultipleTaxProviderTaxIdsOnNode(element, thisNode);
		} else {
			setTaxProviderTaxIdOnNode(element, thisNode);
			if (ENTITY_INDIVIDUAL.equals(entityType)) {
				setNationalProviderIdOnNode(element, thisNode);
			}
			if (ENTITY_VIRTUAL_GROUP.equals(entityType)) {
				setVirtualGroupOnNode(element, thisNode);
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
		if (Program.isCpc(thisNode)) {
			Consumer<Attribute> consumer = id ->
				thisNode.putValue(PRACTICE_ID, id.getValue(), false);
			setOnNode(element, getXpath(PRACTICE_ID), consumer, Filters.attribute(), false);
		}
	}

	/**
	 * Looks up the Practice Site address from the element if the program name is CPC+
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setPracticeSiteAddress(Element element, Node thisNode) {
		if (Program.isCpc(thisNode)) {
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
	 * Will decode the program name from the xml
	 *
	 * @param element Xml fragment being parsed.
	 * @param thisNode The output internal representation of the document
	 */
	private void setProgramNameOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p -> {
			Pair<String, String> nameEntityPair = getProgramNameEntityPair(p.getValue());
			thisNode.putValue(PROGRAM_NAME, nameEntityPair.getLeft(), false);
			thisNode.putValue(ENTITY_TYPE, nameEntityPair.getRight(), false);
			thisNode.putValue(RAW_PROGRAM_NAME, p.getValue(), false);
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
		Consumer<? super List<String>> consumer = p ->
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
		Consumer<? super List<String>> consumer = p ->
			thisNode.putValue(TAX_PAYER_IDENTIFICATION_NUMBER,
				p.toString().substring(1, p.toString().length() - 1).trim());
		setMultipleAttributesOnNode(element, getXpath(TAX_PAYER_IDENTIFICATION_NUMBER),
			consumer, Filters.attribute());
	}

	private void setVirtualGroupOnNode(Element element, Node thisNode) {
		Consumer<? super Attribute> consumer = p ->
			thisNode.putValue(ENTITY_ID,
				p.getValue());
		setOnNode(element, getXpath(ENTITY_ID),
			consumer, Filters.attribute(), true);
	}

	/**
	 * decodes the program name and entity type from the name
	 *
	 * @param name String one of MIPS, MIPS_GROUP, MIPS_INDIV, or CPCPLUS
	 * @return array of String program name, entity type
	 */
	private Pair<String, String> getProgramNameEntityPair(String name) {
		Pair<String, String> pair;
		switch (name.toUpperCase(Locale.ENGLISH)) {
			case MIPS_INDIVIDUAL:
				pair = new ImmutablePair<>(MIPS_PROGRAM_NAME, ENTITY_INDIVIDUAL);
				break;

			case MIPS_GROUP:
				pair = new ImmutablePair<>(MIPS_PROGRAM_NAME, ENTITY_GROUP);
				break;

			case CPCPLUS:
				pair = new ImmutablePair<>(CPCPLUS_PROGRAM_NAME, ENTITY_APM);
				break;

			case MIPS_VIRTUAL_GROUP:
				pair = new ImmutablePair<>(MIPS_PROGRAM_NAME, ENTITY_VIRTUAL_GROUP);
				break;

			default:
				pair = new ImmutablePair<>(name.toLowerCase(Locale.ENGLISH), ENTITY_INDIVIDUAL);
				break;
		}
		return pair;
	}
}
