package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SupplementalData.SupplementalType;

/**
 * Decoder for Supplemental Data Race Element
 */
@Decoder(TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
public class SupplementalDataRaceDecoder extends SkeletalSupplementalDataDecoder {

	public SupplementalDataRaceDecoder(Context context) {
		super(context, SupplementalType.RACE);
	}

}
