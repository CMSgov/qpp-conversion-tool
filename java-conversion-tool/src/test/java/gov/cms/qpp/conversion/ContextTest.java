package gov.cms.qpp.conversion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Program;

public class ContextTest {

	@Test
	public void testDoesDefaultsByDefault() {
		assertTrue(new Context().isDoDefaults());
	}

	@Test
	public void testDoesValidationByDefault() {
		assertTrue(new Context().isDoValidation());
	}

	@Test
	public void testIsNotHistoricalByDefault() {
		assertFalse(new Context().isHistorical());
	}

	@Test
	public void testIsDoDefaultsSetter() {
		Context context = new Context();
		context.setDoDefaults(false);
		assertFalse(context.isDoDefaults());
	}

	@Test
	public void testIsDoValidationSetter() {
		Context context = new Context();
		context.setDoValidation(false);
		assertFalse(context.isDoValidation());
	}

	@Test
	public void testIsHistoricalSetter() {
		Context context = new Context();
		context.setHistorical(true);
		assertTrue(context.isHistorical());
	}

	@Test
	public void testProgramIsAllByDefault() {
		assertSame(Program.ALL, new Context().getProgram());
	}

	@Test
	public void testProgramSetter() {
		Context context = new Context();
		context.setProgram(Program.MIPS);
		assertSame(Program.MIPS, context.getProgram());
	}

	@Test
	public void testScopeIsEmptyByDefault() {
		assertTrue(new Context().getScope().isEmpty());
	}

	@Test
	public void testGetRegistryReturnsValid() {
		assertNotNull(new Context().getRegistry(Decoder.class));
	}

	@Test
	public void testGetRegistryIdentity() {
		Context context = new Context();
		assertSame(context.getRegistry(Decoder.class),
				context.getRegistry(Decoder.class));
	}

}