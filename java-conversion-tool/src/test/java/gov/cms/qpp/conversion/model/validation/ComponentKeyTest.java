package gov.cms.qpp.conversion.model.validation;


import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ComponentKeyTest {
	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(ComponentKey.class)
				.usingGetClass()
				.withCachedHashCode("hashCode", "calcHashCode",
						new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL))
				.verify();
	}
}
