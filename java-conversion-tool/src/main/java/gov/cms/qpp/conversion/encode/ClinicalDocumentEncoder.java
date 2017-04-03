package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Encoder to serialize the root node of the Document-Level Template: QRDA Category III Report (ClinicalDocument).
 */

@Encoder(templateId = "2.16.840.1.113883.10.20.27.1.2")
public class ClinicalDocumentEncoder extends QppOutputEncoder {

	final Logger log = LoggerFactory.getLogger(getClass());
	/**
	 * internalEncode encodes nodes into Json Wrapper.
	 *
	 * @param wrapper will hold the json format of nodes
	 * @param thisNode holds the decoded node sections of clinical document
	 * @throws EncodeException
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node thisNode) throws EncodeException {

		wrapper.putString("programName", thisNode.getValue("programName"));
		wrapper.putString("entityType", "individual");
		wrapper.putString("taxpayerIdentificationNumber", thisNode.getValue("taxpayerIdentificationNumber"));
		wrapper.putString("nationalProviderIdentifier", thisNode.getValue("nationalProviderIdentifier"));

		Map<String, Node> childMapByTemplateId = thisNode.getChildNodes().stream().collect(
				Collectors.toMap(Node::getId, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

		Optional<Node> reportingNode
				= Optional.ofNullable(childMapByTemplateId.remove("2.16.840.1.113883.10.20.27.2.6"))
				.flatMap(rp -> rp.getChildNodes().stream().findFirst());

		Optional<String> performanceStart = reportingNode.flatMap(p -> Optional.of(p.getValue("performanceStart")));
		Optional<String> performanceEnd = reportingNode.flatMap(p -> Optional.of(p.getValue("performanceEnd")));

		if (performanceStart.isPresent()) {
			wrapper.putInteger("performanceYear", performanceStart.get().substring(0, 4));
		}

		wrapper.putObject("measurementSets", encodeMeasurementSets(childMapByTemplateId, performanceStart, performanceEnd));

	}

	private JsonWrapper encodeMeasurementSets(Map<String, Node> childMapByTemplateId,
											  Optional<String> performanceStart,
											  Optional<String> performanceEnd) throws EncodeException {
		JsonWrapper measurementSetsWrapper = new JsonWrapper();
		JsonWrapper childWrapper;
		JsonOutputEncoder sectionEncoder;

		for (Node child : childMapByTemplateId.values()) {
			childWrapper = new JsonWrapper();
			sectionEncoder = encoders.get(child.getId());

			// Section encoder is null when a decoder exists without a corresponding encoder
			if (null != sectionEncoder) { // currently don't have a set of IA Encoders, but this will protect against others

				sectionEncoder.encode(childWrapper, child);

				childWrapper.putString("source", "provider");
				if (performanceStart.isPresent()) {
					childWrapper.putDate("performanceStart", performanceStart.get());
				}
				if (performanceEnd.isPresent()) {
					childWrapper.putDate("performanceEnd", performanceEnd.get());
				}

				measurementSetsWrapper.putObject(childWrapper);
			} else {
				log.warn("No encoder for decoder : " + child.getId());
			}
		}
		return measurementSetsWrapper;
	}

}
