package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;

/**
 * {@link SkeletalKeyValueDecoder} that uses the key {@code name}
 */
public abstract class SkeletalNameDecoder extends SkeletalKeyValueDecoder {

	/**
	 * {@code name} the key used by the {@link SkeletalNameDecoder}
	 */
	public static final String NAME = "name";

	/**
	 * @param context
	 * @param name the value for the {@code name} key
	 */
	public SkeletalNameDecoder(Context context, String name) {
		super(context, NAME, name);
	}

}
