package gov.cms.qpp.conversion;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public abstract class QrdaSourceTestSuite {

	private final String expectedName;
	protected final QrdaSource source;

	public QrdaSourceTestSuite(String expectedName, QrdaSource source) {
		this.expectedName = expectedName;
		this.source = source;
	}

	@Test
	public final void testExpectedName() {
		assertThat(expectedName).isEqualTo(source.getName());
	}

}