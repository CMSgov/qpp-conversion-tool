package gov.cms.qpp.conversion.encode;


import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.HashMap;
import java.util.Map;

import static gov.cms.qpp.conversion.decode.AggregateCountDecoder.AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

/**
 * Encoder for CMS V2 Measure Data
 */
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
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		Map<String, String> measureTypeMapper = initializeMeasureTypeMap();
		String measureType = node.getValue(MEASURE_TYPE);
		Node aggCount = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT);

		String encodeLabel = measureTypeMapper.get(measureType);
		wrapper.putInteger(encodeLabel, aggCount.getValue(AGGREGATE_COUNT));
		maintainContinuity(wrapper, aggCount, encodeLabel);
	}

	/**
	 * Initializes the measure type map with specific values.
	 *
	 * @return intialized measure type map
	 */
	private Map<String, String> initializeMeasureTypeMap() {
		Map<String , String> measureTypeMapper = new HashMap<>();
		measureTypeMapper.put("IPOP", "initialPopulation");
		measureTypeMapper.put("IPP", "initialPopulation");
		measureTypeMapper.put("DENOM", "denominator");
		measureTypeMapper.put("DENEX", "denominatorExclusions");
		measureTypeMapper.put("DENEXCEP", "denominatorExceptions");
		measureTypeMapper.put("NUMER", "numerator");
		return measureTypeMapper;
	}

}
