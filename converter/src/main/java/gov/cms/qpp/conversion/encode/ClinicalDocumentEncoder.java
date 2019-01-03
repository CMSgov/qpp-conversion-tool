package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
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

		JsonWrapper measurementSets =
			encodeMeasurementSets(childMapByTemplateId);
		wrapper.putObject(MEASUREMENT_SETS, measurementSets);
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
		wrapper.putString(ClinicalDocumentDecoder.ENTITY_TYPE, entityType);
		wrapper.putString(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER,
				thisNode.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER));
		wrapper.putString(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER,
				thisNode.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER));

		if (ClinicalDocumentDecoder.ENTITY_APM.equals(entityType)) {
			wrapper.putString(ClinicalDocumentDecoder.ENTITY_ID,
				thisNode.getValue(ClinicalDocumentDecoder.PRACTICE_ID));
		}

		if (ClinicalDocumentDecoder.ENTITY_VIRTUAL_GROUP.equals(entityType)) {
			wrapper.putString(ClinicalDocumentDecoder.ENTITY_ID,
				thisNode.getValue(ClinicalDocumentDecoder.ENTITY_ID));
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
	private JsonWrapper encodeMeasurementSets(Map<TemplateId, Node> childMapByTemplateId) {
		JsonWrapper measurementSetsWrapper = new JsonWrapper();
		JsonWrapper childWrapper;
		JsonOutputEncoder sectionEncoder;

		for (Node child : childMapByTemplateId.values()) {
			if (null == child) {
				String message = "An child was missing ";
				throw new EncodeException(message);
			}
			TemplateId childType = child.getType();
			if (null == childType) {
				String message = "An child type was missing ";
				throw new EncodeException(message);
			}

			try {
				childWrapper = new JsonWrapper();
				sectionEncoder = encoders.get(childType);

				if (null == sectionEncoder) {
					String message = "An encoder was not found for type " + child.getType();
					throw new EncodeException(message);
				}
				sectionEncoder.encode(childWrapper, child);

				measurementSetsWrapper.putObject(childWrapper);
			} catch (RuntimeException exc) {
				String message = "An unexpected error occured for " + child.getType();
				throw new EncodeException(message, exc);
			}
		}
		return measurementSetsWrapper;
	}
}
