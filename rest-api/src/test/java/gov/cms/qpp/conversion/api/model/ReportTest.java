package gov.cms.qpp.conversion.api.model;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.error.Detail;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

class ReportTest {

	@Test
	void testEqualsContract() {
		EqualsVerifier.forClass(Report.class)
				.usingGetClass()
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

	@Test
	void gettersAndSetters_basicFields() {
		Report r = new Report();

		r.setProgramName("MIPS");
		r.setPracticeSiteId("PSI-1");
		r.setTimestamp(123L);

		assertThat(r.getProgramName()).isEqualTo("MIPS");
		assertThat(r.getPracticeSiteId()).isEqualTo("PSI-1");
		assertThat(r.getTimestamp()).isEqualTo(123L);
	}

	@Test
	void setWarnings_copiesInput_and_getWarnings_returnsDefensiveCopy() {
		Report r = new Report();

		Detail d1 = new Detail();
		Detail d2 = new Detail();

		List<Detail> input = new ArrayList<>();
		input.add(d1);

		r.setWarnings(input);

		input.add(d2);
		assertThat(r.getWarnings()).containsExactly(d1);

		List<Detail> returned = r.getWarnings();
		returned.clear();
		assertThat(r.getWarnings()).containsExactly(d1);
	}

	@Test
	void setErrors_copiesInput_and_getErrors_returnsDefensiveCopy() {
		Report r = new Report();

		Detail e1 = new Detail();
		Detail e2 = new Detail();

		List<Detail> input = new ArrayList<>();
		input.add(e1);

		r.setErrors(input);

		input.add(e2);
		assertThat(r.getErrors()).containsExactly(e1);

		List<Detail> returned = r.getErrors();
		returned.clear();
		assertThat(r.getErrors()).containsExactly(e1);
	}

	@Test
	void setWarnings_and_setErrors_null_clearsLists() {
		Report r = new Report();

		r.setWarnings(List.of(new Detail()));
		r.setErrors(List.of(new Detail(), new Detail()));

		assertThat(r.getWarnings()).isNotEmpty();
		assertThat(r.getErrors()).isNotEmpty();

		r.setWarnings(null);
		r.setErrors(null);

		assertThat(r.getWarnings()).isEmpty();
		assertThat(r.getErrors()).isEmpty();
	}
}
