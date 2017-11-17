package gov.cms.qpp.conversion.model;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;

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

	@Test
	public void testIsCpcPlusForNullThrowsNullPointerException() {
		Assertions.assertThrows(NullPointerException.class, () -> Program.isCpc(null));
	}

	@Test
	public void testIsCpcPlusForNullStringIsFalse() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, null);
		Truth.assertThat(Program.isCpc(node)).isFalse();
	}

	@Test
	public void testIsCpcPlusForRandomStringIsFalse() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "some fake mock value");
		Truth.assertThat(Program.isCpc(node)).isFalse();
	}

	@Test
	public void testIsCpcPlusForMipsIsFalse() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "MIPS");
		Truth.assertThat(Program.isCpc(node)).isFalse();
	}

	@Test
	public void testIsCpcPlusForCpcplusUppercaseIsTrue() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "CPCPLUS");
		Truth.assertThat(Program.isCpc(node)).isTrue();
	}

	@Test
	public void testIsCpcPlusForCpcplusLowercaseIsTrue() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "cpcplus");
		Truth.assertThat(Program.isCpc(node)).isTrue();
	}

	@Test
	public void testIsCpcPlusForCpcplusMixedCaseIsTrue() {
		Node node = new Node();
		node.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "cPcPlUs");
		Truth.assertThat(Program.isCpc(node)).isTrue();
	}
}
