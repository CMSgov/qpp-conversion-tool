package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.encode.helper.ReportingParameters;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Encoder to serialize the root node of the Document-Level Template: QRDA Category III Report (ClinicalDocument).
 */

@Encoder(TemplateId.CLINICAL_DOCUMENT)
public class ClinicalDocumentEncoder extends QppOutputEncoder {

	/**
	 * internalEncode encodes nodes into Json Wrapper.
	 *
	 * @param wrapper  will hold the json format of nodes
	 * @param thisNode holds the decoded node sections of clinical document
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node thisNode) {
		wrapper.putString("programName", thisNode.getValue("programName"));
		wrapper.putString("entityType", thisNode.getValue("entityType"));
		wrapper.putString("taxpayerIdentificationNumber", thisNode.getValue("taxpayerIdentificationNumber"));
		wrapper.putString("nationalProviderIdentifier", thisNode.getValue("nationalProviderIdentifier"));

		Map<String, Node> childMapByTemplateId = thisNode.getChildNodes().stream().collect(
			Collectors.toMap(Node::getId, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
		Optional<Node> reportingNode = ReportingParameters.getReportingNode(childMapByTemplateId);

		Optional<String> performanceStart = reportingNode.flatMap(p -> Optional.of(p.getValue("performanceStart")));
		Optional<String> performanceEnd = reportingNode.flatMap(p -> Optional.of(p.getValue("performanceEnd")));

		if (performanceStart.isPresent()) {
			wrapper.putInteger("performanceYear", performanceStart.get().substring(0, 4));
		}

		JsonWrapper measurementSets =
			encodeMeasurementSets(childMapByTemplateId, performanceStart, performanceEnd);
		wrapper.putObject("measurementSets", measurementSets);
	}

	/**
	 * Method for encoding each child measurement set
	 *
	 * @param childMapByTemplateId object that represents the document's children
	 * @param performanceStart object that represents the measurement performance start
	 * @param performanceEnd object that represents the measurement performance end
	 * @return
	 * @throws EncodeException If error occurs during encoding
	 */
	private JsonWrapper encodeMeasurementSets(Map<String, Node> childMapByTemplateId,
												Optional<String> performanceStart,
												Optional<String> performanceEnd) {
		JsonWrapper measurementSetsWrapper = new JsonWrapper();
		JsonWrapper childWrapper;
		JsonOutputEncoder sectionEncoder;

		for (Node child : childMapByTemplateId.values()) {
			childWrapper = new JsonWrapper();
			sectionEncoder = ENCODERS.get(child.getId());
			if ( sectionEncoder == null ){
				continue; //MultiTINS is not a real encoder.
			}
			// Section encoder is null when a decoder exists without a corresponding encoder
			// currently don't have a set of IA Encoders, but this will protect against others
			try {
				sectionEncoder.encode(childWrapper, child);

				childWrapper.putString("source", "provider");
				if (performanceStart.isPresent()) {
					childWrapper.putDate("performanceStart", performanceStart.get());
				}
				if (performanceEnd.isPresent()) {
					childWrapper.putDate("performanceEnd", performanceEnd.get());
				}

				measurementSetsWrapper.putObject(childWrapper);
			} catch (NullPointerException exc) {
				String message = "No encoder for decoder : " + child.getId();
				throw new EncodeException(message, exc);
			}
		}

		return measurementSetsWrapper;
	}

}
