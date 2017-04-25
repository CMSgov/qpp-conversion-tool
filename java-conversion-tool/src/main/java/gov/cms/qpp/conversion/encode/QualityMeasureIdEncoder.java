package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

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
	}

}
