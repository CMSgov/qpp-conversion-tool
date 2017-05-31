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

		for (String name : node.getKeys()) {
			String nameForEncode = name.replace("Decoder", "Encoder");
			childWrapper.putString(nameForEncode, node.getValue(name));
		}

		wrapper.putObject(node.getType().name(), childWrapper);

		for (Node child : node.getChildNodes()) {
			childWrapper.putObject(child.getType().name(), childWrapper);
			encode(childWrapper, child);
		}
	}

	// this seems to be handled by 2.16.840.1.113883.10.20.27.3.3

	@Encoder(TemplateId.CMS_AGGREGATE_COUNT)
	public static class CmsAggregateCountEncoder extends DefaultEncoder {
		public CmsAggregateCountEncoder() {
			super("Aggregate Count - CMS");
		}
	}
	//	 this one looks like a node that is not necessary

	@Encoder(TemplateId.MEASURE_SECTION)
	public static class MeasureSectionEncoder extends DefaultEncoder {

		public MeasureSectionEncoder() {
			super("Measure Section");
		}
	}

	@Encoder(TemplateId.REPORTING_PARAMETERS_ACT)
	public static class ReportingParametersActEncoder extends DefaultEncoder {

		public ReportingParametersActEncoder() {
			super("Reporting Parameters Act - CMS (V2)*");
		}
	}

	@Encoder(TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS)
	public static class ContinuousVariableMeasureValueCmsEncoder extends DefaultEncoder {

		public ContinuousVariableMeasureValueCmsEncoder() {
			super("Continuous Variable Measure Value - CMS");
		}
	}

	@Encoder(TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class EthnicitySupplementalDataElementCmsV2Encoder extends DefaultEncoder {

		public EthnicitySupplementalDataElementCmsV2Encoder() {
			super("Ethnicity Supplemental Data Element – CMS (V2)");
		}
	}

	@Encoder(TemplateId.REPORTING_STRATUM_CMS)
	public static class ReportingStratumCmsEncoder extends DefaultEncoder {

		public ReportingStratumCmsEncoder() {
			super("Reporting Stratum - CMS");
		}
	}

	@Encoder(TemplateId.SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class SexSupplementalDataElementalCmsV2Encoder extends DefaultEncoder {

		public SexSupplementalDataElementalCmsV2Encoder() {
			super("Sex Supplemental Data Element - CMS (V2)");
		}
	}

	@Encoder(TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class RaceSupplementalDataElementCmsV2Encoder extends DefaultEncoder {

		public RaceSupplementalDataElementCmsV2Encoder() {
			super("Race Supplemental Data Element - CMS (V2)");
		}
	}

	@Encoder(TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class PayerSupplementalDataElementCmsV2Encoder extends DefaultEncoder {

		public PayerSupplementalDataElementCmsV2Encoder() {
			super("Payer Supplemental Data Element - CMS (V2)");
		}
	}
}
