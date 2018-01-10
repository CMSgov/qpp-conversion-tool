package gov.cms.qpp.conversion.api.config;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.test.MockitoExtension;
import gov.cms.qpp.test.PropertiesTestSuite;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncConfigTest extends PropertiesTestSuite {

	@InjectMocks
	private AsyncConfig objectUnderTest;

	@Mock
	private Environment environment;

	@Test
	void testIsSyncIfPropertyIsPresent() {
		when(environment.containsProperty(eq(Constants.USE_SYNC_EXECUTOR))).thenReturn(true);
		TaskExecutor executor = objectUnderTest.taskExecutor();
		assertThat(executor).isInstanceOf(SyncTaskExecutor.class);
	}

	@Test
	void testIsAsyncIfPropertyIsAbsent() {
		TaskExecutor executor = objectUnderTest.taskExecutor();
		assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);
	}
}
