package gov.cms.qpp.conversion.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.test.enums.EnumContract;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class ProgramTest implements EnumContract {

	@Test
	void instanceRetrievalMips() {
		Stream.of("MIPS_GROUP", "MIPS_INDIV").forEach(mip ->
			assertWithMessage("Program other than %s was returned", Program.MIPS)
					.that(Program.getInstance(mip)).isSameInstanceAs(Program.MIPS)
		);
	}

	@Test
	void instanceRetrievalMipsApp() {
		Stream.of("MIPS_APP1_GROUP", "MIPS_APP1_INDIV").forEach(mip ->
				assertWithMessage("Program other than %s was returned", Program.APP)
						.that(Program.getInstance(mip)).isSameInstanceAs(Program.APP)
		);
	}

	@Test
	void instanceRetrievalPcf() {
		assertWithMessage("Program other than %s was returned", Program.PCF)
			.that(Program.getInstance("PCF")).isSameInstanceAs(Program.PCF);
	}

	@Test
	void instanceRetrievalDefault() {
		assertWithMessage("Program other than %s was returned", Program.ALL)
				.that(Program.getInstance("meep")).isSameInstanceAs(Program.ALL);
	}

	@Test
	void instanceRetrievalNullProgramName() {
		assertWithMessage("Program other than %s was returned", Program.ALL)
				.that(Program.getInstance(null)).isSameInstanceAs(Program.ALL);
	}

	@Test
	void testIsPcfForMixedCaseIsTrue() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "pCf");
		assertThat(Program.isPcf(node)).isTrue();
	}

	@Test
	void testIsPcfUppercaseIsTrue() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "PCF");
		assertThat(Program.isPcf(node)).isTrue();
	}

	@Test
	void testIsPcfLowercaseIsTrue() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "pcf");
		assertThat(Program.isPcf(node)).isTrue();
	}

	@Test
	void testIsAppIndividualIsTrue() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "MIPS_APP1_INDIV");
		assertThat(Program.isApp(node)).isTrue();
	}

	@Test
	void testIsMipsNonAppIndividualFalse() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "MIPS_INDIV");
		assertThat(Program.isApp(node)).isFalse();
	}

	@Test
	void testExtractProgramForMipsInd() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "MIPS_INDIV");
		assertThat(Program.isMips(node)).isTrue();
	}

	@Test
	void testExtractProgramForMipsGroup() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "MIPS_GROUP");
		assertThat(Program.isMips(node)).isTrue();
	}

	@Test
	void testExtractProgramForMipsVirtualGroup() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "MIPS_VIRTUALGROUP");
		assertThat(Program.isMips(node)).isTrue();
	}

	@Test
	void testSetOfProgramNames() {
		Set<String> actual = Program.setOfAliases();

		Set<String> expected = new HashSet<>();
		for (Program program : Program.values()) {
			expected.addAll(program.getAliases());
		}

		assertThat(actual).containsAtLeastElementsIn(expected);
	}

	@Override
	public Class<? extends Enum<?>> getEnumType() {
		return Program.class;
	}
}
