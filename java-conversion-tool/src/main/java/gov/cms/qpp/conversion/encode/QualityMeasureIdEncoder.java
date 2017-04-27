package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.List;

/**
 * Encoder to serialize Quality Measure Identifier
 */
@Encoder(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdEncoder extends QppOutputEncoder {

	private static final String MEASURE_ID = "measureId";

	/**
	 * Encodes an Quality Measure Id into the QPP format
	 *
	 * @param wrapper JsonWrapper that will represent the Quality Measure Identifier
	 * @param node    Node that represents the Quality Measure Identifier
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		wrapper.putString(MEASURE_ID, node.getValue(MEASURE_ID));
		encodeChildren(wrapper, node);
	}

	/**
	 * Encode child nodes.
	 *
	 * @param wrapper holder for encoded node data
	 * @param parentNode holder of the Quality Measures
	 */
	private void encodeChildren(JsonWrapper wrapper, Node parentNode) throws EncodeException {
		Node populationNode = parentNode.findChildNode(n -> n.getValue("type").equals("IPOP"));
		Node denomExclusionNode = parentNode.findChildNode(n -> n.getValue("type").equals("DENEX"));
		Node numeratorNode = parentNode.findChildNode(n -> n.getValue("type").equals("NUMER"));
		Node denominatorNode = parentNode.findChildNode(n -> n.getValue("type").equals("DENOM"));
		String performanceNotMet = calculatePerformanceNotMet(denominatorNode, denomExclusionNode);

		wrapper.putInteger("populationTotal", populationNode.getChildNodes().get(0).getValue("aggregateCount"));
		wrapper.putInteger("performanceMet", numeratorNode.getChildNodes().get(0).getValue("aggregateCount"));
		wrapper.putInteger("performanceNotMet", performanceNotMet);
	}

	/**
	 * Calculates performance not met
	 *
	 * @param denominatorNode holder of the denominator aggregate count value
	 * @param denomExclusionNode holder of the denominator exclusion aggregate count value
	 * @return the calculation
	 */
	private String calculatePerformanceNotMet(Node denominatorNode, Node denomExclusionNode) {
		Integer denominatorValue = Integer.parseInt(denominatorNode.getChildNodes().get(0).getValue("aggregateCount"));
		Integer denomExclusionValue = Integer.parseInt(denomExclusionNode.getChildNodes().get(0).getValue("aggregateCount"));
		return Integer.toString(denominatorValue - denomExclusionValue);
	}

}
