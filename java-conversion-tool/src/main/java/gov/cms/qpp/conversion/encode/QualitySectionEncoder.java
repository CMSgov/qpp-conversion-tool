package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Encoder to serialize Quality Section (eCQM) and it's measures.  This class is nearly empty due to the fact that it does the
 * same encoding as its super class {@link gov.cms.qpp.conversion.encode.AciSectionEncoder} but is a different templateId.
 */
@Encoder(TemplateId.MEASURE_SECTION_V2)
public class QualitySectionEncoder extends AciSectionEncoder {

	public QualitySectionEncoder(Context context) {
		super(context);
	}

	//empty as it does the exact same thing as its parent class
}
