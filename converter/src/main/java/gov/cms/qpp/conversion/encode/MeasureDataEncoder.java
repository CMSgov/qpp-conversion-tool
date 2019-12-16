package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.util.SubPopulationHelper;

import java.util.EnumMap;
import java.util.Map;

import static gov.cms.qpp.conversion.decode.AggregateCountDecoder.AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

/**
 * Encoder for CMS V2 Measure Data
 */
@Encoder(TemplateId.MEASURE_DATA_CMS_V2)
public class MeasureDataEncoder extends QppOutputEncoder {

	public MeasureDataEncoder(Context context) {
		super(context);
	}

	/**
	 * internalEncode for measure data
	 *
	 * @param wrapper object that will represent the measure data
	 * @param node object that represents the measure data
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		if (!SubPopulationLabel.IPOP.hasAlias(node.getValue(MEASURE_TYPE))) {
			String measureType = node.getValue(MEASURE_TYPE);
			Node aggCount = node.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);

			String encodeLabel = SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.findPopulation(measureType));
			wrapper.putInteger(encodeLabel, aggCount.getValue(AGGREGATE_COUNT));
			maintainContinuity(wrapper, aggCount, encodeLabel);
		}
	}
}
