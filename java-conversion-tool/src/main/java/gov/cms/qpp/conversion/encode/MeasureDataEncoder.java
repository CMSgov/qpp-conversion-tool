package gov.cms.qpp.conversion.encode;


import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import static gov.cms.qpp.conversion.decode.AggregateCountDecoder.AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

@Encoder(TemplateId.MEASURE_DATA_CMS_V2)
public class MeasureDataEncoder extends QppOutputEncoder {

	/**
	 * internalEncode for measure data
	 *
	 * @param wrapper object that will represent the measure data
	 * @param node object that represents the measure data
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		String measureType = node.getValue(MEASURE_TYPE);
		Node aggCount = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());

		wrapper.putInteger(measureType, aggCount.getValue(AGGREGATE_COUNT));
	}

}
