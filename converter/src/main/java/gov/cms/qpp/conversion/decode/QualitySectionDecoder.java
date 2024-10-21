package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;

import static gov.cms.qpp.conversion.model.Constants.QUALITY_SECTION;

/**
 * Decoder to read XML Data for an Quality Section (eCQM).
 */
@Decoder(TemplateId.MEASURE_SECTION_V5)
public class QualitySectionDecoder extends SkeletalSectionDecoder {

	public QualitySectionDecoder(Context context) {
		super(context, QUALITY_SECTION);
	}

}
