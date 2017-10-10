package gov.cms.qpp.conversion.model;


import org.junit.Test;

import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertWithMessage;

public class ProgramTest {
	@Test
	public void instanceRetrievalMips() {
		Stream.of("MIPS_GROUP", "MIPS_INDIV", "MIPS").forEach(mip ->
			assertWithMessage("Program other than %s was returned", Program.MIPS)
					.that(Program.getInstance(mip)).isSameAs(Program.MIPS)
		);
	}

	@Test
	public void instanceRetrievalCpcPlus() {
		assertWithMessage("Program other than %s was returned", Program.CPC)
				.that(Program.getInstance("CPCPLUS")).isSameAs(Program.CPC);
	}

	@Test
	public void instanceRetrievalDefault() {
		assertWithMessage("Program other than %s was returned", Program.ALL)
				.that(Program.getInstance("meep")).isSameAs(Program.ALL);
	}

	@Test
	public void instanceRetrievalNullProgramName() {
		assertWithMessage("Program other than %s was returned", Program.ALL)
				.that(Program.getInstance(null)).isSameAs(Program.ALL);
	}
}
