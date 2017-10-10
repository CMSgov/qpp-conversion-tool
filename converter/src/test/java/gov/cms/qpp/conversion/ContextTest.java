package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class ContextTest {

	@Test
	public void testDoesDefaultsByDefault() {
		assertThat(new Context().isDoDefaults()).isTrue();
	}

	@Test
	public void testDoesValidationByDefault() {
		assertThat(new Context().isDoValidation()).isTrue();
	}

	@Test
	public void testIsNotHistoricalByDefault() {
		assertThat(new Context().isHistorical()).isFalse();
	}

	@Test
	public void testIsDoDefaultsSetter() {
		Context context = new Context();
		context.setDoDefaults(false);
		assertThat(context.isDoDefaults()).isFalse();
	}

	@Test
	public void testIsDoValidationSetter() {
		Context context = new Context();
		context.setDoValidation(false);
		assertThat(context.isDoValidation()).isFalse();
	}

	@Test
	public void testIsHistoricalSetter() {
		Context context = new Context();
		context.setHistorical(true);
		assertThat(context.isHistorical()).isTrue();
	}

	@Test
	public void testProgramIsAllByDefault() {
		assertThat(new Context().getProgram())
				.isSameAs(Program.ALL);
	}

	@Test
	public void testProgramSetter() {
		Context context = new Context();
		context.setProgram(Program.MIPS);
		assertThat(context.getProgram())
				.isSameAs(Program.MIPS);
	}

	@Test
	public void testScopeIsEmptyByDefault() {
		assertThat(new Context().getScope()).isEmpty();
	}

	@Test
	public void testGetRegistryReturnsValid() {
		assertThat(new Context().getRegistry(Decoder.class)).isNotNull();
	}

	@Test
	public void testGetRegistryIdentity() {
		Context context = new Context();
		assertThat(context.getRegistry(Decoder.class))
				.isSameAs(context.getRegistry(Decoder.class));
	}

	@Test
	public void testHasScopeIsFalseByDefault() {
		assertThat(new Context().hasScope()).isFalse();
	}

	@Test
	public void testHasScopeIsFalseIfScopeIsNull() {
		Context context = new Context();
		context.setScope(null);
		assertThat(context.hasScope()).isFalse();
	}

	@Test
	public void testHasScopeIsTrueIfScopeIsNotEmpty() {
		Context context = new Context();
		context.getScope().add(QrdaScope.DEFAULTS);
		assertThat(context.hasScope()).isTrue();
	}

}