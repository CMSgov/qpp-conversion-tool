package gov.cms.qpp.conversion.api.config;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AsyncConfigIsSyncTest {

	@BeforeClass
	public static void setup() {
		System.setProperty(AsyncConfig.USE_SYNC_EXECUTOR, "anything blah blah blah");
	}

	@AfterClass
	public static void teardown() {
		System.getProperties().remove(AsyncConfig.USE_SYNC_EXECUTOR);
	}

	@Autowired
	private TaskExecutor executor;

	@Test
	public void initializedConfig() {
		Assert.assertTrue("Executor should be SyncExecutor, was " + executor, executor instanceof SyncTaskExecutor);
	}
}
