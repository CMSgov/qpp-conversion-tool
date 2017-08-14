package gov.cms.qpp.conversion;

import org.junit.Assert;
import org.junit.Test;

public abstract class QrdaSourceTestSuite {

	private final String expectedName;
	protected final QrdaSource source;

	public QrdaSourceTestSuite(String expectedName, QrdaSource source) {
		this.expectedName = expectedName;
		this.source = source;
	}

	@Test
	public final void testExpectedName() {
		Assert.assertEquals(expectedName, source.getName());
	}

}