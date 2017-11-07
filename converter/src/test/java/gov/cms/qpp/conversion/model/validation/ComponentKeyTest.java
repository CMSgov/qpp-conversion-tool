package gov.cms.qpp.conversion.model.validation;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import nl.jqno.equalsverifier.EqualsVerifier;

class ComponentKeyTest {

	@Test
	void equalsContract() {
		EqualsVerifier.forClass(ComponentKey.class)
				.usingGetClass()
				.withCachedHashCode("hashCode", "calcHashCode",
						new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL))
				.verify();
	}
}
