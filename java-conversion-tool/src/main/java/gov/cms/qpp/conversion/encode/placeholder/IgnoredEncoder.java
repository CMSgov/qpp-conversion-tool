package gov.cms.qpp.conversion.encode.placeholder;

import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Encoder used as a placeholder when a fully implemented encoder has yet to be
 * developed.
 */
public class IgnoredEncoder extends JsonOutputEncoder {

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		// do nothing
	}

	@Encoder(TemplateId.SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class SexSupplementalDataElementalCmsV2Encoder extends IgnoredEncoder {

	}

	@Encoder(TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class RaceSupplementalDataElementCmsV2Encoder extends IgnoredEncoder {

	}

	@Encoder(TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class PayerSupplementalDataElementCmsV2Encoder extends IgnoredEncoder {

	}

	@Encoder(TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
	public static class EthnicitySupplementalDataElementCmsV2Encoder extends IgnoredEncoder {

	}

	@Encoder(TemplateId.MEASURE_SECTION)
	public static class MeasureSectionEncoder extends DefaultEncoder {

		public MeasureSectionEncoder() {
			super("Measure Section");
		}
	}

}
