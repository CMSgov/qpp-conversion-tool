package gov.cms.qpp.conversion.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.google.common.truth.Truth.assertThat;

class FormatHelperTest {

	private String VALID_DASH_DATE = "2017-01-01";
	private String VALID_SLASH_DATE = "2017/01/01";
	private String VALID_TIMEZONED_DATE = "2017/01/01T01:45:23.123";
	private LocalDate DATE_COMPARED = LocalDate.of(2017, 01, 01);

	@Test
	void testFormattedDateParseRemovesDashes() {
		LocalDate date = FormatHelper.formattedDateParse(VALID_DASH_DATE);
		assertThat(date).isEqualTo(DATE_COMPARED);
	}

	@Test
	void testFormattedDateParseRemovesSlashes() {
		LocalDate date = FormatHelper.formattedDateParse(VALID_SLASH_DATE);
		assertThat(date).isEqualTo(DATE_COMPARED);
	}

	@Test
	void testFormattedDateParseRemovesTimeAndZone() {
		LocalDate date = FormatHelper.formattedDateParse(VALID_TIMEZONED_DATE);
		assertThat(date).isEqualTo(DATE_COMPARED);
	}
}
