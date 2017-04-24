package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.encode.helper.ReportingParameters;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Encoder to serialize Quality Section  (eCQM) and it's measures
 */
@Encoder(TemplateId.MEASURE_SECTION_V2)
public class QualitySectionEncoder extends QppOutputEncoder {

	private static final String PERFORMANCE_START = "performanceStart";
	private static final String PERFORMANCE_END = "performanceEnd";
	private static final String CATEGORY = "category";
	private static final String SUBMISSION_METHOD = "submissionMethod";

	/**
	 *  Encodes an Quality Section into the QPP format
	 *
	 * @param wrapper JsonWrapper that will represent the Quality Section
	 * @param node Node that represents the Quality Section and its measurements children if any
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		wrapper.putString(CATEGORY, node.getValue(CATEGORY));

		wrapper.putString(SUBMISSION_METHOD, node.getValue(SUBMISSION_METHOD));

		Map<String, Node> childMapByTemplateId = node.getChildNodes().stream().collect(
				Collectors.toMap(Node::getId, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
		Optional<Node> reportingNode = ReportingParameters.getReportingNode(childMapByTemplateId);

		Optional<String> performanceStart = reportingNode.flatMap(p -> Optional.of(p.getValue(PERFORMANCE_START)));
		Optional<String> performanceEnd = reportingNode.flatMap(p -> Optional.of(p.getValue(PERFORMANCE_END)));

		if (performanceStart.isPresent()) {
			wrapper.putString(PERFORMANCE_START, performanceStart.get());
		}
		if (performanceEnd.isPresent()) {
			wrapper.putString(PERFORMANCE_END, performanceEnd.get());
		}


		List<Node> children = node.getChildNodes();
		JsonWrapper measurementsWrapper = new JsonWrapper();

		encodeChildren(children, measurementsWrapper);
		wrapper.putObject("measurements", measurementsWrapper);
	}

	private void encodeChildren(List<Node> children, JsonWrapper measurementsWrapper) {
		JsonWrapper childWrapper;
		for (Node currentChild : children) {
			childWrapper = new JsonWrapper();
			String templateId = currentChild.getId();
			JsonOutputEncoder childEncoder = ENCODERS.get(templateId);

			if (childEncoder == null) {
				addValidation(templateId, "Failed to find an encoder for template " +
					currentChild.getType().toString());
			} else {
				childEncoder.encode(childWrapper, currentChild);
				measurementsWrapper.putObject(childWrapper);
			}
		}
	}
}
