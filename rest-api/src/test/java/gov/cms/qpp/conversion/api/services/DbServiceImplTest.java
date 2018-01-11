package gov.cms.qpp.conversion.api.services;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DbServiceImplTest {

	@InjectMocks
	private DbServiceImpl underTest;

	@Mock
	private DynamoDBMapper dbMapper;

	@Mock
	private TaskExecutor taskExecutor;

	@Mock
	private Environment environment;

	@BeforeEach
	void before() {
		doAnswer(invocationOnMock -> {
			Runnable method = invocationOnMock.getArgument(0);
			CompletableFuture.runAsync(method);
			return null;
		}).when(taskExecutor).execute(any(Runnable.class));
	}

	@Test
	void testWriteByNull() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn(null);

		Metadata meta = writeMeta();

		assertThat(meta).isNotNull();
		verify(dbMapper, times(1)).save(any(Metadata.class));
	}

	@Test
	void testWriteByEmpty() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("");

		Metadata meta = writeMeta();

		assertThat(meta).isNotNull();
		verify(dbMapper, times(1)).save(any(Metadata.class));
	}

	@Test
	void testNoWriteBecauseNoAudit() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("trueOrSomething");

		Metadata metadataIn = new Metadata();
		metadataIn.setTin("testTin");

		Metadata metadataOut = writeMeta(metadataIn);

		verifyZeroInteractions(dbMapper);
		assertWithMessage("The returned metadata must be an empty metadata.")
				.that(metadataOut.getUuid()).isNull();
	}

	@Test
	void testGetUnprocessedCpcPlusMetaData() {
		QueryResultPage<Metadata> mockMetadataPage = mock(QueryResultPage.class);
		when(mockMetadataPage.getResults()).thenReturn(Lists.newArrayList(new Metadata(), new Metadata()));
		when(dbMapper.queryPage(eq(Metadata.class), any(DynamoDBQueryExpression.class))).thenReturn(mockMetadataPage);

		List<Metadata> metaDataList = underTest.getUnprocessedCpcPlusMetaData();

		verify(dbMapper, times(Constants.CPC_DYNAMO_PARTITIONS)).queryPage(any(Class.class), any(DynamoDBQueryExpression.class));
		assertThat(metaDataList).hasSize(2 * Constants.CPC_DYNAMO_PARTITIONS);
	}

	@Test
	void testGetMetadataById() {
		String fakeUuid = "1337-f4ke-uuid";

		when(dbMapper.load(eq(Metadata.class), anyString())).thenReturn(new Metadata());

		Metadata fakeMetadata = underTest.getMetadataById(fakeUuid);

		verify(dbMapper, times(1)).load(any(Class.class), anyString());

		assertThat(fakeMetadata).isNotNull();
	}

	private Metadata writeMeta() {
		return writeMeta(new Metadata());
	}

	private Metadata writeMeta(Metadata metadata) {
		CompletableFuture<Metadata> writeResult = underTest.write(metadata);
		return writeResult.join();
	}
}

