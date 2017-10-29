package gov.cms.qpp.conversion.model.error;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class DetailTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Detail.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}
}