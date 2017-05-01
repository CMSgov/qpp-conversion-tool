package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Optional;

/**
 * Encoder to serialize Quality Measure Identifier
 */
@Encoder(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdEncoder extends QppOutputEncoder {

	private static final String MEASURE_ID = "measureId";
	private static final String AGGREGATE_COUNT = "aggregateCount";

	/**
	 * Encodes an Quality Measure Id into the QPP format
	 *
	 * @param wrapper JsonWrapper that will represent the Quality Measure Identifier
	 * @param node    Node that represents the Quality Measure Identifier
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) {
		wrapper.putString(MEASURE_ID, node.getValue(MEASURE_ID));
		encodeChildren(wrapper, node);
	}

	/**
	 * Encode child nodes.
	 *
	 * @param wrapper holder for encoded node data
	 * @param parentNode holder of the Quality Measures
	 */
	private void encodeChildren(JsonWrapper wrapper, Node parentNode) {
		JsonWrapper childWrapper = new JsonWrapper();
		String type = "type";

		Node populationNode = parentNode.findChildNode(n -> "IPOP".equals(n.getValue(type)));
		Node numeratorNode = parentNode.findChildNode(n -> "NUMER".equals(n.getValue(type)));
		Node denomExclusionNode = parentNode.findChildNode(n -> "DENEX".equals(n.getValue(type)));
		Node denominatorNode = parentNode.findChildNode(n -> "DENOM".equals(n.getValue(type)));

		childWrapper.putBoolean("isEndToEndReported", "true");

		Optional.ofNullable(populationNode).ifPresent(
				node -> childWrapper.putInteger("populationTotal",
						node.getChildNodes().get(0).getValue(AGGREGATE_COUNT)));

		Optional.ofNullable(numeratorNode).ifPresent(
				node -> childWrapper.putInteger("performanceMet",
						node.getChildNodes().get(0).getValue(AGGREGATE_COUNT)));

		Optional.ofNullable(denomExclusionNode).ifPresent(
				node -> childWrapper.putInteger("performanceExclusion",
						node.getChildNodes().get(0).getValue(AGGREGATE_COUNT)));

		Optional.ofNullable(calculatePerformanceNotMet(denominatorNode, denomExclusionNode)).ifPresent(
				performanceNotMet -> childWrapper.putInteger("performanceNotMet", performanceNotMet));

		wrapper.putObject("value", childWrapper);
	}

	/**
	 * Calculates performance not met
	 *
	 * @param denominatorNode holder of the denominator aggregate count value
	 * @param denomExclusionNode holder of the denominator exclusion aggregate count value
	 * @return the calculation
	 */
	private String calculatePerformanceNotMet(Node denominatorNode, Node denomExclusionNode) {
		if (null == denominatorNode || null == denomExclusionNode) {
			return null;
		}

		String denominatorValue = denominatorNode.getChildNodes().get(0).getValue(AGGREGATE_COUNT);
		String denomExclusionValue = denomExclusionNode.getChildNodes().get(0).getValue(AGGREGATE_COUNT);

		return Integer.toString(Integer.parseInt(denominatorValue) - Integer.parseInt(denomExclusionValue));
	}

}
