package gov.cms.qpp.conversion.api.config;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.test.PropertiesTestSuite;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static com.google.common.truth.Truth.assertWithMessage;

public class AsyncConfigTest extends PropertiesTestSuite {

	private AsyncConfig config;

	@Before
	public void setup() {
		config = new AsyncConfig();
	}

	@Test
	public void testIsSyncIfPropertyIsPresent() {
		System.setProperty(Constants.USE_SYNC_EXECUTOR, "anything blah blah blah");
		TaskExecutor executor = config.taskExecutor();
		assertWithMessage("Executor should be SyncExecutor, was " + executor)
				.that(executor).isInstanceOf(SyncTaskExecutor.class);
	}

	@Test
	public void testIsAsyncIfPropertyIsAbsent() {
		TaskExecutor executor = config.taskExecutor();
		assertWithMessage("Executor should be ThreadPoolTaskExecutor, was " + executor)
				.that(executor).isInstanceOf(ThreadPoolTaskExecutor.class);
	}

}
