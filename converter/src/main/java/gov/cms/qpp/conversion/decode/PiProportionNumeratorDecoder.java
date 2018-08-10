package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * Decoder to read XML data for a Numerator Type Measure
 */
@Decoder(TemplateId.PI_NUMERATOR)
public class PiProportionNumeratorDecoder extends SkeletalNameDecoder {

	protected static final String NUMERATOR_NODE_NAME = "piProportionNumerator";

	public PiProportionNumeratorDecoder(Context context) {
		super(context, NUMERATOR_NODE_NAME);
	}

}
