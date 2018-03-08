package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;

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
			Map<SubPopulationLabel, String> measureTypeMapper = initializeMeasureTypeMap();
			String measureType = node.getValue(MEASURE_TYPE);
			Node aggCount = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT);

			String encodeLabel = measureTypeMapper.get(SubPopulationLabel.findPopulation(measureType));
			wrapper.putInteger(encodeLabel, aggCount.getValue(AGGREGATE_COUNT));
			maintainContinuity(wrapper, aggCount, encodeLabel);
		}
	}

	/**
	 * Initializes the measure type map with specific values.
	 *
	 * @return intialized measure type map
	 */
	private Map<SubPopulationLabel, String> initializeMeasureTypeMap() {
		Map<SubPopulationLabel, String> measureTypeMapper = new EnumMap<>(SubPopulationLabel.class);
		measureTypeMapper.put(SubPopulationLabel.NUMER, "performanceMet");
		measureTypeMapper.put(SubPopulationLabel.DENOM, "eligiblePopulation");
		measureTypeMapper.put(SubPopulationLabel.DENEX, "eligiblePopulationExclusion");
		measureTypeMapper.put(SubPopulationLabel.DENEXCEP, "eligiblePopulationException");
		return measureTypeMapper;
	}

}
