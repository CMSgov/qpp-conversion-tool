package gov.cms.qpp.conversion.model.error;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class DetailTest {

	@Test
	void equalsContract() {
		EqualsVerifier.forClass(Detail.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}

	@Test
	void testSetters() {
		Detail detail = new Detail();
		detail.setPath("path");
		detail.setMessage("message");
		detail.setType("type");
		detail.setValue("value");
		Detail otherDetail = new Detail(detail);

		assertThat(detail).isEqualTo(otherDetail);
	}
}