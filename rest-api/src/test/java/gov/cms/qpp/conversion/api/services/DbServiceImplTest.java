package gov.cms.qpp.conversion.api.services;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import gov.cms.qpp.conversion.api.model.Constants;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class DbServiceImplTest {

	@InjectMocks
	private DbServiceImpl underTest;

	@Mock
	private DynamoDBMapper dbMapper;

	@Mock
	private TaskExecutor taskExecutor;

	@Mock
	private Environment environment;

	@Before
	public void before() {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@Test
	public void testWriteByNull() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn(null);

		Metadata meta = writeMeta();

		assertNotNull("metadata should not be null", meta);
		verify(dbMapper, times(1)).save(any(Metadata.class));
	}

	@Test
	public void testWriteByEmpty() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("");

		Metadata meta = writeMeta();

		assertNotNull("metadata should not be null", meta);
		verify(dbMapper, times(1)).save(any(Metadata.class));
	}

	@Test
	public void testNoWriteBecauseNoAudit() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("trueOrSomething");

		Metadata metadataIn = new Metadata();
		metadataIn.setTin("testTin");

		Metadata metadataOut = writeMeta(metadataIn);

		verifyZeroInteractions(dbMapper);
		assertThat("The returned metadata must be an empty metadata.", metadataOut, is(new Metadata()));
	}

	private Metadata writeMeta() {
		return writeMeta(new Metadata());
	}

	private Metadata writeMeta(Metadata metadata) {
		CompletableFuture<Metadata> writeResult = underTest.write(metadata);
		return writeResult.join();
	}
}

