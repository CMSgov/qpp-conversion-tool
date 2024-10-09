package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Constants;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.util.SubPopulationHelper;

/**
 * Encoder for CMS V2 Measure Data
 */
@Encoder(TemplateId.MEASURE_DATA_CMS_V4)
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
		if (!SubPopulationLabel.IPOP.hasAlias(node.getValue(Constants.MEASURE_TYPE)) &&
			!SubPopulationLabel.NUMEX.hasAlias(node.getValue(Constants.MEASURE_TYPE))) {
			String measureType = node.getValue(Constants.MEASURE_TYPE);
			Node aggCount = node.findFirstNode(TemplateId.PI_AGGREGATE_COUNT);

			String encodeLabel = SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.findPopulation(measureType));
			wrapper.putInteger(encodeLabel, aggCount.getValue(Constants.AGGREGATE_COUNT));
			maintainContinuity(wrapper, aggCount, encodeLabel);
		}
	}
}
