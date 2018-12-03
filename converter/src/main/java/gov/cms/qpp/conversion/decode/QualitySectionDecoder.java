package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Decoder to read XML Data for an Quality Section (eCQM).
 */
@Decoder(TemplateId.MEASURE_SECTION_V2)
public class QualitySectionDecoder extends SkeletalSectionDecoder {

	public QualitySectionDecoder(Context context) {
		super(context, "quality");
	}

}
