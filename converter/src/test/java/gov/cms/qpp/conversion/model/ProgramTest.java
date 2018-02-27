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
					.that(Program.getInstance(mip)).isSameAs(Program.MIPS)
		);
	}

	@Test
	void instanceRetrievalCpcPlus() {
		assertWithMessage("Program other than %s was returned", Program.CPC)
				.that(Program.getInstance("CPCPLUS")).isSameAs(Program.CPC);
	}

	@Test
	void instanceRetrievalDefault() {
		assertWithMessage("Program other than %s was returned", Program.ALL)
				.that(Program.getInstance("meep")).isSameAs(Program.ALL);
	}

	@Test
	void instanceRetrievalNullProgramName() {
		assertWithMessage("Program other than %s was returned", Program.ALL)
				.that(Program.getInstance(null)).isSameAs(Program.ALL);
	}

	@Test
	void testIsCpcPlusForNullThrowsNullPointerException() {
		Assertions.assertThrows(NullPointerException.class, () -> Program.isCpc(null));
	}

	@Test
	void testIsCpcPlusForNullStringIsFalse() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, null);
		assertThat(Program.isCpc(node)).isFalse();
	}

	@Test
	void testIsCpcPlusForRandomStringIsFalse() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "some fake mock value");
		assertThat(Program.isCpc(node)).isFalse();
	}

	@Test
	void testIsCpcPlusForMipsIsFalse() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "MIPS_INDIV");
		assertThat(Program.isCpc(node)).isFalse();
	}

	@Test
	void testIsCpcPlusForCpcplusUppercaseIsTrue() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "CPCPLUS");
		assertThat(Program.isCpc(node)).isTrue();
	}

	@Test
	void testIsCpcPlusForCpcplusLowercaseIsTrue() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "cpcplus");
		assertThat(Program.isCpc(node)).isTrue();
	}

	@Test
	void testIsCpcPlusForCpcplusMixedCaseIsTrue() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "cPcPlUs");
		assertThat(Program.isCpc(node)).isTrue();
	}

	@Test
	void testExtractProgramForMips() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "MIPS_INDIV");
		assertThat(Program.extractProgram(node)).isEqualTo(Program.MIPS);
	}

	@Test
	void testSetOfProgramNames() {
		Set<String> actual = Program.setOfAliases();

		Set<String> expected = new HashSet<>();
		for (Program program : Program.values()) {
			expected.addAll(program.getAliases());
		}

		assertThat(actual).containsAllIn(expected);
	}

	@Override
	public Class<? extends Enum<?>> getEnumType() {
		return Program.class;
	}
}
