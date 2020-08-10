package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Decoder to read XML Data for an PI Section.
 */
@Decoder(TemplateId.PI_SECTION_V2)
public class PiSectionDecoder extends SkeletalSectionDecoder {

	public PiSectionDecoder(Context context) {
		super(context, "pi");
	}

}
