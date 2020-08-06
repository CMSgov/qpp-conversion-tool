package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Decoder to read XML Data for an Quality Section (eCQM).
 */
@Decoder(TemplateId.MEASURE_SECTION_V4)
public class QualitySectionDecoder extends SkeletalSectionDecoder {

	protected static final String QUALITY_SECTION = "quality";
	public static final String CATEGORY_SECTION_V4 = "clinicalDocumentV4";

	public QualitySectionDecoder(Context context) {
		super(context, QUALITY_SECTION);
	}

}
