package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import gov.cms.qpp.conversion.api.model.Metadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class DbServiceImplTest {

	@InjectMocks
	private DbServiceImpl underTest;

	@Mock
	private DynamoDBMapper dbMapper;

	@Mock
	private TaskExecutor taskExecutor;

	@Before
	public void before() {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@Test
	public void testWrite() {
		Metadata meta = writeMeta();

		assertNotNull("metadata should not be null", meta);
		verify(dbMapper, times(1)).save(any(Metadata.class));
	}

	private Metadata writeMeta() {
		CompletableFuture<Metadata> writeResult = underTest.write(new Metadata());
		return writeResult.join();
	}
}

