package gov.cms.qpp.conversion.util;

public class NumberHelper {
	//
	private NumberHelper() {}

	/**
	 * Help method to check for numerics
	 *
	 * @param value
	 * @return
	 */
	public static boolean isNumeric(String value) {
		return value.matches("-?\\d+");
	}
}
