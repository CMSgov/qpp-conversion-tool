package gov.cms.qpp.conversion.encode.placeholder;

import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encoder used as a placeholder when a fully implemented encoder has yet to be
 * developed.
 */
public class DefaultEncoder extends JsonOutputEncoder {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(DefaultEncoder.class);

	private final String description;

	public DefaultEncoder(String description) {
		this.description = description;
	}

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		DEV_LOG.debug("Default JSON encoder {} is handling templateId {} and is described as '{}' ",
				getClass(), node.getType().name(), description);

		JsonWrapper childWrapper = new JsonWrapper();

		for (Node child : node.getChildNodes()) { // LIST encoding for this node
			//childWrapper.put(child.getType().name(), childWrapper); // cyclic self reference
			encode(childWrapper, child); // child LIST entry for this node
		}
		
		for (String name : node.getKeys()) { // MAP encoding
			String nameForEncode = name.replace("Decoder", "Encoder");
			childWrapper.put(nameForEncode, node.getValue(name));
		}

		wrapper.put(node.getType().name(), childWrapper);

	}

	@Encoder(TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS)
	public static class ContinuousVariableMeasureValueCmsEncoder extends DefaultEncoder {

		public ContinuousVariableMeasureValueCmsEncoder() {
			super("Continuous Variable Measure Value - CMS");
		}
	}

	@Encoder(TemplateId.REPORTING_STRATUM_CMS)
	public static class ReportingStratumCmsEncoder extends DefaultEncoder {

		public ReportingStratumCmsEncoder() {
			super("Reporting Stratum - CMS");
		}
	}
}
