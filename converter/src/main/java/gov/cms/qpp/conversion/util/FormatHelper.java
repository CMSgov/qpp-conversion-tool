package gov.cms.qpp.conversion.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatHelper {
	private static final String DATE_FORMAT = "yyyyMMdd";
	
	private FormatHelper(){}

	/**
	 * Formats the date and parses into a {@link LocalDate}.
	 *
	 * @param date String to be parsed into a date.
	 * @return LocalDate parsed from the given string
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
