package gov.cms.qpp.conversion.encode;


import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
		String measureType = node.getValue(MEASURE_TYPE);
		Node aggCount = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		Set<String> accepted = new HashSet(Arrays.asList("IPOP", "IPP"));

		if (accepted.contains(measureType)) {
			wrapper.putInteger("initialPopulation", aggCount.getValue(AGGREGATE_COUNT));
		} else if ("DENOM".equals(measureType)) {
			wrapper.putInteger("denominator", aggCount.getValue(AGGREGATE_COUNT));
		} else if ("DENEX".equals(measureType)) {
			wrapper.putInteger("denominatorExclusions", aggCount.getValue(AGGREGATE_COUNT));
		} else if ("DENEXCEP".equals(measureType)) {
			wrapper.putInteger("denominatorExceptions", aggCount.getValue(AGGREGATE_COUNT));
		} else if ("NUMER".equals(measureType)) {
			wrapper.putInteger("numerator", aggCount.getValue(AGGREGATE_COUNT));
		}
	}

}
