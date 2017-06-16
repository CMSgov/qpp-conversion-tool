package gov.cms.qpp.conversion.encode;

import static gov.cms.qpp.conversion.Converter.CLIENT_LOG;

import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Encoder to serialize reporting paramters
 */
@Encoder(TemplateId.REPORTING_PARAMETERS_ACT)
public class ReportingParametersActEncoder extends QppOutputEncoder {

	public static final String PERFORMANCE_START = "performanceStart";
	public static final String PERFORMANCE_END = "performanceEnd";
	private static final Logger DEV_LOG = LoggerFactory.getLogger(ReportingParametersActEncoder.class);

	/**
	 * Copies the reporting parameters performance start and performance end to the output
	 *
	 * @param wrapper JsonWrapper
	 * @param node Node
	 * @throws EncodeException
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		encodeDate(wrapper, node, PERFORMANCE_START);
		encodeDate(wrapper, node, PERFORMANCE_END);
	}

	/**
	 * Will encode the performance parameter for the specified key
	 * @param wrapper JsonWrapper
	 * @param node parent Node internal representation of xml element
	 * @param key one of either PERFORMANCE_START, or PERFORMANCE_END
	 */
	private void encodeDate(JsonWrapper wrapper, Node node, String key) {
		String date = node.getValue(key);
		try {
			wrapper.putDate(key, date);
		} catch (EncodeException | NullPointerException | DateTimeParseException dtpe) {
			final String message = "Error parsing reporting parameter " + key;
			CLIENT_LOG.error(message);
			DEV_LOG.error(message, dtpe);
			wrapper.putString(key, date);
		}
	}
}
