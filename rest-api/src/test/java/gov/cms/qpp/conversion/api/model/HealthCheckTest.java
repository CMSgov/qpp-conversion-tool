package gov.cms.qpp.conversion.api.model;


import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

class HealthCheckTest {

	@Test
	void testEqualsContract() {
		EqualsVerifier.forClass(HealthCheck.class)
				.usingGetClass()
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

}
