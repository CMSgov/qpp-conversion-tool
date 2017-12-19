package gov.cms.qpp.conversion;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

abstract class SourceTestSuite {

	private final String expectedName;
	protected final Source source;

	SourceTestSuite(String expectedName, Source source) {
		this.expectedName = expectedName;
		this.source = source;
	}

	@Test
	final void testExpectedName() {
		assertThat(expectedName).isEqualTo(source.getName());
	}

}