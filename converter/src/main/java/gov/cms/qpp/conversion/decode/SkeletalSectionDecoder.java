package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;

import static gov.cms.qpp.conversion.model.Constants.CATEGORY;

/**
 * {@link SkeletalSectionDecoder} that uses the key {@code category}
 */
public abstract class SkeletalSectionDecoder extends SkeletalKeyValueDecoder {

	/**
	 * @param context
	 * @param category the value for the {@code category} key
	 */
	public SkeletalSectionDecoder(Context context, String category) {
		super(context, CATEGORY, category);
	}

}
