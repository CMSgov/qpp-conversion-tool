package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import gov.cms.qpp.conversion.model.validation.SubPopulations;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.cms.qpp.conversion.decode.AggregateCountDecoder.AGGREGATE_COUNT;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;

/**
 * Encoder for CMS V2 Measure Data
 */
@Encoder(TemplateId.MEASURE_DATA_CMS_V2)
public class MeasureDataEncoder extends QppOutputEncoder {

	protected static final Set<String> IPOP = Stream.of("IPP", "IPOP")
			.collect(Collectors.toSet());

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
		if (!IPOP.contains(node.getValue(MEASURE_TYPE))) {
			Map<String, String> measureTypeMapper = initializeMeasureTypeMap();
			String measureType = node.getValue(MEASURE_TYPE);
			Node aggCount = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT);

			String encodeLabel = measureTypeMapper.get(measureType);
			wrapper.putInteger(encodeLabel, aggCount.getValue(AGGREGATE_COUNT));
			maintainContinuity(wrapper, aggCount, encodeLabel);
		}
	}

	/**
	 * Initializes the measure type map with specific values.
	 *
	 * @return intialized measure type map
	 */
	private Map<String, String> initializeMeasureTypeMap() {
		Map<String , String> measureTypeMapper = new HashMap<>();
		final String eligiblePopulation = "eligiblePopulation";

		measureTypeMapper.put(SubPopulations.NUMER, "performanceMet");
		measureTypeMapper.put(SubPopulations.DENOM, eligiblePopulation);
		measureTypeMapper.put(SubPopulations.DENEX, "eligiblePopulationExclusion");
		measureTypeMapper.put(SubPopulations.DENEXCEP, "eligiblePopulationException");
		return measureTypeMapper;
	}

}
