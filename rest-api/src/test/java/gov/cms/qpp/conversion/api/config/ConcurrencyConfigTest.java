package gov.cms.qpp.conversion.api.config;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

class ConcurrencyConfigTest {

	@Test
	void testIsSync() {
		TaskExecutor executor = new ConcurrencyConfig().taskExecutor();
		assertThat(executor).isInstanceOf(SyncTaskExecutor.class);
	}
}
