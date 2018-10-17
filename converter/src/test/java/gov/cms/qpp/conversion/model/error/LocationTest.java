package gov.cms.qpp.conversion.model.error;

import static com.google.common.truth.Truth.assertThat;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

class LocationTest {

	@Test
	void equalsContract() {
		EqualsVerifier.forClass(Location.class).usingGetClass().suppress(Warning.NONFINAL_FIELDS).verify();
	}

	@Test
	void testSetters() {
		Location location = new Location();
		location.setPath(UUID.randomUUID().toString());
		location.setLocation(UUID.randomUUID().toString());
		location.setLine(ThreadLocalRandom.current().nextInt());
		location.setColumn(ThreadLocalRandom.current().nextInt());
		Location copy = new Location(location);

		assertThat(location).isEqualTo(copy);
	}

}
