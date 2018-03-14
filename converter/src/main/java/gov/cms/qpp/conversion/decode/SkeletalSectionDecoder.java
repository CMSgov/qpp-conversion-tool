package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;

public abstract class SkeletalSectionDecoder extends SkeletalKeyValueDecoder {

	public static final String CATEGORY = "category";

	public SkeletalSectionDecoder(Context context, String category) {
		super(context, CATEGORY, category);
	}

}
