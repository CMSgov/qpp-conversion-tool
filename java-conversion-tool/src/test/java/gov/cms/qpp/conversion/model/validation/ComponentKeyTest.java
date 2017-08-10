package gov.cms.qpp.conversion.model.validation;


import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ComponentKeyTest {
	@Test
	public void equality() {
		ComponentKey ck = new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL);
		ComponentKey ck2 = new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL);

		assertTrue("value equality failed", ck.equals(ck2));
		assertTrue("identity equality failed. How?", ck.equals(ck));
	}

	@Test
	public void inequality() {
		ComponentKey ck = new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL);
		ComponentKey ck2 = new ComponentKey(TemplateId.NPI_TIN_ID, Program.ALL);
		ComponentKey ck3 = new ComponentKey(TemplateId.PLACEHOLDER, Program.CPC);

		assertFalse("expected inequality with null reference failed", ck.equals(null));
		assertFalse("expected inequality with different object failed", ck.equals("meep"));
		assertFalse("different templateIds should fail", ck.equals(ck2));
		assertFalse("different programs should fail", ck.equals(ck3));
	}
}
