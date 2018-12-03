package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SupplementalData.SupplementalType;

/**
 * Decoder for Supplemental Data Payer Element
 */
@Decoder(TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2)
public class SupplementalDataPayerDecoder extends SkeletalSupplementalDataDecoder {

	public SupplementalDataPayerDecoder(Context context) {
		super(context, SupplementalType.PAYER);
	}

}
