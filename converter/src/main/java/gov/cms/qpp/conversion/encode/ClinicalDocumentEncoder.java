package gov.cms.qpp.conversion.encode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Encoder to serialize the root node of the Document-Level Template: QRDA Category III Report (ClinicalDocument).
 */

@Encoder(TemplateId.CLINICAL_DOCUMENT)
public class ClinicalDocumentEncoder extends QppOutputEncoder {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(ClinicalDocumentEncoder.class);
	private static final String MEASUREMENT_SETS = "measurementSets";

	public ClinicalDocumentEncoder(Context context) {
		super(context);
	}

	/**
	 * internalEncode encodes nodes into Json Wrapper.
	 *
	 * @param wrapper will hold the json format of nodes
	 * @param thisNode holds the decoded node sections of clinical document
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node thisNode) {
		encodeToplevel(wrapper, thisNode);
		Map<TemplateId, Node> childMapByTemplateId = thisNode.getChildNodes().stream().collect(
				Collectors.toMap(Node::getType, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

		JsonWrapper measurementSets = encodeMeasurementSets(childMapByTemplateId, thisNode);
		wrapper.put(MEASUREMENT_SETS, measurementSets);
	}

	/**
	 * This will add the attributes from the Clinical Document Node
	 *
	 * @param wrapper will hold the json format of nodes
	 * @param thisNode holds the decoded node sections of clinical document
	 */
	private void encodeToplevel(JsonWrapper wrapper, Node thisNode) {
		String entityType = thisNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE);

		encodePerformanceYear(wrapper, thisNode);
		wrapper.put(ClinicalDocumentDecoder.ENTITY_TYPE, entityType);
		if (!ClinicalDocumentDecoder.ENTITY_APM.equals(entityType)) {
			wrapper.put(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER,
				thisNode.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER));
			wrapper.put(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER,
				thisNode.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER));
		}

		if (Program.isCpc(thisNode)) {
			wrapper.put(ClinicalDocumentDecoder.ENTITY_ID, thisNode.getValue(ClinicalDocumentDecoder.PRACTICE_ID));
		}

		if(Program.isPcf(thisNode)) {
			wrapper.put(ClinicalDocumentDecoder.ENTITY_ID, thisNode.getValue(ClinicalDocumentDecoder.PCF_ENTITY_ID));
		}

		if (ClinicalDocumentDecoder.ENTITY_VIRTUAL_GROUP.equals(entityType) ||
			(!Program.isCpc(thisNode) || !Program.isPcf(thisNode) && ClinicalDocumentDecoder.ENTITY_APM.equalsIgnoreCase(entityType))) {
			wrapper.put(ClinicalDocumentDecoder.ENTITY_ID, thisNode.getValue(ClinicalDocumentDecoder.ENTITY_ID));
		}
	}

	/**
	 * Extracts performance year from the first found reporting parameters act node.
	 *
	 * @param wrapper wrapper that holds the section
	 * @param node clinical document node
	 */
	private void encodePerformanceYear(JsonWrapper wrapper, Node node) {
		Node reportingDescendant = node.findFirstNode(TemplateId.REPORTING_PARAMETERS_ACT);
		if (reportingDescendant == null) {
			DEV_LOG.error("Missing Reporting Parameters in node hierarchy");
			return;
		}
		String start = reportingDescendant.getValue(ReportingParametersActDecoder.PERFORMANCE_YEAR);
		wrapper.putInteger(ReportingParametersActDecoder.PERFORMANCE_YEAR, start);
		maintainContinuity(wrapper, reportingDescendant, ReportingParametersActDecoder.PERFORMANCE_YEAR);
	}

	/**
	 * Method for encoding each child measurement set
	 *
	 * @param childMapByTemplateId object that represents the document's children
	 * @return encoded measurement sets
	 */
	private JsonWrapper encodeMeasurementSets(Map<TemplateId, Node> childMapByTemplateId, Node currentNode) {
		JsonWrapper measurementSetsWrapper = new JsonWrapper();
		JsonWrapper childWrapper;
		JsonOutputEncoder sectionEncoder;

		for (Node child : childMapByTemplateId.values()) {
			if (child == null) {
				continue;
			}

			try {
				TemplateId childType = child.getType();

				childWrapper = new JsonWrapper();
				sectionEncoder = encoders.get(childType);

				sectionEncoder.encode(childWrapper, child);
				childWrapper.put("source", "qrda3");
				if (TemplateId.MEASURE_SECTION_V4.getRoot().equalsIgnoreCase(childType.getRoot())
					&& ClinicalDocumentDecoder.MIPS_APM.equalsIgnoreCase(
						currentNode.getValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME))) {
					childWrapper.put(ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.MIPS.toLowerCase(Locale.getDefault()));
				} else if (TemplateId.MEASURE_SECTION_V4.getRoot().equalsIgnoreCase(childType.getRoot())
						&& ClinicalDocumentDecoder.APP_APM.equalsIgnoreCase(
						currentNode.getValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME))) {
					childWrapper.put(ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.APP.toLowerCase(Locale.getDefault()));
				}

				measurementSetsWrapper.put(childWrapper);
			} catch (NullPointerException exc) { //NOSONAR NPE can be deep in method calls
				String message = "An unexpected error occured for " + child.getType();
				throw new EncodeException(message, exc);
			}
		}
		return measurementSetsWrapper;
	}
}
