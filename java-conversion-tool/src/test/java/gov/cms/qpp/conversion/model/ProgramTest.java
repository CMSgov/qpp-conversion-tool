package gov.cms.qpp.conversion.model;


import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ProgramTest {
	@Test
	public void instanceRetrievalMips() {
		String[] mips = {"MIPS_GROUP", "MIPS_INDIV", "MIPS"};
		Arrays.stream(mips).forEach(mip -> {
			assertThat("Program other than " + Program.MIPS + " was returned",
					Program.getInstance(mip), is(Program.MIPS));
		});
	}

	@Test
	public void instanceRetrievalCpcPlus() {
		assertThat("Program other than " + Program.CPC + " was returned",
					Program.getInstance("CPCPLUS"), is(Program.CPC));
	}

	@Test
	public void instanceRetrievalDefault() {
		assertThat("Program other than " + Program.ALL + " was returned",
				Program.getInstance("meep"), is(Program.ALL));
	}
}
