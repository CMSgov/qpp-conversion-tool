package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Encoder to serialize the root node of the Document-Level Template: QRDA
 * Category III Report (ClinicalDocument).
 *
 * @author Scott Fradkin
 *
 */

@Encoder(templateId = "2.16.840.1.113883.10.20.27.1.2")
public class ClinicalDocumentEncoder extends QppOutputEncoder {

	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {

		wrapper.putString("programName", node.getValue("programName"));
		wrapper.putString("entityType", "individual");
		wrapper.putString("taxpayerIdentificationNumber", node.getValue("taxpayerIdentificationNumber"));
		wrapper.putString("nationalProviderIdentifier", node.getValue("nationalProviderIdentifier"));

		Map<String, Node> childMapByTemplateId = node.getChildNodes().stream().collect(
				Collectors.toMap(Node::getId, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

		Optional<Node> reportingNode
				= Optional.ofNullable(childMapByTemplateId.remove("2.16.840.1.113883.10.20.27.2.6"))
				.flatMap(rp -> rp.getChildNodes().stream().findFirst());

		Optional<String> performanceStart = reportingNode.flatMap(p -> Optional.of(p.getValue("performanceStart")));
		Optional<String> performanceEnd = reportingNode.flatMap(p -> Optional.of(p.getValue("performanceEnd")));

		if (performanceStart.isPresent()) {
			wrapper.putInteger("performanceYear", performanceStart.get().substring(0, 4));
		}

		JsonWrapper measurementSetsWrapper = new JsonWrapper();

		JsonWrapper childWrapper;
		for (Node child : childMapByTemplateId.values()) {
			childWrapper = new JsonWrapper();
			JsonOutputEncoder sectionEncoder = ENCODERS.get(child.getId());

			if (null != sectionEncoder) { // currently don't have a set of IA
				// Encoders, but this will protect
				// against others

				sectionEncoder.encode(childWrapper, child);

				childWrapper.putString("source", "provider");
				if (performanceStart.isPresent()) {
					childWrapper.putDate("performanceStart", performanceStart.get());
				}
				if (performanceEnd.isPresent()) {
					childWrapper.putDate("performanceEnd", performanceEnd.get());
				}

				measurementSetsWrapper.putObject(childWrapper);
			}
		}
		wrapper.putObject("measurementSets", measurementSetsWrapper);
	}

}
