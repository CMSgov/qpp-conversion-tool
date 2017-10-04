package gov.cms.qpp.conversion.api.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import gov.cms.qpp.test.PropertiesTestSuite;

public class AsyncConfigTest extends PropertiesTestSuite {

	private AsyncConfig config;

	@Before
	public void setup() {
		config = new AsyncConfig();
	}

	@Test
	public void testIsSyncIfPropertyIsPresent() {
		System.setProperty(AsyncConfig.USE_SYNC_EXECUTOR, "anything blah blah blah");
		TaskExecutor executor = config.taskExecutor();
		Assert.assertTrue("Executor should be SyncExecutor, was " + executor, executor instanceof SyncTaskExecutor);
	}

	@Test
	public void testIsAsyncIfPropertyIsAbsent() {
		TaskExecutor executor = config.taskExecutor();
		Assert.assertTrue("Executor should be ThreadPoolTaskExecutor, was " + executor, executor instanceof ThreadPoolTaskExecutor);
	}

}
