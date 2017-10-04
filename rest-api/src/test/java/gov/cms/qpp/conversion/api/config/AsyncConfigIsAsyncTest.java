package gov.cms.qpp.conversion.api.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AsyncConfigIsAsyncTest {

	@Autowired
	private TaskExecutor executor;

	@Test
	public void initializedConfig() {
		Assert.assertTrue("Executor should be ThreadPoolTaskExecutor, was " + executor, executor instanceof ThreadPoolTaskExecutor);
	}
}
