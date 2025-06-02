package gov.cms.qpp.conversion.encode;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import static gov.cms.qpp.conversion.model.Constants.*;

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
		String entityType = thisNode.getValue(ENTITY_TYPE);
		encodePerformanceYear(wrapper, thisNode);
		wrapper.put(ENTITY_TYPE, entityType);
		if (!ENTITY_APM.equals(entityType)
			&& !ENTITY_SUBGROUP.equalsIgnoreCase(entityType)) {
			wrapper.put(NATIONAL_PROVIDER_IDENTIFIER,
				thisNode.getValue(NATIONAL_PROVIDER_IDENTIFIER));
			wrapper.put(TAX_PAYER_IDENTIFICATION_NUMBER,
				thisNode.getValue(TAX_PAYER_IDENTIFICATION_NUMBER));
		}

		if (Program.isPcf(thisNode)) {
			wrapper.put(ENTITY_ID, thisNode.getValue(PCF_ENTITY_ID));
		}

		if (ENTITY_VIRTUAL_GROUP.equals(entityType)) {
			wrapper.put(ENTITY_ID, thisNode.getValue(ENTITY_ID));
		}

		if ((Program.isApp(thisNode) || Program.isMips(thisNode) &&
			ENTITY_APM.equalsIgnoreCase(entityType))) {
			wrapper.put(ENTITY_ID, thisNode.getValue(ENTITY_ID));
		}

		if (ENTITY_SUBGROUP.equalsIgnoreCase(entityType)) {
			wrapper.put(ENTITY_ID, thisNode.getValue(SUBGROUP_ID));
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
		String start = reportingDescendant.getValue(PERFORMANCE_YEAR);
		wrapper.putInteger(PERFORMANCE_YEAR, start);
		maintainContinuity(wrapper, reportingDescendant, PERFORMANCE_YEAR);
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
				String mvpId = currentNode.getValue(MVP_ID);
				if (TemplateId.MEASURE_SECTION_V5.getRoot().equalsIgnoreCase(childType.getRoot())
					&& !StringUtils.isEmpty(mvpId)) {
					childWrapper.put(PROGRAM_NAME, mvpId);
				}
				else if (TemplateId.MEASURE_SECTION_V5.getRoot().equalsIgnoreCase(childType.getRoot())
					&& MIPS_APM.equalsIgnoreCase(
						currentNode.getValue(RAW_PROGRAM_NAME))) {
					childWrapper.put(PROGRAM_NAME, MIPS.toLowerCase(Locale.getDefault()));
				} else if (TemplateId.MEASURE_SECTION_V5.getRoot().equalsIgnoreCase(childType.getRoot())
						&& APP_APM.equalsIgnoreCase(
						currentNode.getValue(RAW_PROGRAM_NAME))) {
					childWrapper.put(PROGRAM_NAME, APP_PROGRAM_NAME.toLowerCase(Locale.getDefault()));
				} else if (TemplateId.MEASURE_SECTION_V5.getRoot().equalsIgnoreCase(childType.getRoot())
						&& APP_PLUS_APM.equalsIgnoreCase(
						currentNode.getValue(RAW_PROGRAM_NAME))) {
					childWrapper.put(PROGRAM_NAME, APP_PLUS_PROGRAM_NAME.toLowerCase(Locale.getDefault()));
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
