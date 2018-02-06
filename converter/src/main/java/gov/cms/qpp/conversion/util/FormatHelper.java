package gov.cms.qpp.conversion.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatHelper {
	private static final String DATE_FORMAT = "yyyyMMdd";

	/**
	 * Formats the date and parses into a local date.
	 *
	 * @param date
	 * @return
	 */
	public static LocalDate formattedDateParse(String date) {
		String parse = cleanString(date);
		parse = parse.replace("-", "").replace("/", "");
		if (parse.length() > DATE_FORMAT.length()) {
			parse = parse.substring(0, DATE_FORMAT.length());
		}
		return LocalDate.parse(cleanString(parse), DateTimeFormatter.ofPattern(DATE_FORMAT));
	}

	/**
	 * Remove whitespace and lowercases the string passed in
	 *
	 * @param value to be formatted
	 * @return a cleaned string value
	 */
	public static String cleanString(String value) {
		if (value == null) {
			return "";
		}
		return value.trim().toLowerCase(Locale.ENGLISH);
	}
}
