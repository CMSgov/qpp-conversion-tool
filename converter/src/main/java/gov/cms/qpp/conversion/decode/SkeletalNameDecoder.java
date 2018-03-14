package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;

public abstract class SkeletalNameDecoder extends SkeletalKeyValueDecoder {

	public SkeletalNameDecoder(Context context, String name) {
		super(context, "name", name);
	}

}
