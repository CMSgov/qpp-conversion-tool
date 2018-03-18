package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;

/**
 * {@link SkeletalSectionDecoder} that uses the key {@code category}
 */
public abstract class SkeletalSectionDecoder extends SkeletalKeyValueDecoder {

	/**
	 * {@code category} the key used by the {@link SkeletalSectionDecoder}
	 */
	public static final String CATEGORY = "category";

	/**
	 * @param context
	 * @param category the value for the {@code category} key
	 */
	public SkeletalSectionDecoder(Context context, String category) {
		super(context, CATEGORY, category);
	}

}
