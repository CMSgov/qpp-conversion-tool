package gov.cms.qpp.conversion.util;


import com.rits.cloning.Cloner;

/**
 * Utility that help clone target objects
 */
public class CloneHelper {
	private static final Cloner CACHED = Cloner.standard();

	private CloneHelper(){}

	/**
	 * Create deep copy of given object
	 *
	 * @param in to clone
	 * @param <T> object type
	 * @return clone
	 */
	public static <T> T deepClone(final T in) {
		return CACHED.deepClone(in);
	}
}
