package gov.cms.qpp.conversion.model.validation;


import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ComponentKeyTest {
	@Test
	public void equality() {
		ComponentKey ck = new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL);
		ComponentKey ck2 = new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL);

		assertEquals("value equality failed", ck, ck2);
		assertEquals("identity equality failed. How?", ck, ck);
	}

	@Test
	public void inequality() {
		ComponentKey ck = new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL);
		ComponentKey ck2 = new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL);
		ComponentKey ck3 = new ComponentKey(TemplateId.NPI_TIN_ID, Program.ALL);
		ComponentKey ck4 = new ComponentKey(TemplateId.PLACEHOLDER, Program.CPC);

		assertNotEquals("expected inequality with different object failed", ck, "meep");
		assertNotEquals("different templateIds should fail", ck, ck3);
		assertNotEquals("different programs should fail", ck, ck4);
	}
}
