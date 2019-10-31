package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Program;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class ContextTest {

	@Test
	void testDoesValidationByDefault() {
		assertThat(new Context().isDoValidation()).isTrue();
	}

	@Test
	void testIsNotHistoricalByDefault() {
		assertThat(new Context().isHistorical()).isFalse();
	}

	@Test
	void testIsDoValidationSetter() {
		Context context = new Context();
		context.setDoValidation(false);
		assertThat(context.isDoValidation()).isFalse();
	}

	@Test
	void testIsHistoricalSetter() {
		Context context = new Context();
		context.setHistorical(true);
		assertThat(context.isHistorical()).isTrue();
	}

	@Test
	void testProgramIsAllByDefault() {
		assertThat(new Context().getProgram())
				.isSameInstanceAs(Program.ALL);
	}

	@Test
	void testProgramSetter() {
		Context context = new Context();
		context.setProgram(Program.MIPS);
		assertThat(context.getProgram())
				.isSameInstanceAs(Program.MIPS);
	}

	@Test
	void testGetRegistryReturnsValid() {
		assertThat(new Context().getRegistry(Decoder.class)).isNotNull();
	}

	@Test
	void testGetRegistryIdentity() {
		Context context = new Context();
		assertThat(context.getRegistry(Decoder.class))
				.isSameInstanceAs(context.getRegistry(Decoder.class));
	}

}