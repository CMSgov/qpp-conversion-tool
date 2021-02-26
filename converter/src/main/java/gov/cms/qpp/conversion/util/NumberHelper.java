package gov.cms.qpp.conversion.util;

import java.math.BigDecimal;

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
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException exc) {
			return false;
		}
	}

	public static boolean isZero(String value) {
		return BigDecimal.ZERO.compareTo(BigDecimal.valueOf(Double.parseDouble(value))) == 0;
	}
}
