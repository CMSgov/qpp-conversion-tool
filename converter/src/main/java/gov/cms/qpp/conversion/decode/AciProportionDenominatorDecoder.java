package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Decoder to parse Advancing Care Information Numerator Denominator Type
 * Measure Denominator Data.
 */
@Decoder(TemplateId.PI_DENOMINATOR)
public class AciProportionDenominatorDecoder extends SkeletalNameDecoder {

	public AciProportionDenominatorDecoder(Context context) {
		super(context, "aciProportionDenominator");
	}

}
