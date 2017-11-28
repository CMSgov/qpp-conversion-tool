package gov.cms.qpp.conversion;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;

abstract class QrdaSourceTestSuite {

	private final String expectedName;
	protected final QrdaSource source;

	QrdaSourceTestSuite(String expectedName, QrdaSource source) {
		this.expectedName = expectedName;
		this.source = source;
	}

	@Test
	final void testExpectedName() {
		assertThat(expectedName).isEqualTo(source.getName());
	}

}