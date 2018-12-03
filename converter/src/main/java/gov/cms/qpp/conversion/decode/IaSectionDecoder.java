package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Decoder to parse Improvement Activity Section.
 */
@Decoder(TemplateId.IA_SECTION)
public class IaSectionDecoder extends SkeletalSectionDecoder {

	public IaSectionDecoder(Context context) {
		super(context, "ia");
	}

}
