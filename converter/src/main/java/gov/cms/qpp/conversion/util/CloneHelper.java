package gov.cms.qpp.conversion.util;


import com.rits.cloning.Cloner;

public class CloneHelper {
	private static final Cloner CACHED = Cloner.standard();

	private CloneHelper(){}

	public static <T> T deepClone(final T in) {
		return CACHED.deepClone(in);
	}
}
