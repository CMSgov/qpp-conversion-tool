package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encoder to serialize reporting paramters
 */
@Encoder(TemplateId.REPORTING_PARAMETERS_ACT)
public class ReportingParametersActEncoder extends QppOutputEncoder {

	public static final String PERFORMANCE_START = "performanceStart";
	public static final String PERFORMANCE_END = "performanceEnd";
	private static final Logger DEV_LOG = LoggerFactory.getLogger(ReportingParametersActEncoder.class);

	public ReportingParametersActEncoder(Context context) {
		super(context);
	}

	/**
	 * Copies the reporting parameters performance start and performance end to the output
	 *
	 * @param wrapper JsonWrapper
	 * @param node Node
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
		} catch (RuntimeException dtpe) {
			final String message = "Error parsing reporting parameter " + key;
			DEV_LOG.error(message, dtpe);
			wrapper.putString(key, date);
		}
	}
}
