package gov.cms.qpp.conversion.encode.placeholder;

import gov.cms.qpp.conversion.encode.EncodeException;
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
 *
 */
public class DefaultEncoder extends JsonOutputEncoder {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultEncoder.class);

	private final String description;

	public DefaultEncoder(String description) {
		this.description = description;
	}

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		LOG.debug("Default JSON encoder {} is handling templateId {} and is described as '{}' ",
				getClass(), node.getId(), description);

		// TODO like the decoder this might be better in the parent
		// and we are given the child
		JsonWrapper childWrapper = new JsonWrapper();

		for (String name : node.getKeys()) {
			String nameForEncode = name.replace("Decoder", "Encoder");
			childWrapper.putString(nameForEncode, node.getValue(name));
		}

		wrapper.putObject(node.getId(), childWrapper);

		for (Node child : node.getChildNodes()) {
			childWrapper.putObject(child.getId(), childWrapper);
			encode(childWrapper, child);
		}
	}

	// this one looks like a node that is not necessary
	@Encoder(TemplateId.PERFORMANCE_RATE)
	public static class N_Encoder extends DefaultEncoder {

		public N_Encoder() {
			super("Performance Rate");
		}
	}

	// this seems to be handled by 2.16.840.1.113883.10.20.27.3.3

	@Encoder(TemplateId.CMS_AGGREGATE_COUNT)
	public static class R_Encoder extends DefaultEncoder {
		public R_Encoder() {
			super("Aggregate Count - CMS");
		}
	}
//	 this one looks like a node that is not necessary

	@Encoder(TemplateId.MEASURE_SECTION)
	public static class B_Encoder extends DefaultEncoder {

		public B_Encoder() {
			super("Measure Section");
		}
	}

	// this one looks like a node that is not necessary
	@Encoder(TemplateId.MEASURE_SECTION_V2)
	public static class D_Encoder extends DefaultEncoder {
		public D_Encoder() {
			super("QRDA Category III Measure Section - CMS (V2)");
		}
	}

	@Encoder(TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS)
	public static class I_Encoder extends DefaultEncoder {
		public I_Encoder() {
			super("Advancing Care Information Measure Performed Measure Reference and Results");
		}
	}

	@Encoder(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
	public static class K_Encoder extends DefaultEncoder {
		public K_Encoder() {
			super("Measure Reference and Results - CMS (V2)");
		}
	}

	@Encoder(TemplateId.REPORTING_PARAMETERS_ACT)
	public static class L_Encoder extends DefaultEncoder {

		public L_Encoder() {
			super("Reporting Parameters Act - CMS (V2)*");
		}
	}

	@Encoder(TemplateId.REPORTING_PARAMETERS_ACT)
	public static class S_Encoder extends DefaultEncoder {

		public S_Encoder() {
			super("Continuous Variable Measure Value - CMS");
		}
	}

	@Encoder(TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class T_Encoder extends DefaultEncoder {

		public T_Encoder() {
			super("Ethnicity Supplemental Data Element – CMS (V2)");
		}
	}


	@Encoder(TemplateId.MEASURE_DATA_CMS_V2)
	public static class U_Encoder extends DefaultEncoder {

		public U_Encoder() {
			super("Measure Data - CMS (V2)");
		}
	}

	@Encoder(TemplateId.REPORTING_STRATUM_CMS )
	public static class V_Encoder extends DefaultEncoder {
		public V_Encoder() {
			super("Reporting Stratum - CMS");
		}
	}

	@Encoder(TemplateId.SEX_SUPPLEMENTAL_DATA_ELEMENTAL_CMS_V2)
	public static class W_Encoder extends DefaultEncoder {

		public W_Encoder() {
			super("Sex Supplemental Data Element - CMS (V2)");
		}
	}

	@Encoder(TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class X_Encoder extends DefaultEncoder {

		public X_Encoder() {
			super("Race Supplemental Data Element - CMS (V2)");
		}
	}


	@Encoder(TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class Y_Encoder extends DefaultEncoder {

		public Y_Encoder() {
			super("Payer Supplemental Data Element - CMS (V2)");
		}
	}

	@Encoder(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE_CMS_V2)
	public static class ZZ_Encoder extends DefaultEncoder {

		public ZZ_Encoder() {
			super("Performance Rate for Proportion Measure - CMS (V2)");
		}
	}
	@Encoder(TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS)
	public static class ZZZ_Encoder extends DefaultEncoder {

		public ZZZ_Encoder() {
			super("Performance Rate for CONTINUOUS VARIABLE MEASURE VALUE CMS");
		}
	}

}
