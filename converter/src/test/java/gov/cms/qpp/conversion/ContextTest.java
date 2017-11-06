package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.segmentation.QrdaScope;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class ContextTest {

	@Test
	void testDoesDefaultsByDefault() {
		assertThat(new Context().isDoDefaults()).isTrue();
	}

	@Test
	void testDoesValidationByDefault() {
		assertThat(new Context().isDoValidation()).isTrue();
	}

	@Test
	void testIsNotHistoricalByDefault() {
		assertThat(new Context().isHistorical()).isFalse();
	}

	@Test
	void testIsDoDefaultsSetter() {
		Context context = new Context();
		context.setDoDefaults(false);
		assertThat(context.isDoDefaults()).isFalse();
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
				.isSameAs(Program.ALL);
	}

	@Test
	void testProgramSetter() {
		Context context = new Context();
		context.setProgram(Program.MIPS);
		assertThat(context.getProgram())
				.isSameAs(Program.MIPS);
	}

	@Test
	void testScopeIsEmptyByDefault() {
		assertThat(new Context().getScope()).isEmpty();
	}

	@Test
	void testGetRegistryReturnsValid() {
		assertThat(new Context().getRegistry(Decoder.class)).isNotNull();
	}

	@Test
	void testGetRegistryIdentity() {
		Context context = new Context();
		assertThat(context.getRegistry(Decoder.class))
				.isSameAs(context.getRegistry(Decoder.class));
	}

	@Test
	void testHasScopeIsFalseByDefault() {
		assertThat(new Context().hasScope()).isFalse();
	}

	@Test
	void testHasScopeIsFalseIfScopeIsNull() {
		Context context = new Context();
		context.setScope(null);
		assertThat(context.hasScope()).isFalse();
	}

	@Test
	void testHasScopeIsTrueIfScopeIsNotEmpty() {
		Context context = new Context();
		context.getScope().add(QrdaScope.DEFAULTS);
		assertThat(context.hasScope()).isTrue();
	}

}