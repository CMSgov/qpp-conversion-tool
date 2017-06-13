package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeParseException;

import static gov.cms.qpp.conversion.Converter.CLIENT_LOG;

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
		String date = node.getValue(PERFORMANCE_START);
		try {
			wrapper.putDate(PERFORMANCE_START, date);
		} catch (EncodeException | NullPointerException | DateTimeParseException dtpe) {
			final String message = "Error parsing reporting parameter performance start date";
			CLIENT_LOG.error(message);
			DEV_LOG.error(message, dtpe);
			wrapper.putString(PERFORMANCE_START, date);
		}
		date = node.getValue(PERFORMANCE_END);
		try {
			wrapper.putDate(PERFORMANCE_END, date);
		} catch (EncodeException | NullPointerException | DateTimeParseException dtpe) {
			final String message = "Error parsing reporting parameter performance end date";
			CLIENT_LOG.error(message);
			DEV_LOG.error(message, dtpe);
			wrapper.putString(PERFORMANCE_END, date);
		}
	}
}
