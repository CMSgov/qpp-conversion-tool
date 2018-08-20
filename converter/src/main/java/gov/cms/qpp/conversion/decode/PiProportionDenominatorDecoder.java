package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Decoder to parse Promoting Interoperability Numerator Denominator Type
 * Measure Denominator Data.
 */
@Decoder(TemplateId.PI_DENOMINATOR)
public class PiProportionDenominatorDecoder extends SkeletalNameDecoder {

	public PiProportionDenominatorDecoder(Context context) {
		super(context, "piProportionDenominator");
	}

}
