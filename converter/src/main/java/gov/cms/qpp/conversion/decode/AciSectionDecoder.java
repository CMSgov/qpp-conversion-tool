package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Decoder to read XML Data for an ACI Section.
 */
@Decoder(TemplateId.ACI_SECTION)
public class AciSectionDecoder extends SkeletalSectionDecoder {

	public AciSectionDecoder(Context context) {
		super(context, "aci");
	}

}
