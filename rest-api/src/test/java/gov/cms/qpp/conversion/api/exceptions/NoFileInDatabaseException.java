package gov.cms.qpp.conversion.api.exceptions;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class NoFileInDatabaseExceptionTest {

	@Test
	void testConstructor() {
		NoFileInDatabaseException noFileInDatabaseException = new NoFileInDatabaseException("test");


		assertThat(noFileInDatabaseException).hasMessageThat().isEqualTo("test");
	}
}
